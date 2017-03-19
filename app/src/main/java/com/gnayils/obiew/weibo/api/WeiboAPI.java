package com.gnayils.obiew.weibo.api;

import android.util.Log;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
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
        httpClientBuilder.interceptors().add(new LoggingInterceptor());
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

    public static class LoggingInterceptor implements Interceptor  {

        public static final String TAG = LoggingInterceptor.class.getSimpleName();

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            String requestLog = String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers());
            if(request.method().compareToIgnoreCase("post") == 0){
                Buffer buffer = new Buffer();
                request.newBuilder().build().body().writeTo(buffer);
                requestLog ="\n" + requestLog + "\n" + buffer.readUtf8();
            }
            Log.d(TAG, "Request: " + "\n" + requestLog);

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            String responseLog = String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers());
            String bodyString = response.body().string();
            Log.d(TAG,"Response: " + "\n" + responseLog + "\n" + bodyString);

            return response.newBuilder().body(ResponseBody.create(response.body().contentType(), bodyString)).build();
        }
    }

    public static synchronized <T> T getAPI(final Class<T> clazz) {
        return instance.retrofit.create(clazz);
    }

    public static <T> T get(Class<T> clazz) {
        return instance.getInterface(clazz);
    }
}
