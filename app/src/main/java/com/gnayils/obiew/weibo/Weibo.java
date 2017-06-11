package com.gnayils.obiew.weibo;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Gnayils on 11/03/2017.
 */

public class Weibo {



    public static class consts {

        public static final int STATUS_TIMELINE_ITEM_COUNT_PER_PAGE = 20;
        public static final int COMMENT_TIMELINE_ITEM_COUNT_PER_PAGE = 50;
        public static final int REPOST_TIMELINE_ITEM_COUNT_PER_PAGE = 50;


        public static final int STATUS_TEXT_MAX_LENGTH = 140;
    }

    public static class format {

        public static final String TAG = format.class.getSimpleName();

        public static final DateFormat DISPLAY_PATTERN = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.ENGLISH);

        public static final DateFormat SOURCE_PATTERN = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

        public static final int FOLLOWER_COUNT_FORMAT_LIMIT = 100000;
        public static final int FOLLOWER_COUNT_FORMAT_UNIT = 10000;
        public static final int COMMENT_COUNT_FORMAT_LIMIT = 10000;

        public static String date(String source) {
            try {
                Date sourceDate = SOURCE_PATTERN.parse(source);
                return DISPLAY_PATTERN.format(sourceDate);
            } catch (ParseException e) {
                Log.e(TAG, "date format failed: " + source, e);
                return source;
            }
        }

        public static String followerCount(int count) {
            if(count > FOLLOWER_COUNT_FORMAT_LIMIT) {
                return String.format("%d万", count / FOLLOWER_COUNT_FORMAT_UNIT);
            }
            return String.valueOf(count);
        }

        public static String commentCount(int count) {
            if(count > COMMENT_COUNT_FORMAT_LIMIT) {
                return String.format("%d万", count / COMMENT_COUNT_FORMAT_LIMIT);
            }
            return String.valueOf(count);
        }

    }

}
