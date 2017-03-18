package com.gnayils.obiew.presenter;

import android.util.Log;

import com.gnayils.obiew.interfaces.StatusInterface;
import com.gnayils.obiew.weibo.TokenKeeper;
import com.gnayils.obiew.weibo.api.StatusAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.StatusTimeline;

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

    private CompositeSubscription compositeSubscription = new CompositeSubscription();;

    private long sinceId = 0;

    public StatusPresenter(StatusInterface.View statusView) {
        this.statusView = statusView;
        this.statusView.setPresenter(this);
    }

    @Override
    public void loadStatusTimeline(boolean latest) {
        compositeSubscription.clear();
        Subscription subscription = WeiboAPI.get(StatusAPI.class).homeTimeline(TokenKeeper.getToken().getToken(), latest ? 0L : this.sinceId, 0L)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        statusView.showLoadingIndicator(true);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusTimeline>(){

                    @Override
                    public void onCompleted() {
                        statusView.showLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        statusView.showLoadingIndicator(false);
                        Log.e(TAG, "update status time line failed: ", e);
                    }

                    @Override
                    public void onNext(StatusTimeline timeline) {
                        StatusPresenter.this.sinceId = timeline.max_id;
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
