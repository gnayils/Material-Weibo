package com.gnayils.obiew.presenter;

import android.util.Log;

import com.gnayils.obiew.interfaces.RepostInterface;
import com.gnayils.obiew.weibo.api.StatusAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.RepostTimeline;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gnayils on 18/03/2017.
 */

public class RepostPresenter implements RepostInterface.Presenter {

    public static final String TAG = RepostPresenter.class.getSimpleName();

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private RepostInterface.View repostView;

    private long sinceId = 0;

    public RepostPresenter(RepostInterface.View repostView) {
        this.repostView = repostView;
        this.repostView.setPresenter(this);
    }

    @Override
    public void loadRepostTimeline(long statusId, boolean latest) {
        compositeSubscription.clear();
        Subscription subscription = WeiboAPI.get(StatusAPI.class)
                .repostTimeline(statusId, latest ? 0L : sinceId, 0L)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        repostView.showRepostLoadingIndicator(true);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RepostTimeline>() {
                    @Override
                    public void onCompleted() {
                        repostView.showRepostLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        repostView.showRepostLoadingIndicator(false);
                        Log.e(TAG, "update repost time line failed: ", e);
                    }

                    @Override
                    public void onNext(RepostTimeline repostTimeline) {
                        RepostPresenter.this.sinceId = repostTimeline.next_cursor;
                        repostView.show(repostTimeline);
                    }
                });
        compositeSubscription.add(subscription);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
