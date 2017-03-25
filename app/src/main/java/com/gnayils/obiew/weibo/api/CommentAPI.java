package com.gnayils.obiew.weibo.api;

import com.gnayils.obiew.weibo.bean.CommentTimeline;
import com.gnayils.obiew.weibo.bean.StatusTimeline;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 16/11/2016.
 */

public interface CommentAPI {

    @GET("2/comments/show.json")
    Observable<CommentTimeline> show(@Query("id") long statusId, @Query("max_id") long maxId, @Query("since_id") long sinceId);

}
