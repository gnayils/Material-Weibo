package com.gnayils.obiew.activity;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.URLParser;
import com.gnayils.obiew.weibo.TokenKeeper;
import com.gnayils.obiew.weibo.api.AuthorizeAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.AccessToken;

import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;


public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.web_view)
    protected WebView webView;

    private String authorizationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        final String appKey = getString(R.string.weico_app_key);
        final String appSecret = getString(R.string.weico_app_secret);
        final String callbackUrl = getString(R.string.weico_callback_url);
        String authorizeUrl = getString(R.string.auth_url);
        String authorizeScope = getString(R.string.auth_scope);
        final String finalAuthorizeUrl = String.format(authorizeUrl, appKey, authorizeScope, callbackUrl);

        Log.d(TAG, "final authorize url: " + finalAuthorizeUrl);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading: " + url);
                if (url != null && url.startsWith(callbackUrl)) {
                    try {
                        authorizationCode = URLParser.decode(new URL(url)).get("code").get(0);
                        WeiboAPI.get(AuthorizeAPI.class)
                                .accessToken(appKey, appSecret, "authorization_code", authorizationCode, callbackUrl)
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
                                        TokenKeeper.writeToken(accessToken);
                                        LoginActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MainActivity.start(LoginActivity.this);
                                                LoginActivity.this.finish();
                                            }
                                        });
                                    }
                                });
                        Log.d(TAG, "authorization code: " + authorizationCode);
                    } catch (Exception e) {
                        Log.e(TAG, "get the authorization code from the callback url failed", e);
                    }
                    return true;
                }
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.d(TAG, "new progress: " + newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(finalAuthorizeUrl);
            }
        });
    }

}

