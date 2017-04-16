package com.gnayils.obiew.presenter;

import android.util.Log;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;
import com.gnayils.obiew.interfaces.StatusInterface;
import com.gnayils.obiew.weibo.TokenKeeper;
import com.gnayils.obiew.weibo.api.StatusAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.AccessToken;
import com.gnayils.obiew.weibo.bean.StatusTimeline;
import com.gnayils.obiew.weibo.bean.User;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gnayils on 18/03/2017.
 */

public class StatusPresenter implements StatusInterface.Presenter {

    public static final String TAG = StatusPresenter.class.getSimpleName();

    private StatusInterface.View statusView;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private long homeTimelineSinceId = 0;
    private int userTimelinePage = 1;

    public StatusPresenter(StatusInterface.View statusView) {
        this.statusView = statusView;
        this.statusView.setPresenter(this);
    }

    @Override
    public void loadStatusTimeline(final boolean isLoadingLatest) {
        Subscription subscription = WeiboAPI.get(StatusAPI.class)
                .homeTimeline(isLoadingLatest ? 0L : homeTimelineSinceId, 0L)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, true);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusTimeline>(){

                    @Override
                    public void onCompleted() {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, false);
                        Log.e(TAG, "update status time line failed: ", e);
                    }

                    @Override
                    public void onNext(StatusTimeline timeline) {
                        homeTimelineSinceId = timeline.max_id;
                        statusView.show(timeline);
                    }
                });
        compositeSubscription.add(subscription);
    }

    @Override
    public void loadStatusTimeline(final boolean isLoadingLatest, User user) {
        Subscription subscription = WeiboAPI.get(StatusAPI.class)
                .userTimeline(user.id, 20, userTimelinePage ++ )
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, true);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusTimeline>(){

                    @Override
                    public void onCompleted() {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, false);
                        Log.e(TAG, "update status time line failed: ", e);
                    }

                    @Override
                    public void onNext(StatusTimeline timeline) {
                        statusView.show(timeline);
                    }
                });
        compositeSubscription.add(subscription);
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        compositeSubscription.clear();
    }
}
