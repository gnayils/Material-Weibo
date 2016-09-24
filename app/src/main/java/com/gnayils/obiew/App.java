package com.gnayils.obiew;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.util.ViewHelper;

/**
 * Created by Administrator on 8/13/2016.
 */
public class App extends Application {

    private static Application INSTANCE;
    private static String APP_KEY;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        APP_KEY = getString(R.string.weibo_app_key);
        BitmapLoader.initialize();
    }

    public static Context context() {
        return INSTANCE;
    }

    public static Resources resources() {
        return INSTANCE.getResources();
    }

    public static String getAppKey() {
        return APP_KEY;
    }

}
