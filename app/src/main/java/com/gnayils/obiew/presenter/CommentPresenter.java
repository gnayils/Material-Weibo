package com.gnayils.obiew.presenter;

import android.util.Log;

import com.gnayils.obiew.interfaces.CommentInterface;
import com.gnayils.obiew.weibo.TextDecorator;
import com.gnayils.obiew.weibo.api.CommentAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.Comments;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gnayils on 12/03/2017.
 */
@Deprecated
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
    public void loadCommentTimeline(long statusId, final boolean isLoadingLatest) {
        compositeSubscription.clear();
        Subscription subscription = WeiboAPI.get(CommentAPI.class)
                .show(statusId, isLoadingLatest ? 0L : sinceId, 0L)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        commentView.showCommentLoadingIndicator(isLoadingLatest,true);
                    }
                })
                .doOnNext(new Action1<Comments>() {
                    @Override
                    public void call(Comments comments) {
                        TextDecorator.decorate(comments);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Comments>() {
                    @Override
                    public void onCompleted() {
                        commentView.showCommentLoadingIndicator(isLoadingLatest, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        commentView.showCommentLoadingIndicator(isLoadingLatest, false);
                        Log.e(TAG, "update comment time line failed: ", e);
                    }

                    @Override
                    public void onNext(Comments comments) {
                        CommentPresenter.this.sinceId = comments.max_id;
                        commentView.show(comments);
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
