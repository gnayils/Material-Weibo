package com.gnayils.obiew.interfaces;

import com.gnayils.obiew.BasePresenter;
import com.gnayils.obiew.BaseView;
import com.gnayils.obiew.weibo.bean.StatusTimeline;

/**
 * Created by Gnayils on 18/03/2017.
 */

public interface StatusInterface  {

    interface View extends BaseView<Presenter> {

        void show(StatusTimeline statusTimeline);

        void showLoadingIndicator(boolean refreshing);

    }

    interface Presenter extends BasePresenter {

        void loadStatusTimeline(boolean latest);

    }
}
