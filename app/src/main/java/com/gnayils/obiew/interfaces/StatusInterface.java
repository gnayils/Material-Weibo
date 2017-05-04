package com.gnayils.obiew.interfaces;

import com.gnayils.obiew.weibo.bean.StatusTimeline;
import com.gnayils.obiew.weibo.bean.User;

/**
 * Created by Gnayils on 18/03/2017.
 */

public interface StatusInterface  {

    interface View extends BaseView {

        void show(StatusTimeline statusTimeline, int feature);

        void showStatusLoadingIndicator(boolean isLoadingLatest, boolean refreshing);

    }

    interface Presenter extends BasePresenter {

        void loadStatusTimeline(boolean isLoadingLatest);

        void loadStatusTimeline(boolean isLoadingLatest, User user, int feature);
    }
}
