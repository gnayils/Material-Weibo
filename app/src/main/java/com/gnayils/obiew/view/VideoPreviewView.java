package com.gnayils.obiew.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.gnayils.obiew.R;
import com.gnayils.obiew.activity.UserProfileActivity;
import com.gnayils.obiew.activity.VideoActivity;
import com.gnayils.obiew.weibo.bean.URL;
import com.gnayils.obiew.weibo.bean.Video;

import static com.gnayils.obiew.util.ViewUtils.dp2px;

/**
 * Created by Gnayils on 14/05/2017.
 */

public class VideoPreviewView extends FrameLayout {

    public ForegroundImageView coverImageView;
    public ForegroundImageView playImageView;

    public Video video;

    public VideoPreviewView(@NonNull Context context) {
        this(context, null);
    }

    public VideoPreviewView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPreviewView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VideoPreviewView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        coverImageView = new ForegroundImageView(context);
        coverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        coverImageView.setBackgroundResource(R.drawable.bg_status_picture_thumbnail);
        coverImageView.setForegroundResource(R.drawable.fg_status_picture_thumbnail_mask);
        coverImageView.setClipToOutline(true);
        FrameLayout.LayoutParams coverImageViewLayoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        coverImageViewLayoutParams.gravity = Gravity.CENTER;
        coverImageView.setLayoutParams(coverImageViewLayoutParams);
        coverImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video != null) {
                    VideoActivity.start(getContext(), video);
                }
            }
        });

        playImageView = new ForegroundImageView(context);
        playImageView.setScaleType(ImageView.ScaleType.CENTER);
        playImageView.setImageResource(R.drawable.ic_play);
        playImageView.setImageTintList(new ColorStateList(
                new int[][]{
                        new int[] { }
                },
                new int[]{
                        Color.WHITE
                }));
        playImageView.setClickable(false);
        FrameLayout.LayoutParams playImageViewLayoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        playImageViewLayoutParams.gravity = Gravity.CENTER;
        playImageView.setLayoutParams(playImageViewLayoutParams);


        addView(coverImageView);
        addView(playImageView);
    }

    public void show(URL url) {
        if(url == null || url.video == null) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
            video = url.video;
            Glide.with(getContext()).load(video.cover_img).into(coverImageView);
        }
    }
}
