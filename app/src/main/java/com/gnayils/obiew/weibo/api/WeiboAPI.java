package com.gnayils.obiew.weibo.api;

import android.util.Log;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;
import com.gnayils.obiew.weibo.TokenKeeper;
import com.gnayils.obiew.weibo.bean.APIError;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Gnayils on 13/11/2016.
 */

public class WeiboAPI {

    private static final String TAG = WeiboAPI.class.getSimpleName();

    public static final String BASE_URL = App.context().getString(R.string.api_base_url);
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
                                .unsubscribeOn(Schedulers.io())
                                .doOnError(new RetrofitErrorHandler())
                                .observeOn(AndroidSchedulers.mainThread());
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

    private class RetrofitErrorHandler implements Action1<Throwable> {

        @Override
        public void call(Throwable throwable) {
            APIError apiError = null;
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                Converter<ResponseBody, APIError> converter = retrofit.responseBodyConverter(APIError.class, new Annotation[0]);
                try {
                    apiError = converter.convert(httpException.response().errorBody());
                } catch (IOException e) {
                    Log.e(TAG, "convert http response error body to APIError failed", e);
                }
            } else if (throwable instanceof IOException) {
            }
            if(apiError != null) {
                EventBus.getDefault().post(apiError);
            }
        }
    }

    public static class AttachTokenInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();
            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("access_token", TokenKeeper.getToken())
                    .build();
            Request.Builder requestBuilder = original.newBuilder().url(url);
            Request request = requestBuilder.build();
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
