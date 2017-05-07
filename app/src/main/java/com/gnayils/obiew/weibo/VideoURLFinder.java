package com.gnayils.obiew.weibo;

import android.util.Log;

import com.gnayils.obiew.weibo.api.URLsAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.StatusTimeline;
import com.gnayils.obiew.weibo.bean.URL;
import com.gnayils.obiew.weibo.bean.URLs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Subscriber;

/**
 * Created by Gnayils on 07/05/2017.
 */

public class VideoURLFinder {

    public static final String TAG = VideoURLFinder.class.getName();

    public static final Pattern SHORT_URL = Pattern.compile("http:\\/\\/t\\.cn\\/\\w{7}");

    public static List<String> tempURLs = new ArrayList<>();

    public static void find(StatusTimeline statusTimeline) {
        if (statusTimeline != null && statusTimeline.statuses != null) {
            tempURLs.clear();
            for (Status status : statusTimeline.statuses) {
                find(status);
            }
            if (!tempURLs.isEmpty()) {
                final List<URL> result = new ArrayList<>();
                for (int i = 0; i < tempURLs.size(); i += 20) {
                    int fromIndex = i;
                    int toIndex = i + 20;
                    if (toIndex > tempURLs.size()) {
                        toIndex = tempURLs.size();
                    }
                    List<String> subTempURLs = tempURLs.subList(fromIndex, toIndex);
                    WeiboAPI.get(URLsAPI.class).expand(subTempURLs)
                            .subscribe(new Subscriber<URLs>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, "expand short url failed", e);
                                }

                                @Override
                                public void onNext(URLs urls) {
                                    System.out.println("onNextThread: " + Thread.currentThread().getName());
                                    synchronized (result) {
                                        result.addAll(urls.urls);
                                        result.notifyAll();
                                    }
                                }
                            });

                    try {
                        synchronized (result) {
                            result.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for(URL url : result) {
                    System.out.println(url.url_short + "\t" + url.url_long);
                }
            }
        }
    }


    public static void find(Status status) {
        if (status != null && status.text != null) {
            Matcher matcher = SHORT_URL.matcher(status.text);
            while (matcher.find()) {
                String url_short = matcher.group();
                tempURLs.add(url_short);
                System.out.println("url_short: " + url_short);
                URL url = new URL();
                url.url_short = url_short;
                status.urls.add(url);
            }
        }

    }
}
