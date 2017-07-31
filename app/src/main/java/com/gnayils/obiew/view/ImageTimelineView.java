package com.gnayils.obiew.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.bean.PicUrls;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.Statuses;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.gnayils.obiew.util.ViewUtils.dp2px;

/**
 * Created by Gnayils on 17/04/2017.
 */

public class ImageTimelineView extends LoadMoreRecyclerView<PicUrls, PictureCardView> {

    public ImageTimelineView(Context context) {
        this(context, null);
    }

    public ImageTimelineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTimelineView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new GridLayoutManager(context, 3));
        setPadding(dp2px(context, 6), 0, dp2px(context, 6), 0);
    }

    @Override
    public PictureCardView createView(ViewGroup parent, int viewType) {
        PictureCardView pictureCardView = new PictureCardView(parent.getContext());
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp2px(pictureCardView.getContext(), 2), dp2px(pictureCardView.getContext(), 4), dp2px(pictureCardView.getContext(), 2), dp2px(pictureCardView.getContext(), 0));
        pictureCardView.setLayoutParams(layoutParams);
        return pictureCardView;
    }

    @Override
    public void bindView(PictureCardView pictureCardView, PicUrls picUrls, int position) {
        pictureCardView.imageView.setHintVisible(picUrls.isGif());
        Glide.with(getContext()).load(picUrls.middle()).asBitmap().into(pictureCardView.imageView);
    }
}
