package com.gnayils.obiew.interfaces;

import com.gnayils.obiew.weibo.bean.RepostTimeline;

/**
 * Created by Gnayils on 18/03/2017.
 */

public interface RepostInterface {


    interface View extends BaseView {

        void show(RepostTimeline repostTimeline);

        void showRepostLoadingIndicator(boolean isLoadingLatest, boolean refreshing);

    }

    interface Presenter extends BasePresenter {

        void loadRepostTimeline(long statusId, boolean isLoadingLatest);

    }
}
