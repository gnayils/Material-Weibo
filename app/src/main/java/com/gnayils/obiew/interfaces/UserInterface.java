package com.gnayils.obiew.interfaces;

import android.app.Activity;

import com.gnayils.obiew.BasePresenter;
import com.gnayils.obiew.BaseView;
import com.gnayils.obiew.weibo.bean.StatusTimeline;
import com.gnayils.obiew.weibo.bean.User;

/**
 * Created by Gnayils on 12/11/2016.
 */

public interface UserInterface {


    interface View extends BaseView<Presenter>{

        void show(User user);

    }

    interface Presenter extends BasePresenter {

        void requestLogin(Activity activity);

        void requestSignUp(Activity activity);
    }
}
