package com.gnayils.obiew.weibo.api;

import com.gnayils.obiew.weibo.bean.Statuses;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 04/06/2017.
 */

public interface SearchAPI {

    @GET("2/search/topics.json")
    Observable<Statuses> topics(@Query("q") String topic, @Query("page") int page, @Query("count") int count);
}
