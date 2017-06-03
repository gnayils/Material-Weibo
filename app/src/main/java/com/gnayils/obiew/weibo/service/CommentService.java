package com.gnayils.obiew.weibo.service;

import android.util.Log;

import com.gnayils.obiew.presenter.CommentPresenter;
import com.gnayils.obiew.weibo.TextDecorator;
import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.api.CommentAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.CommentTimeline;
import com.gnayils.obiew.weibo.bean.Status;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gnayils on 01/06/2017.
 */

public class CommentService extends BaseService {

    public static final String TAG = CommentService.class.getSimpleName();

    private int commentTimelineCurrentPage = 0;

    public void showCommentTimeline(Status status, boolean loadLatest, SubscriberAdapter<CommentTimeline> subscriberAdapter) {
        commentTimelineCurrentPage = loadLatest ? 1 : ++commentTimelineCurrentPage;
        Subscription subscription = WeiboAPI.get(CommentAPI.class)
                .show(status.id, commentTimelineCurrentPage, Weibo.Const.COMMENT_TIMELINE_ITEM_COUNT_PER_PAGE)
                .doOnSubscribe(subscriberAdapter.onSubscribeAction)
                .doOnUnsubscribe(subscriberAdapter.onUnsubscribeAction)
                .doOnNext(Actions.DECORATE_COMMENT_TEXT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriberAdapter);
        addSubscription(subscription);
    }

}
