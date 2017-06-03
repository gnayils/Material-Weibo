package com.gnayils.obiew.weibo.service;

import android.util.Log;

import com.gnayils.obiew.presenter.FriendshipPresenter;
import com.gnayils.obiew.weibo.api.FriendShipsAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.Users;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by Gnayils on 02/06/2017.
 */

public class FriendshipService extends BaseService {

    public static final String TAG = FriendshipPresenter.class.getSimpleName();

    public void friends(String uid, SubscriberAdapter<Users> subscriberAdapter) {
        Subscription subscription = WeiboAPI.get(FriendShipsAPI.class)
                .friends(uid)
                .doOnSubscribe(subscriberAdapter.onSubscribeAction)
                .doOnUnsubscribe(subscriberAdapter.onUnsubscribeAction)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriberAdapter);
        addSubscription(subscription);
    }

}
