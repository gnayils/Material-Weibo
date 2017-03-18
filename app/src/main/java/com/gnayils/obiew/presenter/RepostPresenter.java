package com.gnayils.obiew.presenter;

import android.util.Log;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;
import com.gnayils.obiew.interfaces.CommentInterface;
import com.gnayils.obiew.interfaces.RepostInterface;
import com.gnayils.obiew.weibo.TokenKeeper;
import com.gnayils.obiew.weibo.api.CommentAPI;
import com.gnayils.obiew.weibo.api.StatusAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.CommentTimeline;
import com.gnayils.obiew.weibo.bean.RepostTimeline;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;

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

    private StatusesAPI statusesAPI;

    public RepostPresenter(RepostInterface.View repostView) {
        this.repostView = repostView;
        this.repostView.setPresenter(this);
        statusesAPI = new StatusesAPI(App.context(), App.resources().getString(R.string.weibo_app_key), TokenKeeper.getToken());
    }

    @Override
    public void loadRepost(long statusId, boolean latest) {
        compositeSubscription.clear();

        statusesAPI.repostTimeline(statusId, latest ? 0L : sinceId, 0L, 20, 1, StatusesAPI.AUTHOR_FILTER_ALL, new RequestListener() {
            @Override
            public void onComplete(String s) {
                Log.d(TAG, s);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        });


//        Subscription subscription = WeiboAPI.get(StatusAPI.class)
//                .repostTimeline(TokenKeeper.getToken().getToken(), statusId, latest ? 0L : sinceId, 0L)
//                .doOnSubscribe(new Action0() {
//                    @Override
//                    public void call() {
//                        repostView.showLoadingIndicator(true);
//                    }
//                })
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<RepostTimeline>() {
//                    @Override
//                    public void onCompleted() {
//                        repostView.showLoadingIndicator(false);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        repostView.showLoadingIndicator(false);
//                        Log.e(TAG, "update repost time line failed: ", e);
//                    }
//
//                    @Override
//                    public void onNext(RepostTimeline repostTimeline) {
//                        RepostPresenter.this.sinceId = repostTimeline.max_id;
//                        repostView.show(repostTimeline);
//                    }
//                });
//        compositeSubscription.add(subscription);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
