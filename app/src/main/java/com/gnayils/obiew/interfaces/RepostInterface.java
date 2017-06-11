package com.gnayils.obiew.interfaces;

import com.gnayils.obiew.weibo.bean.Reposts;

/**
 * Created by Gnayils on 18/03/2017.
 */
@Deprecated
public interface RepostInterface {


    interface View extends BaseView {

        void show(Reposts reposts);

        void showRepostLoadingIndicator(boolean isLoadingLatest, boolean refreshing);

    }

    interface Presenter extends BasePresenter {

        void loadRepostTimeline(long statusId, boolean isLoadingLatest);

    }
}
