package com.gnayils.obiew.weibo.api;

import com.gnayils.obiew.weibo.bean.StatusTimeline;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 04/06/2017.
 */

public interface SearchAPI {

    @GET("2/search/topics.json")
    Observable<StatusTimeline> topics(@Query("q") String topic, @Query("page") int page, @Query("count") int count);
}
