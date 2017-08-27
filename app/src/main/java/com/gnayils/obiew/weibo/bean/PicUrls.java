package com.gnayils.obiew.weibo.bean;

import android.support.annotation.NonNull;

import com.gnayils.obiew.weibo.Weibo;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Objects;

/**
 * Created by Gnayils on 19/03/2017.
 */

public class PicUrls implements Serializable, Comparable<PicUrls> {

    public String thumbnail_pic;
    public Status status;

    public String middle() {
        if(thumbnail_pic != null) {
            return thumbnail_pic.replace("/thumbnail/", "/bmiddle/");
        }
        return null;
    }

    public String large() {
        if(thumbnail_pic != null) {
            return thumbnail_pic.replace("/thumbnail/", "/large/");
        }
        return null;
    }

    public boolean isGif() {
        if(thumbnail_pic != null) {
            return thumbnail_pic.toLowerCase().endsWith(".gif");
        }
        return false;
    }

    @Override
    public boolean equals(Object another) {
        if (another == this) {
            return true;
        } else if (!(another instanceof Status)) {
            return false;
        }
        PicUrls anotherPicUrls = (PicUrls) another;
        return Objects.equals(thumbnail_pic, anotherPicUrls.thumbnail_pic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thumbnail_pic);
    }


    @Override
    public int compareTo(@NonNull PicUrls another) {
        try {
            return (int) (Weibo.format.SOURCE_PATTERN.parse(another.status.created_at).getTime() - Weibo.format.SOURCE_PATTERN.parse(status.created_at).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
