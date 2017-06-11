package com.gnayils.obiew.weibo.service;

import com.gnayils.obiew.weibo.TextDecorator;
import com.gnayils.obiew.weibo.VideoInfoParser;
import com.gnayils.obiew.weibo.bean.Comments;
import com.gnayils.obiew.weibo.bean.Reposts;
import com.gnayils.obiew.weibo.bean.Statuses;

import rx.exceptions.Exceptions;
import rx.functions.Action1;

/**
 * Created by Gnayils on 31/05/2017.
 */

public class Actions {

    public static final Action1<Statuses> DECORATE_STATUS_TEXT = new Action1<Statuses>(){

        @Override
        public void call(Statuses statuses) {
            TextDecorator.decorate(statuses);
        }
    };

    public static final Action1<Comments> DECORATE_COMMENT_TEXT = new Action1<Comments>(){

        @Override
        public void call(Comments comments) {
            TextDecorator.decorate(comments);
        }
    };

    public static final Action1<Reposts> DECORATE_REPOST_TEXT = new Action1<Reposts>(){

        @Override
        public void call(Reposts reposts) {
            TextDecorator.decorate(reposts);
        }
    };

    public static final Action1<Statuses> PARSE_STATUS_VIDEO_INFO = new Action1<Statuses>(){

        @Override
        public void call(Statuses statuses) {
            try {
                VideoInfoParser.parse(statuses);
            } catch (Throwable throwable) {
                throw Exceptions.propagate(throwable);
            }
        }
    };
}
