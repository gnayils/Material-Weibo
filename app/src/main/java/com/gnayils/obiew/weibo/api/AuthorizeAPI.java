package com.gnayils.obiew.weibo.api;

import com.gnayils.obiew.weibo.bean.AccessToken;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Gnayils on 19/03/2017.
 */

public interface AuthorizeAPI {

    @POST("oauth2/access_token")
    Observable<AccessToken> accessToken(@Query("client_id") String clientId, @Query("client_secret") String clientSecret, @Query("grant_type") String grantType, @Query("code") String code, @Query("redirect_uri") String redirectUri);
}
