package com.gnayils.obiew.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gnayils.obiew.R;
import com.gnayils.obiew.bmpldr.BitmapLoadAdapter;
import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.bmpldr.BitmapLoadListener;
import com.gnayils.obiew.util.URLParser;
import com.gnayils.obiew.view.AvatarView;
import com.gnayils.obiew.weibo.LoginUser;
import com.gnayils.obiew.weibo.TokenKeeper;
import com.gnayils.obiew.weibo.api.AuthorizeAPI;
import com.gnayils.obiew.weibo.api.UserAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.AccessToken;
import com.gnayils.obiew.weibo.bean.User;

import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;


public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.web_view)
    protected WebView webView;
    @Bind(R.id.avatar_view)
    protected AvatarView avatarView;

    private String appKey;
    private String appSecret;
    private String callbackUrl;
    private String authorizationUrl;
    public static final int MESSAGE_AUTHORIZATION_CODE_OBTAINED = 0;
    public static final int MESSAGE_ACCESS_TOKEN_OBTAINED = 1;
    public static final int MESSAGE_LOGIN_USER_OBTAINED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        String accessToken = TokenKeeper.getToken();
        if (accessToken != null && !accessToken.isEmpty()) {
            webView.setVisibility(View.INVISIBLE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainActivity.start(LoginActivity.this);
                    finish();
                }
            }, 500);
            return;
        }

        appKey = getString(R.string.weico_app_key);
        appSecret = getString(R.string.weico_app_secret);
        callbackUrl = getString(R.string.weico_callback_url);
        authorizationUrl = String.format(getString(R.string.auth_url), appKey, getString(R.string.auth_scope), callbackUrl);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith(callbackUrl)) {
                    try {
                        String authorizationCode = URLParser.decode(new URL(url)).get("code").get(0);
                        Message message = handler.obtainMessage(MESSAGE_AUTHORIZATION_CODE_OBTAINED, authorizationCode);
                        handler.dispatchMessage(message);
                    } catch (Exception e) {
                        Log.e(TAG, "get the authorization code from the callback url failed", e);
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(webView, url);
            }
        });

        webView.loadUrl(authorizationUrl);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_AUTHORIZATION_CODE_OBTAINED) {
                WeiboAPI.get(AuthorizeAPI.class)
                        .accessToken(appKey, appSecret, "authorization_code", msg.obj.toString(), callbackUrl)
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                webView.animate().alpha(0.0f).setDuration(300)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                webView.setVisibility(View.INVISIBLE);
                                            }
                                        }).start();
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<AccessToken>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "get access token failed", e);
                            }

                            @Override
                            public void onNext(AccessToken accessToken) {
                                TokenKeeper.write(accessToken);
                                dispatchMessage(obtainMessage(MESSAGE_ACCESS_TOKEN_OBTAINED));
                            }
                        });
            } else if (msg.what == MESSAGE_ACCESS_TOKEN_OBTAINED) {
                WeiboAPI.get(UserAPI.class)
                        .show(TokenKeeper.getUid())
                        .subscribe(new Subscriber<User>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "get login user failed", e);
                            }

                            @Override
                            public void onNext(User user) {
                                dispatchMessage(obtainMessage(MESSAGE_LOGIN_USER_OBTAINED));
                            }
                        });
            } else if(msg.what == MESSAGE_LOGIN_USER_OBTAINED) {

                BitmapLoader.getInstance().loadBitmap(LoginUser.getUser().avatar_large, avatarView.avatarCircleImageView, new BitmapLoadAdapter() {

                    @Override
                    public void onPostLoad(Bitmap bitmap) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.start(LoginActivity.this);
                                LoginActivity.this.finish();
                            }
                        }, 2000);
                    }
                });
            }
        }
    };

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        context.startActivity(intent);
    }
}

