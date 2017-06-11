package com.gnayils.obiew.weibo.service;

import com.gnayils.obiew.presenter.FriendshipPresenter;
import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.api.FriendShipsAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.Group;
import com.gnayils.obiew.weibo.bean.Groups;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.Statuses;
import com.gnayils.obiew.weibo.bean.Users;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Gnayils on 02/06/2017.
 */

public class FriendshipService extends BaseService {

    public static final String TAG = FriendshipPresenter.class.getSimpleName();

    private int[] groupTimelineCurrentPages = new int[Status.FEATURES_COUNT];

    public void friends(long uid, SubscriberAdapter<Users> subscriberAdapter) {
        Subscription subscription = WeiboAPI.get(FriendShipsAPI.class)
                .friends(uid)
                .doOnSubscribe(subscriberAdapter.onSubscribeAction)
                .doOnUnsubscribe(subscriberAdapter.onUnsubscribeAction)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriberAdapter);
        addSubscription(subscription);
    }

    public void groups(SubscriberAdapter<Groups> subscriberAdapter) {
        Subscription subscription = WeiboAPI.get(FriendShipsAPI.class)
                .groups()
                .doOnSubscribe(subscriberAdapter.onSubscribeAction)
                .doOnUnsubscribe(subscriberAdapter.onUnsubscribeAction)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriberAdapter);
        addSubscription(subscription);
    }

    public void showGroupTimeline(Group group, boolean loadLatest, int feature, SubscriberAdapter<Statuses> subscriberAdapter) {
        groupTimelineCurrentPages[feature] = loadLatest ? 1 : ++groupTimelineCurrentPages[feature];
        Subscription subscription = WeiboAPI.get(FriendShipsAPI.class)
                .groupTimeline(group.id, feature, groupTimelineCurrentPages[feature], Weibo.consts.STATUS_TIMELINE_ITEM_COUNT_PER_PAGE)
                .doOnSubscribe(subscriberAdapter.onSubscribeAction)
                .doOnUnsubscribe(subscriberAdapter.onUnsubscribeAction)
                .doOnNext(Actions.PARSE_STATUS_VIDEO_INFO)
                .doOnNext(Actions.DECORATE_STATUS_TEXT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriberAdapter);
        addSubscription(subscription);
    }
}
