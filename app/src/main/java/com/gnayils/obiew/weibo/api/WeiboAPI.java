package com.gnayils.obiew.weibo.api;

import android.util.Log;

import com.gnayils.obiew.Obiew;
import com.gnayils.obiew.R;
import com.gnayils.obiew.weibo.APIErrorHandler;
import com.gnayils.obiew.weibo.Account;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Gnayils on 13/11/2016.
 */

public class WeiboAPI {

    private static final String TAG = WeiboAPI.class.getSimpleName();

    public static final String APP_KEY = Obiew.getAppResources().getString(R.string.app_key);
    public static final String BASE_URL = Obiew.getAppResources().getString(R.string.api_base_url);
    public static final int DEFAULT_TIMEOUT = 10;
    private static WeiboAPI instance = new WeiboAPI();

    private Retrofit retrofit;
    private Map<String, Object> interfaceMap = new HashMap<String, Object>();

    private WeiboAPI() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        httpClientBuilder.addInterceptor(new AttachTokenInterceptor());
        httpClientBuilder.addInterceptor(loggingInterceptor);
        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    public synchronized <T> T getInterface(final Class<T> clazz) {
        T interfaceInstance = (T) interfaceMap.get(clazz.getName());
        if(interfaceInstance == null) {
            interfaceInstance = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler(){

                private T retrofitInstance = retrofit.create(clazz);

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Object result =  method.invoke(retrofitInstance, args);
                    if(result instanceof Observable) {
                        return ((Observable) result)
                                .subscribeOn(Schedulers.io())
                                .doOnError(new APIErrorHandler(retrofit));
                    } else {
                        Log.e(TAG, "all method in the retrofit instance must be return a Observable instance");
                        return null;
                    }
                }
            });
            interfaceMap.put(clazz.getName(), interfaceInstance);
        }
        return interfaceInstance;
    }

    public static class AttachTokenInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if(Account.accessToken != null) {

                HttpUrl httpUrl = request.url();
                HttpUrl url = httpUrl.newBuilder()
                        .addQueryParameter("access_token", Account.accessToken.access_token)
                        .addQueryParameter("source", APP_KEY)
                        .build();
                Headers headers = request.headers();
                headers.newBuilder().add("Authorization", "OAuth2 " + Account.accessToken.access_token);

                Request.Builder requestBuilder = request.newBuilder().headers(headers).url(url);
                request = requestBuilder.build();
            }
            return chain.proceed(request);
        }
    }

    public static synchronized <T> T getAPI(final Class<T> clazz) {
        return instance.retrofit.create(clazz);
    }

    public static <T> T get(Class<T> clazz) {
        return instance.getInterface(clazz);
    }
}
