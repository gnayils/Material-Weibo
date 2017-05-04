package com.gnayils.obiew.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.ViewGroup;

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
        setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        imageTimelineAdapter = new ImageTimelineAdapter();
        setAdapter(imageTimelineAdapter);
        setPadding(dp2px(context, 4), 0, dp2px(context, 4), 0);
    }

    public void show(StatusTimeline statusTimeline) {
        imageTimelineAdapter.addTimeline(statusTimeline);
    }

    private class ImageTimelineAdapter extends LoadMoreRecyclerView.LoadMoreAdapter {

        List<Status> statusList = new ArrayList<Status>();

        @Override
        public int getActualItemCount() {
            return 50;
        }

        @Override
        public RecyclerView.ViewHolder onCreateActualViewHolder(ViewGroup parent, int viewType) {
            CardView statusCardView = new CardView(parent.getContext());
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, dp2px(parent.getContext(), (int) (200 + Math.random() * 100)));
            layoutParams.setMargins(dp2px(statusCardView.getContext(), 4), dp2px(statusCardView.getContext(), 4), dp2px(statusCardView.getContext(), 4), dp2px(statusCardView.getContext(), 4));
            statusCardView.setLayoutParams(layoutParams);
            return new ImageCardViewHolder(statusCardView);
        }

        @Override
        public void onBindActualViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        public void addTimeline(StatusTimeline statusTimeline) {
            Set<Status> statusSet = new TreeSet<Status>(statusList);
            statusSet.addAll(statusTimeline.statuses);
            statusList.clear();
            statusList.addAll(statusSet);
            notifyDataSetChanged();
        }
    }

    class ImageCardViewHolder extends RecyclerView.ViewHolder {

        ImageCardViewHolder(CardView statusCardView) {
            super(statusCardView);
        }
    }
}
