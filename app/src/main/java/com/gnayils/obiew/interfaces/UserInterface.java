package com.gnayils.obiew.interfaces;

import com.gnayils.obiew.weibo.bean.User;

/**
 * Created by Gnayils on 12/11/2016.
 */

public interface UserInterface {

    interface View extends BaseView {

        void show(User user);

    }

    interface Presenter extends BasePresenter {
        void loadUser();
    }
}
