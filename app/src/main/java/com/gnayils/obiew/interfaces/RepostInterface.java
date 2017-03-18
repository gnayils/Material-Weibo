package com.gnayils.obiew.interfaces;

import com.gnayils.obiew.BasePresenter;
import com.gnayils.obiew.BaseView;
import com.gnayils.obiew.weibo.bean.CommentTimeline;
import com.gnayils.obiew.weibo.bean.RepostTimeline;

/**
 * Created by Gnayils on 18/03/2017.
 */

public interface RepostInterface {


    interface View extends BaseView<Presenter> {

        void show(RepostTimeline repostTimeline);

        void showLoadingIndicator(boolean refreshing);

    }

    interface Presenter extends BasePresenter {

        void loadRepost(long statusId, boolean latest);

    }
}
