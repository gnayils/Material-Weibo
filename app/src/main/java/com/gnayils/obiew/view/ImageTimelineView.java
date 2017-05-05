package com.gnayils.obiew.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gnayils.obiew.R;
import com.gnayils.obiew.bmpldr.BitmapLoadAdapter;
import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.weibo.bean.PicUrls;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.StatusTimeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.gnayils.obiew.util.ViewUtils.dp2px;

/**
 * Created by Gnayils on 17/04/2017.
 */

public class ImageTimelineView extends LoadMoreRecyclerView {

    private ImageTimelineAdapter imageTimelineAdapter;

    public ImageTimelineView(Context context) {
        this(context, null);
    }

    public ImageTimelineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTimelineView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new GridLayoutManager(context, 3));
        imageTimelineAdapter = new ImageTimelineAdapter();
        setAdapter(imageTimelineAdapter);
        setPadding(dp2px(context, 6), 0, dp2px(context, 6), 0);
    }

    public void show(StatusTimeline statusTimeline) {
        imageTimelineAdapter.addTimeline(statusTimeline);
    }

    private class ImageTimelineAdapter extends LoadMoreRecyclerView.LoadMoreAdapter {

        List<PicUrls> picUrlsList = new ArrayList<>();
        List<Status> statusList = new ArrayList<>();

        @Override
        public int getActualItemCount() {
            return picUrlsList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateActualViewHolder(ViewGroup parent, int viewType) {
            PictureCardView pictureCardView = new PictureCardView(parent.getContext());
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(dp2px(pictureCardView.getContext(), 2), dp2px(pictureCardView.getContext(), 4), dp2px(pictureCardView.getContext(), 2), dp2px(pictureCardView.getContext(), 0));
            pictureCardView.setLayoutParams(layoutParams);
            return new ImageCardViewHolder(pictureCardView);
        }

        @Override
        public void onBindActualViewHolder(RecyclerView.ViewHolder holder, int position) {
            Glide.with(getContext()).load(picUrlsList.get(position).middleThumbnailPic()).into(((ImageCardViewHolder) holder).imageView);
        }

        public void addTimeline(StatusTimeline statusTimeline) {
            Set<Status> statusSet = new TreeSet<>(statusList);
            statusSet.addAll(statusTimeline.statuses);
            statusList.clear();
            statusList.addAll(statusSet);

            picUrlsList.clear();
            for(Status status : statusList) {
                if(status.pic_urls != null) {
                    picUrlsList.addAll(status.pic_urls);
                }
            }
            notifyDataSetChanged();
        }
    }

    class ImageCardViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        ImageCardViewHolder(PictureCardView pictureCardView) {
            super(pictureCardView);
            imageView = pictureCardView.imageView;
        }
    }
}
