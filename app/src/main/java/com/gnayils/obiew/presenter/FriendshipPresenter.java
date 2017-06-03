package com.gnayils.obiew.presenter;

import android.util.Log;

import com.gnayils.obiew.interfaces.FriendshipInterface;
import com.gnayils.obiew.weibo.api.FriendShipsAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.Users;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gnayils on 12/11/2016.
 */
@Deprecated
public class FriendshipPresenter implements FriendshipInterface.Presenter {

    public static final String TAG = FriendshipPresenter.class.getSimpleName();

    private FriendshipInterface.View friendshipView;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();


    public FriendshipPresenter(FriendshipInterface.View friendshipView) {
        this.friendshipView = friendshipView;
        this.friendshipView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        compositeSubscription.clear();
    }


    @Override
    public void friends(String uid) {
        Subscription subscription = WeiboAPI.get(FriendShipsAPI.class)
                .friends(uid)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        friendshipView.setLoadingIndicatorVisible(true);
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        friendshipView.setLoadingIndicatorVisible(false);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Users>(){

                    @Override
                    public void onCompleted() {
                        friendshipView.setLoadingIndicatorVisible(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        friendshipView.setLoadingIndicatorVisible(false);
                        Log.e(TAG, "get friendship failed: ", e);
                    }

                    @Override
                    public void onNext(Users users) {
                        friendshipView.show(users);
                    }
                });
        compositeSubscription.add(subscription);
    }
}
