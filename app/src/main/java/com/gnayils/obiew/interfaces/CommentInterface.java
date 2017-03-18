package com.gnayils.obiew.interfaces;

import com.gnayils.obiew.BasePresenter;
import com.gnayils.obiew.BaseView;
import com.gnayils.obiew.weibo.bean.CommentTimeline;

/**
 * Created by Gnayils on 12/03/2017.
 */

public interface CommentInterface {

    interface View  extends BaseView<Presenter> {

        void show(CommentTimeline commentTimeline);

        void showLoadingIndicator(boolean refreshing);

    }


    interface Presenter extends BasePresenter {

        void loadComment(long statusId, boolean latest);

    }
}
