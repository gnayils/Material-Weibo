package com.gnayils.obiew.weibo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Browser;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;
import com.gnayils.obiew.bmpldr.BitmapLoader;
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

    public static void decorate(StatusTimeline statusTimeline) {
        if(statusTimeline != null && statusTimeline.statuses != null) {
            for(Status status : statusTimeline.statuses) {
                status.setSpannableText(decorate(status.text));
                status.setSpannableSource(replaceUrlSpan((Spannable) Html.fromHtml(status.source)));
                if(status.retweeted_status != null && status.retweeted_status.text != null && !status.retweeted_status.text.isEmpty()) {
                    status.retweeted_status.setSpannableText(decorate("@" + status.retweeted_status.user.screen_name +  ": " + status.retweeted_status.text));
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
            spannableString = decorateWebUrls(text);
            decorateTopics(spannableString);
            decorateMentions(spannableString);
            decorateEmotions(spannableString);
        }
        return spannableString;
    }

    private static SpannableString decorateWebUrls(String text) {
        List<LinkSpan> webUrlLinkSpans = new ArrayList<>();
        Matcher httpMatcher;
        String replacement = "网页链接";
        while((httpMatcher = Patterns.WEB_URL.matcher(text)).find()) {
            String webUrl = httpMatcher.group();
            text = text.replace(webUrl, replacement);
            LinkSpan linkSpan = new LinkSpan(webUrl, httpMatcher.start(), httpMatcher.end() - (webUrl.length() - replacement.length()));
            webUrlLinkSpans.add(linkSpan);
        }
        SpannableString spannableString = new SpannableString(text);
        for(LinkSpan linkSpan : webUrlLinkSpans) {
            spannableString.setSpan(linkSpan, linkSpan.start, linkSpan.end, linkSpan.flag);
        }
        return spannableString;
    }

    private static void decorateTopics(SpannableString spannableString) {
        Linkify.addLinks(spannableString, Pattern.compile("#[^#]+#"), "com.gnayils.obiew.scheme.topic://");
        replaceUrlSpan(spannableString);
    }

    private static void decorateMentions(SpannableString spannableString) {
        Linkify.addLinks(spannableString, Pattern.compile("@[\\w\\u4e00-\\u9fa5]+"), "com.gnayils.obiew.scheme.mention://");
        replaceUrlSpan(spannableString);
    }

    private static void decorateEmotions(SpannableString spannableString) {
        String string =spannableString.toString();
        Matcher emotionKeyMatcher = Pattern.compile("\\[\\S+?\\]").matcher(string);
        while(emotionKeyMatcher.find()) {
            String emotionKey = emotionKeyMatcher.group();
            Bitmap bitmap = EmotionDB.get(emotionKey, 2);
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
            LinkSpan linkSpan = new LinkSpan(urlSpan.getURL(), start, end);
            spannable.removeSpan(urlSpan);
            spannable.setSpan(linkSpan, start, end, linkSpan.flag);
        }
        return spannable;
    }



    public static class LinkSpan extends TouchableSpan {

        public String url;
        public int start;
        public int end;
        public int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

        public LinkSpan(String url, int start, int end) {
            super(App.resources().getColor(R.color.colorLink), App.resources().getColor(R.color.colorLink), App.resources().getColor(R.color.colorLinkBackground));
            this.url = url;
            this.start = start;
            this.end = end;
        }

        @Override
        public void onClick(View widget) {
            Uri uri = Uri.parse(url);
            Context context = widget.getContext();
            if (uri.getScheme().startsWith("http")) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                context.startActivity(intent);
            }
        }
    }
}
