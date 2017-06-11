package com.gnayils.obiew.weibo.api;

import com.gnayils.obiew.weibo.bean.Groups;
import com.gnayils.obiew.weibo.bean.Statuses;
import com.gnayils.obiew.weibo.bean.Users;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 13/11/2016.
 */

public interface FriendShipsAPI {

    @GET("2/friendships/friends.json")
    Observable<Users> friends(@Query("uid") long uid);

    @GET("2/friendships/groups.json")
    Observable<Groups> groups();

    @GET("2/friendships/groups/timeline.json")
    Observable<Statuses> groupTimeline(@Query("list_id") long groupId, @Query("feature") int feature, @Query("page") int page, @Query("count") int count);

}
