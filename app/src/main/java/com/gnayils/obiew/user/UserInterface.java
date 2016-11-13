package com.gnayils.obiew.user;

import com.gnayils.obiew.App;
import com.gnayils.obiew.bean.User;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 13/11/2016.
 */

public interface UserInterface {

    @GET("users/show.json")
    Observable<User> getUser(@Query("access_token") String accessToken, @Query("uid") String uid);
}
