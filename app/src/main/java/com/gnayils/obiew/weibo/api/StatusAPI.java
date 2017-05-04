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

    @GET("2/statuses/home_timeline.json")
    Observable<StatusTimeline> homeTimeline(@Query("max_id")long maxId, @Query("since_id")long sinceId);

    @GET("2/statuses/repost_timeline.json")
    Observable<RepostTimeline> repostTimeline(@Query("id") long statusId, @Query("max_id") long maxId, @Query("since_id") long sinceId);

    @GET("2/statuses/user_timeline.json")
    Observable<StatusTimeline> userTimeline(@Query("uid") long uid, @Query("feature")int feature, @Query("count") int count, @Query("page") int page);

}
