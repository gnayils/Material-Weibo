package com.gnayils.obiew.weibo.service;

import com.gnayils.obiew.weibo.api.UserAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.User;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Gnayils on 03/06/2017.
 */

public class UserService extends BaseService {

    public void showUserById(long uid, SubscriberAdapter<User> subscriberAdapter) {
        Subscription subscription = WeiboAPI.get(UserAPI.class).showById(uid)
                .doOnSubscribe(subscriberAdapter.onSubscribeAction)
                .doOnUnsubscribe(subscriberAdapter.onUnsubscribeAction)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriberAdapter);
        addSubscription(subscription);
    }

    public void showUserByName(String screenName, SubscriberAdapter<User> subscriberAdapter) {
        Subscription subscription = WeiboAPI.get(UserAPI.class).showByName(screenName)
                .doOnSubscribe(subscriberAdapter.onSubscribeAction)
                .doOnUnsubscribe(subscriberAdapter.onUnsubscribeAction)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriberAdapter);
        addSubscription(subscription);
    }
}
