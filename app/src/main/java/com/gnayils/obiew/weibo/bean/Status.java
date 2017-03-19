package com.gnayils.obiew.weibo.bean;

import com.gnayils.obiew.weibo.Weibo;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Gnayils on 16/11/2016.
 */

public class Status implements Comparable<Status>, Serializable {

    public String created_at;
    public long id;
    public String mid;
    public String idstr;
    public String text;
    public int textLength;
    public int source_allowclick;
    public int source_type;
    public String source;
    public boolean favorited;
    public boolean truncated;
    public String in_reply_to_status_id;
    public String in_reply_to_user_id;
    public String in_reply_to_screen_name;
    public String thumbnail_pic;
    public String bmiddle_pic;
    public String original_pic;
    public Geo geo;
    public User user;
    public String pid;
    public Status retweeted_status;
    public String picStatus;
    public int reposts_count;
    public int comments_count;
    public int attitudes_count;
    public boolean isLongText;
    public int mlevel;
    public Visible visible;
    public long biz_feature;
    public int hasActionTypeCard;
    public String rid;
    public int userType;
    public String cardid;
    public int positive_recom_flag;
    public String gif_ids;
    public int is_show_bulletin;
    public String original_source;
    public List<PicUrls> pic_urls;
    public List<Annotations> annotations;
    public List<?> darwin_tags;
    public List<?> hot_weibo_tags;
    public List<?> text_tag_tips;


    @Override
    public boolean equals(Object another) {
        if (another == this) {
            return true;
        }else if (!(another instanceof Status)) {
            return false;
        }
        Status anotherStatus = (Status) another;
        return id == anotherStatus.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public int compareTo(Status another) {
        try {
            return (int) (Weibo.Date.SOURCE_FORMAT.parse(another.created_at).getTime() - Weibo.Date.SOURCE_FORMAT.parse(created_at).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
