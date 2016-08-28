package com.gnayils.obiew.user;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.models.User;

/**
 * Created by Administrator on 8/20/2016.
 */
public interface LoginView {

    void updateUser(User user);

}
