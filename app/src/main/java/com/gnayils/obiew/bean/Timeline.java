package com.gnayils.obiew.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Gnayils on 16/11/2016.
 */

public class Timeline {

    public boolean hasvisible;
    public int previous_cursor;
    public long next_cursor;
    public int total_number;
    public int interval;
    public int uve_blank;
    public long since_id;
    public long max_id;
    public int has_unread;
    public List<Status> statuses;
    public List<?> advertises;
    public List<?> ad;

}
