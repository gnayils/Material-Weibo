package com.gnayils.obiew.weibo.bean;

import java.util.List;

/**
 * Created by Gnayils on 18/03/2017.
 */

public class Repost {

    public String created_at;
    public long id;
    public String text;
    public String source;
    public String favorited;
    public String truncated;
    public String in_reply_to_status_id;
    public String in_reply_to_user_id;
    public String in_reply_to_screen_name;
    public Object geo;
    public String mid;
    public int reposts_count;
    public int comments_count;
    public User user;
    public Status retweeted_status;
    public List<?> annotations;
}
