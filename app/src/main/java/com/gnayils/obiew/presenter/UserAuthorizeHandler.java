package com.gnayils.obiew.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;
import com.gnayils.obiew.event.AuthorizeCallBackEvent;
import com.gnayils.obiew.event.AuthorizeResultEvent;
import com.gnayils.obiew.weibo.TokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 8/20/2016.
 */
public class UserAuthorizeHandler {

    private static final String TAG = UserAuthorizeHandler.class.getSimpleName();

    private AuthInfo authInfo = new AuthInfo(App.context(), App.context().getString(R.string.weibo_app_key), App.context().getString(R.string.weibo_redirect_url), App.context().getString(R.string.weibo_scope));
    private SsoHandler ssoHandler;

    public void requestLogin(Activity activity) {
        EventBus.getDefault().register(this);
        ssoHandler = new SsoHandler(activity, authInfo);
        ssoHandler.authorize(new AuthListener());
    }

    public void requestSignup(Activity activity) {
        EventBus.getDefault().register(this);
        ssoHandler = new SsoHandler(activity, authInfo);
        ssoHandler.registerOrLoginByMobile(App.context().getString(R.string.register_activity_title), new AuthListener());
    }

    private static class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken != null && accessToken.isSessionValid()) {
                Log.d(TAG, "access token: " + accessToken.getToken());
                TokenKeeper.writeToken(accessToken);
                EventBus.getDefault().post(new AuthorizeResultEvent(true, false, false, null));
            } else {
                Log.e(TAG, "authorize request failed: " + values.getString("code"));
                EventBus.getDefault().post(new AuthorizeResultEvent(false, false, true, values.getString("code")));
            }
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "authorize request cancelled");
            EventBus.getDefault().post(new AuthorizeResultEvent(false, true, false, null));
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Log.e(TAG, "authorize request exception: " + e.getMessage());
            EventBus.getDefault().post(new AuthorizeResultEvent(false, false, true, e.getMessage()));
        }
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onActivityResult(AuthorizeCallBackEvent event) {
        ssoHandler.authorizeCallBack(event.requestCode, event.resultCode, event.data);
        EventBus.getDefault().unregister(this);
    }
}
