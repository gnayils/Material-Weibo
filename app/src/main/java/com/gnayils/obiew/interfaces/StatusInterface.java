package com.gnayils.obiew.interfaces;

import com.gnayils.obiew.weibo.bean.Statuses;
import com.gnayils.obiew.weibo.bean.User;

/**
 * Created by Gnayils on 18/03/2017.
 */

@Deprecated
public interface StatusInterface  {

    @Deprecated
    interface View extends BaseView {

        void show(Statuses statuses, int feature);

        void showStatusLoadingIndicator(boolean isLoadingLatest, boolean refreshing);

    }

    @Deprecated
    interface Presenter extends BasePresenter {

        void loadStatusTimeline(boolean isLoadingLatest);

        void loadStatusTimeline(boolean isLoadingLatest, User user, int feature);
    }
}
