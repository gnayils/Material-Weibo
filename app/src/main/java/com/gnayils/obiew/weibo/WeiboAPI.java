package com.gnayils.obiew.weibo;

import android.util.Log;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
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

    public static final String BASE_URL = App.context().getString(R.string.weibo_base_url);
    public static final int DEFAULT_TIMEOUT = 10;
    private static WeiboAPI instance = new WeiboAPI();

    private Retrofit retrofit;
    private Map<String, Object> interfaceMap = new HashMap<String, Object>();

    private WeiboAPI() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
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

    public static synchronized <T> T getAPI(final Class<T> clazz) {
        return instance.retrofit.create(clazz);
    }

    public static <T> T get(Class<T> clazz) {
        return instance.getInterface(clazz);
    }
}
