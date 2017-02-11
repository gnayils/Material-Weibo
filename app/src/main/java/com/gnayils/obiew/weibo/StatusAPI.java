package com.gnayils.obiew.weibo;

import com.gnayils.obiew.bean.Status;
import com.gnayils.obiew.bean.Timeline;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 16/11/2016.
 */

public interface StatusAPI {

    @GET("statuses/home_timeline.json")
    Observable<Timeline> homeTimeline(@Query("access_token") String accessToken);
}
