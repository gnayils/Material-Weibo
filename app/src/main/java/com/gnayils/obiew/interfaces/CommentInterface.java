package com.gnayils.obiew.interfaces;

import com.gnayils.obiew.weibo.bean.Comments;

/**
 * Created by Gnayils on 12/03/2017.
 */
@Deprecated
public interface CommentInterface {

    interface View extends BaseView {

        void show(Comments comments);

        void showCommentLoadingIndicator(boolean isLoadingLatest, boolean refreshing);
    }


    interface Presenter extends BasePresenter {

        void loadCommentTimeline(long statusId, boolean isLoadingLatest);

    }
}
