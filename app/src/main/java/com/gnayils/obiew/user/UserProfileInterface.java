package com.gnayils.obiew.user;

import android.app.Activity;

import com.gnayils.obiew.BasePresenter;
import com.gnayils.obiew.BaseView;
import com.sina.weibo.sdk.openapi.models.User;

/**
 * Created by Gnayils on 12/11/2016.
 */

public interface UserProfileInterface {


    interface View extends BaseView<Presenter>{

        void updateUser(User user);

    }


    interface Presenter extends BasePresenter {

        void requestLogin(Activity activity);

        void requestSignup(Activity activity);

    }
}
