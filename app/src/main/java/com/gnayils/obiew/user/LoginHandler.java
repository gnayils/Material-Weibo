package com.gnayils.obiew.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;
import com.gnayils.obiew.util.TokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.Tag;
import com.sina.weibo.sdk.openapi.models.User;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 8/20/2016.
 */
public class LoginHandler {

    private static final String TAG = LoginHandler.class.getSimpleName();

    private LoginView loginView;

    private AuthInfo authInfo =  new AuthInfo(App.context(), App.context().getString(R.string.weibo_app_key), App.context().getString(R.string.weibo_redirect_url), App.context().getString(R.string.weibo_scope));
    private SsoHandler ssoHandler;

    public LoginHandler(LoginView loginView) {
        this.loginView = loginView;
    }

    public void login(Activity activity) {
        ssoHandler = new SsoHandler(activity, authInfo);
        ssoHandler.authorize(new AuthListener());
    }

    public void signup(Activity activity) {
        ssoHandler = new SsoHandler(activity, authInfo);
        ssoHandler.registerOrLoginByMobile(App.context().getString(R.string.register_activity_title), new AuthListener());
    }

    public void authorizeCallBack(int requestCode, int resultCode, Intent data) {
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken.isSessionValid()) {
                Log.i(TAG, "access token: " + accessToken.getToken());
                TokenKeeper.writeToken(accessToken);
                UsersAPI usersApi = new UsersAPI(App.context(), App.getAppKey(), accessToken);
                usersApi.show(Long.parseLong(accessToken.getUid()), new RequestListener() {
                    @Override
                    public void onComplete(String s) {
                        User user = User.parse(s);
                        loginView.updateUser(user);
                        Log.i(TAG, s);
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                });

            } else {
                Log.e(TAG, values.getString("code"));
            }
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "login cancelled");
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Log.i(TAG, "login exception: " + e.getMessage());
        }
    }
}
