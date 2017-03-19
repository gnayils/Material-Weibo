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
    Observable<User> show(@Query("access_token") String accessToken, @Query("uid") String uid);

}
