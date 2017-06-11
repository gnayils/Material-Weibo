package com.gnayils.obiew.weibo.service;

import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.api.CommentAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.Comments;
import com.gnayils.obiew.weibo.bean.Status;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Gnayils on 01/06/2017.
 */

public class CommentService extends BaseService {

    public static final String TAG = CommentService.class.getSimpleName();

    private int commentTimelineCurrentPage = 0;

    public void showCommentTimeline(Status status, boolean loadLatest, SubscriberAdapter<Comments> subscriberAdapter) {
        commentTimelineCurrentPage = loadLatest ? 1 : ++commentTimelineCurrentPage;
        Subscription subscription = WeiboAPI.get(CommentAPI.class)
                .show(status.id, commentTimelineCurrentPage, Weibo.consts.COMMENT_TIMELINE_ITEM_COUNT_PER_PAGE)
                .doOnSubscribe(subscriberAdapter.onSubscribeAction)
                .doOnUnsubscribe(subscriberAdapter.onUnsubscribeAction)
                .doOnNext(Actions.DECORATE_COMMENT_TEXT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriberAdapter);
        addSubscription(subscription);
    }

}
