package com.gnayils.obiew;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.gnayils.obiew.activity.LoginActivity;
import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.weibo.TokenKeeper;
import com.gnayils.obiew.weibo.bean.APIError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 8/13/2016.
 */
public class App extends Application {

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        EventBus.getDefault().register(this);
    }

    public static Context context() {
        return application;
    }

    public static Resources resources() {
        return context().getResources();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAPIError(APIError apiError) {
        switch (apiError.error_code) {
            case APIError.INVALID_ACCESS_TOKEN:
            case APIError.EXPIRED_TOKEN:
                TokenKeeper.clear();
                LoginActivity.start(this);
                break;
            default:
                Log.w("onAPIError", "api error handler cannot deal with this error code: " + apiError.error_code);
                break;
        }
    }
}
