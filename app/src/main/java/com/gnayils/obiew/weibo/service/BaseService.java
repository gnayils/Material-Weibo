package com.gnayils.obiew.weibo.service;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gnayils on 01/06/2017.
 */

public class BaseService {

    private CompositeSubscription subscriptions = new CompositeSubscription();

    protected void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    public void unsubscribe() {
        subscriptions.clear();
    }
}
