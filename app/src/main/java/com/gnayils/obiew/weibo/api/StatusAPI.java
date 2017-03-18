package com.gnayils.obiew.weibo.api;

import com.gnayils.obiew.weibo.bean.RepostTimeline;
import com.gnayils.obiew.weibo.bean.StatusTimeline;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 16/11/2016.
 */

public interface StatusAPI {

    @GET("statuses/home_timeline.json")
    Observable<StatusTimeline> homeTimeline(@Query("access_token") String accessToken, @Query("max_id")long maxId, @Query("since_id")long sinceId);

    @GET("statuses/repost_timeline.json")
    Observable<RepostTimeline> repostTimeline(@Query("access_token") String accessToken, @Query("id") long statusId, @Query("max_id") long maxId, @Query("since_id") long sinceId);

}
