package com.gnayils.obiew.presenter;

import android.util.Log;

import com.gnayils.obiew.interfaces.StatusInterface;
import com.gnayils.obiew.weibo.TextDecorator;
import com.gnayils.obiew.weibo.VideoURLFinder;
import com.gnayils.obiew.weibo.api.StatusAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.StatusTimeline;
import com.gnayils.obiew.weibo.bean.User;

import java.util.Arrays;

import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gnayils on 18/03/2017.
 */

public class StatusPresenter implements StatusInterface.Presenter {

    public static final String TAG = StatusPresenter.class.getSimpleName();

    private StatusInterface.View statusView;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private long homeTimelineSinceId = 0;
    private int[] userTimelinePages = new int[Status.FEATURES_COUNT];

    public StatusPresenter(StatusInterface.View statusView) {
        this.statusView = statusView;
        this.statusView.setPresenter(this);
        Arrays.fill(userTimelinePages, 1);
    }

    @Override
    public void loadStatusTimeline(final boolean isLoadingLatest) {
        Subscription subscription = WeiboAPI.get(StatusAPI.class)
                .homeTimeline(isLoadingLatest ? 0L : homeTimelineSinceId, 0L)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("doOnSubscribe : " + Thread.currentThread().getName());
                        statusView.showStatusLoadingIndicator(isLoadingLatest, true);
                    }
                })
                .doOnNext(new Action1<StatusTimeline>() {
                    @Override
                    public void call(StatusTimeline statusTimeline) {
                        System.out.println("first doOnNext : " + Thread.currentThread().getName());
                        VideoURLFinder.find(statusTimeline);
                    }
                })
                .doOnNext(new Action1<StatusTimeline>() {
                    @Override
                    public void call(StatusTimeline statusTimeline) {
                        System.out.println("doOnNext : " + Thread.currentThread().getName());
                        TextDecorator.decorate(statusTimeline);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
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
                        System.out.println("subscribe : " + Thread.currentThread().getName());
                        homeTimelineSinceId = timeline.max_id;
                        statusView.show(timeline, Status.FEATURE_ALL);
                    }
                });
        compositeSubscription.add(subscription);
    }

    @Override
    public void loadStatusTimeline(final boolean isLoadingLatest, User user, final int feature) {
        Subscription subscription = WeiboAPI.get(StatusAPI.class)
                .userTimeline(user.id, feature, 20, userTimelinePages[feature] ++)
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
                        statusView.show(timeline, feature);
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
