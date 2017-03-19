package com.gnayils.obiew.presenter;

import android.util.Log;

import com.gnayils.obiew.interfaces.CommentInterface;
import com.gnayils.obiew.weibo.TokenKeeper;
import com.gnayils.obiew.weibo.api.CommentAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.CommentTimeline;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gnayils on 12/03/2017.
 */

public class CommentPresenter implements CommentInterface.Presenter {

    public static final String TAG = CommentPresenter.class.getSimpleName();

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private CommentInterface.View commentView;
    private long sinceId = 0;

    public CommentPresenter(CommentInterface.View commentView)  {
        this.commentView = commentView;
        this.commentView.setPresenter(this);
    }

    @Override
    public void loadComment(long statusId, boolean latest) {
        compositeSubscription.clear();
        Subscription subscription = WeiboAPI.get(CommentAPI.class)
                .show(TokenKeeper.getToken().access_token, statusId, latest ? 0L : sinceId, 0L)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        commentView.showLoadingIndicator(true);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommentTimeline>() {
                    @Override
                    public void onCompleted() {
                        commentView.showLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        commentView.showLoadingIndicator(false);
                        Log.e(TAG, "update comment time line failed: ", e);
                    }

                    @Override
                    public void onNext(CommentTimeline commentTimeline) {
                        CommentPresenter.this.sinceId = commentTimeline.max_id;
                        commentView.show(commentTimeline);
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
