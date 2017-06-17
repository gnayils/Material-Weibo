package com.gnayils.obiew.weibo.service;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gnayils.obiew.Obiew;
import com.gnayils.obiew.R;
import com.gnayils.obiew.weibo.Account;
import com.gnayils.obiew.weibo.api.AuthorizeAPI;
import com.gnayils.obiew.weibo.api.FriendShipsAPI;
import com.gnayils.obiew.weibo.api.UserAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.AccessToken;
import com.gnayils.obiew.weibo.bean.Groups;
import com.gnayils.obiew.weibo.bean.User;

import rx.Subscriber;
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
