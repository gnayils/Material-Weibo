package com.gnayils.obiew.weibo;

import android.util.Log;

import com.gnayils.obiew.util.Sync;
import com.gnayils.obiew.util.URLParser;
import com.gnayils.obiew.weibo.api.URLsAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.StatusTimeline;
import com.gnayils.obiew.weibo.bean.URL;
import com.gnayils.obiew.weibo.bean.URLs;
import com.gnayils.obiew.weibo.bean.Video;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Subscriber;

/**
 * Created by Gnayils on 07/05/2017.
 */

public class VideoURLFinder {

    public static final String TAG = VideoURLFinder.class.getName();

    public static final Pattern SHORT_URL = Pattern.compile("http:\\/\\/t\\.cn\\/\\w{7}");
    public static final String VIDEO_URL_PREFIX = "http://video.weibo.com/show";

    //public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2";
    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";
    public static final Map<String, String> COOKIES = new HashMap<>();

    static {
        COOKIES.put("Apache", "5519377093477.195.1494498485914");
        COOKIES.put("SCF", "AhYLnDoRTsrPVgl5u6xVeMPIbLd9EuEEDDyLdVmK7DE1x8np_GzzpEY80hK03UISGCHXjVq5-BzoyWVjaaSRhGk.");
        COOKIES.put("SINAGLOBAL", "8458929767307.768.1478954117277");
        COOKIES.put("SUB", "_2AkMuSg_ndcPxrAFTnPwTzmzjaYpH-jydn2YRAn7uJhMyAxh77lMIqSUGMTnbUqRRKAkzLekoReEuO7dMAA..");
        COOKIES.put("SUBP", "0033WrSXqPxfM72wWs9jqgMF55529P9D9W5.bWZACYDl1LebQqrG2VXO5JpV2h-feh.7Soef12WpMC4odcXt");
        COOKIES.put("SUHB", "04WvZ08AU5Ze1R");
        COOKIES.put("ULV", "1494498485965:24:2:1:5519377093477.195.1494498485914:1493725535436");
        COOKIES.put("UOR", ",,login.sina.com.cn");
        COOKIES.put("_s_tentry", "www.sina.com.cn");
        COOKIES.put("login_sid_t", "80f0ed304030758d2bab97121e49d50f");
    }

    public static void find(StatusTimeline statusTimeline) throws Throwable {
        if (statusTimeline != null && statusTimeline.statuses != null) {
            Map<String, Status> shortUrlMap = new HashMap<>();
            for (Status status : statusTimeline.statuses) {
                findShortUrl(status, shortUrlMap);
                findShortUrl(status.retweeted_status, shortUrlMap);
            }
            List<String> shortUrlList = new ArrayList<>(shortUrlMap.keySet());
            List<URL> urlList = expandShortUrl(shortUrlList);
            List<URL> videoUrlList = filterVideoUrl(urlList);
            for(URL url : videoUrlList) {
                Status status = shortUrlMap.get(url.url_short);
                status.videoUrls = url;
            }
        }
    }

    private static void findShortUrl(Status status, Map<String, Status> urlMap) {
        if (status != null && status.text != null) {
            Matcher matcher = SHORT_URL.matcher(status.text);
            while (matcher.find()) {
                String url_short = matcher.group();
                urlMap.put(url_short, status);
            }
        }
    }

    private static List<URL> expandShortUrl(List<String> shortUrlList) throws Throwable {
        final List<URL> urlList = new ArrayList<>();
        for (int i = 0; i < shortUrlList.size(); i += 20) {
            int fromIndex = i;
            int toIndex = i + 20;
            if (toIndex > shortUrlList.size()) {
                toIndex = shortUrlList.size();
            }
            List<String> subUrlList = shortUrlList.subList(fromIndex, toIndex);
            final Throwable throwable = new Throwable();
            Log.d(TAG, "begin request to expand the short url");
            WeiboAPI.get(URLsAPI.class).expand(subUrlList)
                    .subscribe(new Subscriber<URLs>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "expand short url failed", e);
                            throwable.initCause(e);
                            Sync.notifyAll(urlList);
                        }

                        @Override
                        public void onNext(URLs urls) {
                            urlList.addAll(urls.urls);
                            Log.d(TAG, "expand url request get the response, perform request thread start to notify: " + Thread.currentThread().getName());
                            Sync.notifyAll(urlList);
                        }
                    });
            Log.d(TAG, "expand url request has been sent, send request thread begin to wait: " + Thread.currentThread().getName());
            Sync.wait(urlList);
            Log.d(TAG, "expand url request has been sent, send request thread being notified: " + Thread.currentThread().getName());
            if (throwable.getCause() != null) {
                throw throwable.getCause();
            }
        }
        Log.d(TAG, "all videoUrls in status timeline: \n" + urlList.toString());
        return urlList;
    }

    private static List<URL> filterVideoUrl(List<URL> urlList) {
        for(int i = urlList.size() - 1; i > -1; i--) {
            if(urlList.get(i).url_long.startsWith(VIDEO_URL_PREFIX)) {
                Log.d(TAG, ">>>>>>>>>>>short url: " + urlList.get(i).url_short + ", long url: " + urlList.get(i).url_long);
                loadVideoInfo(urlList.get(i));
            } else {
                urlList.remove(i);
            }
        }
        return urlList;
    }

    public static void loadVideoInfo(URL url) {
        try {
            Document document = Jsoup.connect(url.url_long).userAgent(USER_AGENT)
                    .cookie("SUB", "_2AkMuSg_ndcPxrAFTnPwTzmzjaYpH-jydn2YRAn7uJhMyAxh77lMIqSUGMTnbUqRRKAkzLekoReEuO7dMAA..").get();
            Elements common_video_player = document.select("#playerRoom [node-type=\"common_video_player\"]");
            String action_data = common_video_player.attr("action-data");
            Log.d(TAG, "action_data: " + action_data);
            Map<String, String> map = URLParser.parse(new java.net.URL(VIDEO_URL_PREFIX + "?" + action_data));
            for(Map.Entry<String, String> entry : map.entrySet()) {
                Log.d(TAG, entry.getKey() + "=" + entry.getValue());
            }
            Gson gson = new Gson();
            JsonElement jsonElement = gson.toJsonTree(map);
            Log.d(TAG, "jsonElement: \n" + jsonElement.toString());
            Video video = gson.fromJson(jsonElement, Video.class);
            url.video = video;
        } catch (Exception e) {
            Log.e(TAG, "load video information failed from the url: " + url.url_long, e);
        }
    }
}
