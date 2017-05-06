package com.gnayils.obiew.weibo.bean;

import android.text.SpannableString;

import com.gnayils.obiew.weibo.TextDecorator;
import com.gnayils.obiew.weibo.Weibo;

import java.io.Serializable;
import java.text.ParseException;

/**
 * Created by Gnayils on 12/03/2017.
 */

public class Comment implements Comparable<Comment>, Serializable{

    public String created_at;
    public long id;
    public long rootid;
    public int floor_number;
    public String text;
    private transient SpannableString spannableText;
    public int source_allowclick;
    public int source_type;
    public String source;
    public User user;
    public String mid;
    public String idstr;
    public Status status;
    public Comment reply_comment;
    public String reply_original_text;

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
        }else if (!(another instanceof Comment)) {
            return false;
        }
        Comment anotherComment = (Comment) another;
        return id == anotherComment.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public int compareTo(Comment another) {
        try {
            return (int) (Weibo.Date.SOURCE_FORMAT.parse(another.created_at).getTime() - Weibo.Date.SOURCE_FORMAT.parse(created_at).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
