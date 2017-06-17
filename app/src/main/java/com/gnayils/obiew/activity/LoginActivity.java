package com.gnayils.obiew.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.gnayils.obiew.Obiew;
import com.gnayils.obiew.R;
import com.gnayils.obiew.util.Popup;
import com.gnayils.obiew.util.URLParser;
import com.gnayils.obiew.weibo.Account;
import com.gnayils.obiew.weibo.bean.AccessToken;
import com.gnayils.obiew.weibo.bean.Group;
import com.gnayils.obiew.weibo.bean.Groups;
import com.gnayils.obiew.weibo.bean.User;
import com.gnayils.obiew.weibo.bean.Users;
import com.gnayils.obiew.weibo.service.AuthorizeService;
import com.gnayils.obiew.weibo.service.FriendshipService;
import com.gnayils.obiew.weibo.service.SubscriberAdapter;
import com.gnayils.obiew.weibo.service.UserService;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.web_view)
    WebView webView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private MaterialDialog progressDialog;

    private String appKey = Obiew.getAppResources().getString(R.string.app_key);
    private String callbackUrl = Obiew.getAppResources().getString(R.string.callback_url);
    private String authorizationUrl = String.format(Obiew.getAppResources().getString(R.string.auth_url), appKey, Obiew.getAppResources().getString(R.string.auth_scope), callbackUrl);

    private AuthorizeService authorizeService = new AuthorizeService();
    private UserService userService = new UserService();
    private FriendshipService friendshipService = new FriendshipService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
                        progressDialog = Popup.indeterminateProgressDialog("用户登录", "正在请求授权...", false);
                        String authorizationCode = URLParser.decode(new URL(url)).get("code").get(0);
                        getAccessToken(authorizationCode);
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

    private void getAccessToken(String authorizationCode) {
        authorizeService.getAccessToken(authorizationCode, new SubscriberAdapter<AccessToken>() {

            @Override
            public void onError(Throwable e) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Popup.toast("登录失败: " + e.getMessage());
            }

            @Override
            public void onNext(AccessToken accessToken) {
                Account.accessToken = accessToken;
                getUser(accessToken);
            }
        });
    }

    private void getUser(AccessToken accessToken) {
        userService.showUserById(accessToken.uid, new SubscriberAdapter<User>() {

            @Override
            public void onError(Throwable e) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Popup.toast("获取用户信息失败: " + e.getMessage());
            }

            @Override
            public void onNext(User user) {
                Account.user = user;
                getFriendGroups();
            }
        });
    }

    private void getFriendGroups() {
        friendshipService.groups(new SubscriberAdapter<Groups>() {

            @Override
            public void onError(Throwable e) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Popup.toast("获取用户好友分组失败: " + e.getMessage());
            }

            @Override
            public void onNext(Groups groups) {
                Account.groups = groups;
                cacheAccount();
            }
        });
    }

    private void cacheAccount() {
        new AsyncTask<Void, Void, Boolean>() {

            Exception failedCause;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Bitmap avatar = Glide.with(LoginActivity.this).load(Account.user.avatar_large).asBitmap().into(-1, -1).get();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if (avatar.compress(Bitmap.CompressFormat.PNG, 100, baos)) {
                        Account.user.avatarBytes = baos.toByteArray();
                    }
                    Account.cache(LoginActivity.this);
                    return true;
                } catch (Exception e) {
                    failedCause = e;
                    Log.e(TAG, "cache account failed", e);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean isSuccessful) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (!isSuccessful) {
                    Popup.toast("用户登录失败" + (failedCause != null ? ": " + failedCause.getMessage() : ""));
                }
                LoginActivity.this.finish();
            }

        }.execute();
    }


    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        context.startActivity(intent);
    }
}

