package com.gnayils.obiew.weibo.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Gnayils on 27/05/2017.
 */

public class Users {

    public int next_cursor;
    public int previous_cursor;
    public int total_number;
    public List<User> users;

}
