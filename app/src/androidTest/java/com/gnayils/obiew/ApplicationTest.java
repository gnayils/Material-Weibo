package com.gnayils.obiew;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    private final String TAG = this.getClass().getSimpleName();

    public ApplicationTest() {
        super(Application.class);

    }

    public void testMemoryClass() {
        ActivityManager am = (ActivityManager) this.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        Log.v(TAG, "Memory class: " + am.getMemoryClass());
        Log.v(TAG, "Large Memory class: " + am.getLargeMemoryClass());
    }

    public void testEmotionResource() {
        String[] arrays = getContext().getResources().getStringArray(R.array.emotion_map);
        for (String string : arrays) {
            try {
                R.drawable.class.getDeclaredField(string.split(",")[1]);
            } catch (NoSuchFieldException e) {
                System.out.println(string + " not found");
            }
        }
    }

    public void testRxJavaThreadPolicy() {
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        System.out.println("OnSubscribe call: " + Thread.currentThread().getName());
                        subscriber.onStart();
                        subscriber.onNext("asdf");
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("doOnSubscribe: " + Thread.currentThread().getName());
                    }
                })
                .subscribe(new Subscriber<String>() {

                    @Override
                    public void onStart() {
                        System.out.println("onStart: " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("onNext: " + Thread.currentThread().getName());

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError: " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("onCompleted: " + Thread.currentThread().getName());
                    }
                });
    }


}