package com.gnayils.obiew.interfaces;

import com.gnayils.obiew.weibo.bean.User;
import com.gnayils.obiew.weibo.bean.Users;

/**
 * Created by Gnayils on 27/05/2017.
 */
@Deprecated
public class FriendshipInterface {

    public interface View extends BaseView {

        void setLoadingIndicatorVisible(boolean visible);

        void show(Users users);

    }

    public interface Presenter extends BasePresenter {

        void friends(String uid);

    }
}
