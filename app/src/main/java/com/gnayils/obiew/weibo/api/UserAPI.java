package com.gnayils.obiew.weibo.api;

import com.gnayils.obiew.weibo.bean.User;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 13/11/2016.
 */

public interface UserAPI {

    @GET("2/users/show.json")
    Observable<User> showById(@Query("uid") long uid);


    @GET("2/users/show.json")
    Observable<User> showByName(@Query("screen_name") String screen_name);
}
