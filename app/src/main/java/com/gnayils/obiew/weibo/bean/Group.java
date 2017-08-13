package com.gnayils.obiew.weibo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gnayils on 11/06/2017.
 */

public class Group implements Serializable {

    public long id;
    public String idstr;
    public String name;
    public String mode;
    public int visible;
    public int like_count;
    public int member_count;
    public String description;
    public String profile_image_url;
    public User user;
    public String created_at;
    public List<?> tags;
}
