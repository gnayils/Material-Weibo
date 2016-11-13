package com.gnayils.obiew;

import com.gnayils.obiew.user.UserInterface;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Gnayils on 13/11/2016.
 */

public class WeiboInterface {

    public static final String BASE_URL = App.context().getString(R.string.weibo_base_url);
    public static final int DEFAULT_TIMEOUT = 10;
    private static WeiboInterface instance = new WeiboInterface();

    private Retrofit retrofit;
    private UserInterface userInterface;

    private WeiboInterface() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    public synchronized UserInterface getUserInterface() {
        if(userInterface == null) {
            userInterface = retrofit.create(UserInterface.class);
        }
        return userInterface;
    }

    public static WeiboInterface getInstance() {
        return instance;
    }

}
