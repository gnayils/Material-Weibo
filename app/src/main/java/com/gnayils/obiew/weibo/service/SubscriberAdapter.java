package com.gnayils.obiew.weibo.service;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Gnayils on 30/05/2017.
 */

public class SubscriberAdapter<T> extends Subscriber<T> {

    public final Action0 onSubscribeAction;
    public final Action0 onUnsubscribeAction;
    public final Action1<T> doOnNextAction;
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public SubscriberAdapter() {
        onSubscribeAction= new Action0() {
            @Override
            public void call() {
                if(Looper.myLooper() == Looper.getMainLooper()) {
                    onSubscribe();
                } else {
                    HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            onSubscribe();
                        }
                    });
                }
            }
        };
        onUnsubscribeAction = new Action0() {
            @Override
            public void call() {
                if(Looper.myLooper() == Looper.getMainLooper()) {
                    onUnsubscribe();
                } else {
                    HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            onUnsubscribe();
                        }
                    });
                }
            }
        };
        doOnNextAction = new Action1<T>() {
            @Override
            public void call(T t) {
                doOnNext(t);
            }
        };
    }

    /**
     * Do not ensure the method will runs in Main thread
     */
    @Override
    public void onStart() {

    }

    /**
     * Ensure the method will runs in Main thread
     */
    public void onSubscribe() {

    }

    @Override
    public void onError(Throwable e) {

    }

    public void doOnNext(T t) {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onCompleted() {

    }

    /**
     * Ensure the method will runs in Main thread
     */
    public void onUnsubscribe() {

    }

    public void setupTo(Observable<T> observable) {


    }
}
