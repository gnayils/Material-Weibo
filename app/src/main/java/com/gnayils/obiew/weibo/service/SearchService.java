package com.gnayils.obiew.weibo.service;

import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.api.SearchAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.StatusTimeline;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Gnayils on 03/06/2017.
 */

public class SearchService extends BaseService {

    private int topicCurrentPage;

    public void showTopicTimeline(boolean loadLatest, String topic, SubscriberAdapter<StatusTimeline> subscriberAdapter) {
        topicCurrentPage = loadLatest ? 1 : ++ topicCurrentPage;
        Subscription subscription = WeiboAPI.get(SearchAPI.class).topics(topic, topicCurrentPage, Weibo.consts.TOPIC_TIMELINE_ITEM_COUNT_PER_PAGE)
                .doOnSubscribe(subscriberAdapter.onSubscribeAction)
                .doOnUnsubscribe(subscriberAdapter.onUnsubscribeAction)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriberAdapter);
        addSubscription(subscription);
    }

}
