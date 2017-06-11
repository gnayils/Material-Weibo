package com.gnayils.obiew.weibo.api;

import com.gnayils.obiew.weibo.bean.Comments;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 16/11/2016.
 */

public interface CommentAPI {

    @GET("2/comments/show.json")
    Observable<Comments> show(@Query("id") long statusId, @Query("page") int page, @Query("count") int count);

    @Deprecated
    @GET("2/comments/show.json")
    Observable<Comments> show(@Query("id") long statusId, @Query("max_id") long maxId, @Query("since_id") long sinceId);

}
