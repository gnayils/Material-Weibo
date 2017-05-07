package com.gnayils.obiew.weibo.bean;

import android.text.SpannableString;

import com.gnayils.obiew.weibo.TextDecorator;
import com.gnayils.obiew.weibo.Weibo;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gnayils on 18/03/2017.
 */

public class Repost implements Comparable<Repost>, Serializable {

    public String created_at;
    public long id;
    public String mid;
    public String idstr;
    public String text;
    private transient SpannableString spannableText;
    public int source_allowclick;
    public int source_type;
    public String source;
    public boolean favorited;
    public boolean truncated;
    public String in_reply_to_status_id;
    public String in_reply_to_user_id;
    public String in_reply_to_screen_name;
    public Object geo;
    public User user;
    public Status retweeted_status;
    public int reposts_count;
    public int comments_count;
    public int attitudes_count;
    public boolean isLongText;
    public int mlevel;
    public Visible visible;
    public int biz_feature;
    public int hasActionTypeCard;
    public int userType;
    public String cardid;
    public int positive_recom_flag;
    public String gif_ids;
    public int is_show_bulletin;
    public List<PicUrls> pic_urls;
    public List<Annotations> annotations;
    public List<?> darwin_tags;
    public List<?> hot_weibo_tags;
    public List<?> text_tag_tips;

    public SpannableString getSpannableText() {
        if(spannableText == null) {
            spannableText = TextDecorator.decorate(text);
        }
        return spannableText;
    }

    public void setSpannableText(SpannableString spannableText) {
        this.spannableText = spannableText;
    }

    @Override
    public boolean equals(Object another) {
        if (another == this) {
            return true;
        } else if (!(another instanceof Repost)) {
            return false;
        }
        Repost anotherRepost = (Repost) another;
        return id == anotherRepost.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Repost another) {
        try {
            return (int) (Weibo.Date.SOURCE_FORMAT.parse(another.created_at).getTime() - Weibo.Date.SOURCE_FORMAT.parse(created_at).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
