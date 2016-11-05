package com.gnayils.obiew;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.gnayils.obiew.bmpldr.BitmapLoader;

/**
 * Created by Administrator on 8/13/2016.
 */
public class App extends Application {

    private static Application application;
    private static String appKey;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        appKey = getString(R.string.weibo_app_key);
        BitmapLoader.initialize();
    }

    public static Context context() {
        return application;
    }

    public static Resources resources() {
        return context().getResources();
    }

    public static String getAppKey() {
        return appKey;
    }

}
