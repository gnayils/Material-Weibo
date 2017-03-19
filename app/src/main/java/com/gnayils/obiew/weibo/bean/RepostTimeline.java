package com.gnayils.obiew.weibo.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Gnayils on 18/03/2017.
 */

public class RepostTimeline {

    public boolean hasvisible;
    public long previous_cursor;
    public long next_cursor;
    public int total_number;
    public int interval;
    public List<Repost> reposts;

}
