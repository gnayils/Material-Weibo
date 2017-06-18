package com.gnayils.obiew.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
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

    public void show(boolean isLatest, Statuses statuses) {
        imageTimelineAdapter.addTimeline(isLatest, statuses);
    }

    static class ImageTimelineAdapter extends LoadMoreRecyclerView.LoadMoreAdapter<ImageCardViewHolder> {

        List<PicUrls> picUrlsList = new ArrayList<>();
        List<Status> statusList = new ArrayList<>();

        @Override
        public int getItemsCount() {
            return picUrlsList.size();
        }

        @Override
        public ImageCardViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
            PictureCardView pictureCardView = new PictureCardView(parent.getContext());
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(dp2px(pictureCardView.getContext(), 2), dp2px(pictureCardView.getContext(), 4), dp2px(pictureCardView.getContext(), 2), dp2px(pictureCardView.getContext(), 0));
            pictureCardView.setLayoutParams(layoutParams);
            return new ImageCardViewHolder(pictureCardView);
        }

        @Override
        public void onBindItemViewHolder(ImageCardViewHolder holder, int position) {
            holder.imageView.setHintVisible(picUrlsList.get(position).isGif());
            Glide.with(holder.imageView.getContext()).load(picUrlsList.get(position).middle()).asBitmap().into(holder.imageView);
        }

        @Override
        public ImageCardViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
            return new ImageCardViewHolder(new DefaultFooterView(parent, parent.getContext()));
        }

        @Override
        public void onBindFooterViewHolder(ImageCardViewHolder holder, int position) {
            holder.defaultFooterView.progressDrawable.stop();
            holder.defaultFooterView.progressDrawable.start();
        }

        public void addTimeline(boolean isLatest, Statuses statuses) {
            Set<Status> statusSet = new TreeSet<>();
            if(isLatest) {
                statusList.clear();
                statusSet.addAll(statuses.statuses);
            } else {
                statusSet.addAll(statusList);
                statusSet.addAll(statuses.statuses);
                statusList.clear();
            }
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

    static class ImageCardViewHolder extends RecyclerView.ViewHolder {

        GiFHintImageView imageView;
        DefaultFooterView defaultFooterView;

        ImageCardViewHolder(PictureCardView pictureCardView) {
            super(pictureCardView);
            imageView = pictureCardView.imageView;
        }

        ImageCardViewHolder(DefaultFooterView defaultFooterView) {
            super(defaultFooterView);
            this.defaultFooterView = defaultFooterView;
        }
    }
}
