package com.gnayils.obiew.weibo.service;

import com.gnayils.obiew.weibo.TextDecorator;
import com.gnayils.obiew.weibo.VideoInfoParser;
import com.gnayils.obiew.weibo.bean.CommentTimeline;
import com.gnayils.obiew.weibo.bean.RepostTimeline;
import com.gnayils.obiew.weibo.bean.StatusTimeline;

import rx.exceptions.Exceptions;
import rx.functions.Action1;

/**
 * Created by Gnayils on 31/05/2017.
 */

public class Actions {

    public static final Action1<StatusTimeline> DECORATE_STATUS_TEXT = new Action1<StatusTimeline>(){

        @Override
        public void call(StatusTimeline statusTimeline) {
            TextDecorator.decorate(statusTimeline);
        }
    };

    public static final Action1<CommentTimeline> DECORATE_COMMENT_TEXT = new Action1<CommentTimeline>(){

        @Override
        public void call(CommentTimeline commentTimeline) {
            TextDecorator.decorate(commentTimeline);
        }
    };

    public static final Action1<RepostTimeline> DECORATE_REPOST_TEXT = new Action1<RepostTimeline>(){

        @Override
        public void call(RepostTimeline repostTimeline) {
            TextDecorator.decorate(repostTimeline);
        }
    };

    public static final Action1<StatusTimeline> PARSE_STATUS_VIDEO_INFO = new Action1<StatusTimeline>(){

        @Override
        public void call(StatusTimeline statusTimeline) {
            try {
                VideoInfoParser.parse(statusTimeline);
            } catch (Throwable throwable) {
                throw Exceptions.propagate(throwable);
            }
        }
    };
}
