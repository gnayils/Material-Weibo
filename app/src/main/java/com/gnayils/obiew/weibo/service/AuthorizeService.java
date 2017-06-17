package com.gnayils.obiew.weibo.service;

import com.gnayils.obiew.Obiew;
import com.gnayils.obiew.R;
import com.gnayils.obiew.weibo.api.AuthorizeAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.AccessToken;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Gnayils on 15/06/2017.
 */

public class AuthorizeService extends BaseService {

    public static final String TAG = UserService.class.getSimpleName();

    public static final int MESSAGE_GET_ACCESS_TOKEN = 0;
    public static final int MESSAGE_GET_USER = 1;
    public static final int MESSAGE_GET_GROUP = 2;
    public static final int MESSAGE_ALL_GOT = 3;

    private String appKey = Obiew.getAppResources().getString(R.string.app_key);
    private String appSecret = Obiew.getAppResources().getString(R.string.app_secret);
    private String callbackUrl = Obiew.getAppResources().getString(R.string.callback_url);

    public void getAccessToken(String authorizationCode, SubscriberAdapter<AccessToken> subscriberAdapter) {
        Subscription subscription = WeiboAPI.get(AuthorizeAPI.class)
                .accessToken(appKey, appSecret, "authorization_code", authorizationCode, callbackUrl)
                .doOnSubscribe(subscriberAdapter.onSubscribeAction)
                .doOnUnsubscribe(subscriberAdapter.onUnsubscribeAction)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriberAdapter);
        addSubscription(subscription);

        Observable<AccessToken> observable = WeiboAPI.get(AuthorizeAPI.class)
                .accessToken(appKey, appSecret, "authorization_code", authorizationCode, callbackUrl);
        subscriberAdapter.setupTo(observable);

    }

}
