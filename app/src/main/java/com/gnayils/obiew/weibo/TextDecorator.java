package com.gnayils.obiew.weibo;

import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.text.util.Linkify;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;
import com.gnayils.obiew.view.CenteredImageSpan;
import com.gnayils.obiew.weibo.bean.Comment;
import com.gnayils.obiew.weibo.bean.CommentTimeline;
import com.gnayils.obiew.weibo.bean.Repost;
import com.gnayils.obiew.weibo.bean.RepostTimeline;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.StatusTimeline;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gnayils on 02/02/2017.
 */

public class TextDecorator {

    public static final Pattern URL_PATTERN = Pattern.compile(WeiboSpan.HTTP_MATCHER);

    public static final Pattern EMOTION_PATTERN = Pattern.compile("\\[\\S+?\\]");

    public static void decorate(StatusTimeline statusTimeline) {
        if(statusTimeline != null && statusTimeline.statuses != null) {
            for(Status status : statusTimeline.statuses) {
                status.setSpannableText(decorate(status.text));
                status.setSpannableSource(replaceUrlSpan((Spannable) Html.fromHtml(status.source)));
                if(status.retweeted_status != null && status.retweeted_status.text != null && !status.retweeted_status.text.isEmpty()) {
                    String username = "";
                    if(status.retweeted_status.user != null) {
                        username = "@" + status.retweeted_status.user.screen_name + ": ";
                    }
                    status.retweeted_status.setSpannableText(decorate(username + status.retweeted_status.text));
                }
            }
        }
    }

    public static void decorate(CommentTimeline commentTimeline) {
        if(commentTimeline != null && commentTimeline.comments != null) {
            for(Comment comment : commentTimeline.comments) {
                comment.setSpannableText(decorate(comment.text));
            }
        }
    }

    public static void decorate(RepostTimeline repostTimeline) {
        if(repostTimeline != null && repostTimeline.reposts != null) {
            for(Repost repost : repostTimeline.reposts) {
                repost.setSpannableText(decorate(repost.text));
            }
        }
    }

    public static SpannableString decorate(String text) {
        SpannableString spannableString = null;
        if(text != null && !text.isEmpty()) {
            spannableString = decorateURLs(text);
            decorateTopics(spannableString);
            decorateMentions(spannableString);
            decorateEmotions(spannableString);
        }
        return spannableString;
    }

    private static SpannableString decorateURLs(String text) {
        List<WeiboSpan> weiboSpen = new ArrayList<>();
        Matcher httpMatcher;
        String replacement = "网页链接";
        while((httpMatcher = URL_PATTERN.matcher(text)).find()) {
            String webURL = httpMatcher.group();
            text = text.replace(webURL, replacement);
            WeiboSpan weiboSpan = new WeiboSpan(webURL, httpMatcher.start(), httpMatcher.end() - (webURL.length() - replacement.length()));
            weiboSpen.add(weiboSpan);
        }
        SpannableString spannableString = new SpannableString(text);
        for(WeiboSpan weiboSpan : weiboSpen) {
            spannableString.setSpan(weiboSpan, weiboSpan.start, weiboSpan.end, weiboSpan.flag);
        }
        return spannableString;
    }

    private static void decorateTopics(SpannableString spannableString) {
        Linkify.addLinks(spannableString, Pattern.compile(WeiboSpan.TOPIC_REGEX), WeiboSpan.TOPIC_SCHEME + WeiboSpan.SCHEME_SEPARATOR);
        replaceUrlSpan(spannableString);
    }

    private static void decorateMentions(SpannableString spannableString) {
        Linkify.addLinks(spannableString, Pattern.compile(WeiboSpan.MENTION_REGEX), WeiboSpan.MENTION_SCHEME + WeiboSpan.SCHEME_SEPARATOR);
        replaceUrlSpan(spannableString);
    }

    private static void decorateEmotions(SpannableString spannableString) {
        String string =spannableString.toString();
        Matcher emotionKeyMatcher = EMOTION_PATTERN.matcher(string);
        while(emotionKeyMatcher.find()) {
            String emotionKey = emotionKeyMatcher.group();
            Bitmap bitmap = EmotionDB.get(emotionKey, App.resources().getDimension(R.dimen.emotion_size_in_text));
            if(bitmap == null) {
                continue;
            } else {
                spannableString.setSpan(new CenteredImageSpan(App.context(), bitmap), emotionKeyMatcher.start(), emotionKeyMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public static Spannable replaceUrlSpan(Spannable spannable) {
        URLSpan[] urlSpans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (URLSpan urlSpan : urlSpans) {
            int start = spannable.getSpanStart(urlSpan);
            int end = spannable.getSpanEnd(urlSpan);
            WeiboSpan linkSpan = new WeiboSpan(urlSpan.getURL(), start, end);
            spannable.removeSpan(urlSpan);
            spannable.setSpan(linkSpan, start, end, linkSpan.flag);
        }
        return spannable;
    }



}
