package com.gnayils.obiew.user;

import android.app.Activity;

import com.gnayils.obiew.BasePresenter;
import com.gnayils.obiew.BaseView;
import com.gnayils.obiew.bean.Timeline;
import com.gnayils.obiew.bean.User;

/**
 * Created by Gnayils on 12/11/2016.
 */

public interface UserInterface {


    interface ProfileView extends BaseView<Presenter>{

        void updateUser(User user);

    }

    interface StatusTimelineView extends BaseView<Presenter> {

        void showStatusTimeline(Timeline timeline);

        void showLoadingIndicator(boolean refreshing);

    }


    interface Presenter extends BasePresenter {

        void requestLogin(Activity activity);

        void requestSignUp(Activity activity);

        void loadStatusTimeline();

    }
}
