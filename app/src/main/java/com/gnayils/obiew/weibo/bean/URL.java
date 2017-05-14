package com.gnayils.obiew.weibo.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gnayils on 07/05/2017.
 */

public class URL implements Serializable {

    public boolean result;
    public String url_short;
    public String url_long;
    public int type;
    public int transcode;

    public Video video;

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_MUSIC = 2;
    public static final int TYPE_ACTIVITY = 3;
    public static final int TYPE_VOTE = 5;


    @Override
    public boolean equals(Object another) {
        if (another == this) {
            return true;
        } else if (!(another instanceof Status)) {
            return false;
        }
        URL anotherURL = (URL) another;
        return Objects.equals(url_short, anotherURL.url_short);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url_short);
    }


    @Override
    public String toString() {
        return "url_short: " + url_short + ", url_long: " + url_long;
    }
}
