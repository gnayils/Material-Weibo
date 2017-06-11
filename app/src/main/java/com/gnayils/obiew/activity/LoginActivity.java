package com.gnayils.obiew.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.gnayils.obiew.R;
import com.gnayils.obiew.util.URLParser;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.view.AvatarView;
import com.gnayils.obiew.weibo.Account;
import com.gnayils.obiew.weibo.api.AuthorizeAPI;
import com.gnayils.obiew.weibo.api.FriendShipsAPI;
import com.gnayils.obiew.weibo.api.UserAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.AccessToken;
import com.gnayils.obiew.weibo.bean.Groups;
import com.gnayils.obiew.weibo.bean.User;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.concurrent.Semaphore;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;


public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.web_view)
    WebView webView;
    @Bind(R.id.avatar_view)
    AvatarView avatarView;

    @Bind(R.id.frame_layout_bottom_part)
    FrameLayout bottomPartFrameLayout;
    @Bind(R.id.frame_layout_top_part)
    FrameLayout topPartFrameLayout;

    private String appKey;
    private String appSecret;
    private String callbackUrl;
    private String authorizationUrl;
    public static final int MESSAGE_AUTHORIZATION_CODE_OBTAINED = 0;
    public static final int MESSAGE_ACCESS_TOKEN_OBTAINED = 1;
    public static final int MESSAGE_LOGIN_USER_OBTAINED = 2;
    public static final int MESSAGE_FRIEND_GROUPS_OBTAINED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setupBackground();

        if (Account.loadCache(this)) {
            webView.setVisibility(View.INVISIBLE);
            Bitmap avatar = BitmapFactory.decodeByteArray(Account.user.avatarBytes, 0, Account.user.avatarBytes.length);
            avatarView.avatarCircleImageView.setImageBitmap(avatar);
            handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_LOGIN_USER_OBTAINED), 1000);
            return;
        }

        appKey = getString(R.string.app_key);
        appSecret = getString(R.string.app_secret);
        callbackUrl = getString(R.string.callback_url);
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
                        .observeOn(AndroidSchedulers.mainThread())
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
                                Account.accessToken = accessToken;
                                dispatchMessage(obtainMessage(MESSAGE_ACCESS_TOKEN_OBTAINED));
                            }
                        });
            } else if (msg.what == MESSAGE_ACCESS_TOKEN_OBTAINED) {
                WeiboAPI.get(UserAPI.class)
                        .showById(Account.accessToken.uid)
                        .doOnNext(new Action1<User>() {
                            @Override
                            public void call(User user) {
                                try {
                                    Account.user = user;
                                    Bitmap avatar = Glide.with(LoginActivity.this)
                                            .load(Account.user.avatar_large).asBitmap().into(-1, -1).get();
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    if (avatar.compress(Bitmap.CompressFormat.PNG, 100, baos)) {
                                        Account.user.avatarBytes = baos.toByteArray();
                                    }
                                } catch (Exception e) {
                                    throw Exceptions.propagate(e);
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
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
                                Bitmap avatar = BitmapFactory.decodeByteArray(Account.user.avatarBytes, 0, Account.user.avatarBytes.length);
                                avatarView.avatarCircleImageView.setImageBitmap(avatar);
                                dispatchMessage(obtainMessage(MESSAGE_LOGIN_USER_OBTAINED));
                            }
                        });
            } else if (msg.what == MESSAGE_LOGIN_USER_OBTAINED) {
                WeiboAPI.get(FriendShipsAPI.class).groups()
                        .doOnNext(new Action1<Groups>() {
                            @Override
                            public void call(Groups groups) {
                                Account.groups = groups;
                                Account.cache(LoginActivity.this);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Groups>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "get friend groups failed", e);
                            }

                            @Override
                            public void onNext(Groups groups) {
                                sendMessageDelayed(obtainMessage(MESSAGE_FRIEND_GROUPS_OBTAINED), 1000);
                            }
                        });
            } else if (msg.what == MESSAGE_FRIEND_GROUPS_OBTAINED) {
                MainActivity.start(LoginActivity.this);
                LoginActivity.this.finish();
            }
        }
    };

    private void setupBackground() {
        Size size = ViewUtils.getScreenSize(this);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(new int[]{
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorPrimary)
        });
        gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        gradientDrawable.setCornerRadii(new float[]{
                0, 0, 0, 0, size.getWidth() / 2, size.getWidth() / 3, size.getWidth() / 2, size.getWidth() / 3
        });
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        topPartFrameLayout.setBackground(gradientDrawable);

        gradientDrawable = new GradientDrawable();
        gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        gradientDrawable.setColors(new int[]{
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark)
        });
        gradientDrawable.setCornerRadii(new float[]{
                size.getWidth() / 2, size.getWidth() / 4, size.getWidth() / 2, size.getWidth() / 4, 0, 0, 0, 0
        });
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        bottomPartFrameLayout.setBackground(gradientDrawable);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        context.startActivity(intent);
    }
}

