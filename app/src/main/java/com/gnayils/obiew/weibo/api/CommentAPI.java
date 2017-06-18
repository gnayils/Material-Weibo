package com.gnayils.obiew.weibo.api;

import com.gnayils.obiew.weibo.bean.Comment;
import com.gnayils.obiew.weibo.bean.Comments;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 16/11/2016.
 */

public interface CommentAPI {

    @GET("2/comments/show.json")
    Observable<Comments> show(@Query("id") long statusId, @Query("page") int page, @Query("count") int count);

    @FormUrlEncoded
    @POST("2/comments/create.json")
    Observable<Comment> create(@Query("id") long statusId, @Field("comment") String comment);

    @Deprecated
    @GET("2/comments/show.json")
    Observable<Comments> show(@Query("id") long statusId, @Query("max_id") long maxId, @Query("since_id") long sinceId);

}
