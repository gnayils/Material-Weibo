package com.gnayils.obiew.weibo.api;

import com.gnayils.obiew.weibo.bean.User;
import com.gnayils.obiew.weibo.bean.Users;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 13/11/2016.
 */

public interface FriendShipsAPI {

    @GET("2/friendships/friends.json")
    Observable<Users> friends(@Query("uid") String uid);

}
