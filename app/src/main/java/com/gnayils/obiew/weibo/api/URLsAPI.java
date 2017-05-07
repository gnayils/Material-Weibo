package com.gnayils.obiew.weibo.api;

import com.gnayils.obiew.weibo.bean.URL;
import com.gnayils.obiew.weibo.bean.URLs;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 07/05/2017.
 */

public interface URLsAPI {

    @GET("2/short_url/expand.json")
    Observable<URLs> expand(@Query("url_short")List<String> url_shorts);

}
