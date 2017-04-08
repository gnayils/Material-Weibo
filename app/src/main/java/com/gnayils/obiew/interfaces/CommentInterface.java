package com.gnayils.obiew.interfaces;

import com.gnayils.obiew.weibo.bean.CommentTimeline;

/**
 * Created by Gnayils on 12/03/2017.
 */

public interface CommentInterface {

    interface View extends BaseView {

        void show(CommentTimeline commentTimeline);

        void showCommentLoadingIndicator(boolean refreshing);
    }


    interface Presenter extends BasePresenter {

        void loadCommentTimeline(long statusId, boolean latest);

    }
}
