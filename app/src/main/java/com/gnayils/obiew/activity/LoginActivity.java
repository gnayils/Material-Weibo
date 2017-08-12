package com.gnayils.obiew.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.gnayils.obiew.Obiew;
import com.gnayils.obiew.Preferences;
import com.gnayils.obiew.R;
import com.gnayils.obiew.util.Popup;
import com.gnayils.obiew.util.URLParser;
import com.gnayils.obiew.weibo.Account;
import com.gnayils.obiew.weibo.bean.AccessToken;
import com.gnayils.obiew.weibo.bean.Groups;
import com.gnayils.obiew.weibo.bean.User;
import com.gnayils.obiew.weibo.service.AuthorizeService;
import com.gnayils.obiew.weibo.service.FriendshipService;
import com.gnayils.obiew.weibo.service.SubscriberAdapter;
import com.gnayils.obiew.weibo.service.UserService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;


public class LoginActivity extends BaseActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.web_view)
    WebView webView;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private MaterialDialog progressDialog;

    private String appKey = Obiew.getAppResources().getString(R.string.app_key);
    private String callbackUrl = Obiew.getAppResources().getString(R.string.callback_url);
    private String authorizationUrl = String.format(Obiew.getAppResources().getString(R.string.auth_url), appKey, Obiew.getAppResources().getString(R.string.auth_scope), callbackUrl);

    private String username;
    private String password;

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
        webView.addJavascriptInterface(new AccountGrabber(), "AccountGrabberJS");
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
        webView.setWebChromeClient(new WebChromeClient() {

            boolean fillAccountFinished = false;

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    String[] account = Preferences.getAccount();
                    if(account[0] != null && !account[0].isEmpty() && account[1] != null && !account[1].isEmpty()) {
                        if (!fillAccountFinished && !view.getUrl().isEmpty() && view.getUrl().equalsIgnoreCase("about:blank")) {
                            webView.loadUrl("javascript:fillAccount()");
                            fillAccountFinished = true;
                        }
                    }
                }
                super.onProgressChanged(view, newProgress);
            }

        });

        new JSInjectionTask().execute();
    }

    class AccountGrabber {

        @JavascriptInterface
        public void grabAccount(String username, String password) {
            LoginActivity.this.username = username;
            LoginActivity.this.password = password;
        }
    }

    class JSInjectionTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            int attemptCount = 3;
            String[] account = Preferences.getAccount();
            while (attemptCount-- > -1) {
                try {
                    String accountHelperJS = readAssetsFile("AccountHelper.js", LoginActivity.this);
                    accountHelperJS = accountHelperJS.replace("%username%", account[0] == null ? "" : account[0]).replace("%password%", account[1] == null ? "" :  account[1]);

                    Document dom = Jsoup.connect(authorizationUrl).get();
                    String html = dom.toString();
                    html = html.replace("<html>", "<html id='all' >")
                            .replace("</head>", accountHelperJS + "</head>")
                            .replace("action-type=\"submit\"", "action-type=\"submit\" id=\"submit\"");

                    dom = Jsoup.parse(html);
                    Element inputUsername = dom.select("input#userId").first();
                    inputUsername.attr("oninput", "grabAccount()");

                    Element pwdAccount = dom.select("input#passwd").first();
                    pwdAccount.attr("oninput", "grabAccount()");

                    html = dom.toString();

                    return html;
                } catch (Exception e) {
                    Log.e(TAG, "inject js to html failed", e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String html) {
            if(html == null) {
                Popup.errorDialog("错误", "加载网页失败, 请稍后再试...");
            } else {
                webView.loadDataWithBaseURL("https://api.weibo.com", html, "text/html", "UTF-8", "");
            }
        }

        String readAssetsFile(String file, Context context) {
            StringBuffer stringBuffer = new StringBuffer();
            try {
                InputStream e = context.getResources().getAssets().open(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(e, "UTF-8"));
                String readLine = null;
                while ((readLine = reader.readLine()) != null) {
                    stringBuffer.append(readLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stringBuffer.toString();
        }
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
                    if(username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                        Preferences.saveAccount(username, password);
                    }
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

