package com.gnayils.obiew.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.StatusTimeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.gnayils.obiew.util.ViewUtils.dp2px;

/**
 * Created by Gnayils on 08/04/2017.
 */

public class StatusTimelineView extends LoadMoreRecyclerView {

    private StatusTimelineAdapter statusTimelineAdapter;

    public StatusTimelineView(Context context) {
        this(context, null);
    }

    public StatusTimelineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusTimelineView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new LinearLayoutManager(getContext()));
        statusTimelineAdapter = new StatusTimelineAdapter();
        setAdapter(statusTimelineAdapter);
    }

    public void show(StatusTimeline statusTimeline) {
        statusTimelineAdapter.addTimeline(statusTimeline);
    }

    private class StatusTimelineAdapter extends LoadMoreRecyclerView.LoadMoreAdapter {

        List<Status> statusList = new ArrayList<Status>();

        @Override
        public int getActualItemCount() {
            return statusList.size();
        }

        @Override
        public ViewHolder onCreateActualViewHolder(ViewGroup parent, int viewType) {
            StatusCardView statusCardView = new StatusCardView(parent.getContext());
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(dp2px(statusCardView.getContext(), 8), dp2px(statusCardView.getContext(), 4), dp2px(statusCardView.getContext(), 8), dp2px(statusCardView.getContext(), 4));
            statusCardView.setLayoutParams(layoutParams);
            statusCardView.setRadius(dp2px(statusCardView.getContext(), 4));
            return new StatusCardViewHolder(statusCardView);
        }

        @Override
        public void onBindActualViewHolder(ViewHolder holder, int position) {
            Status status = statusList.get(position);
            ((StatusCardViewHolder)holder).statusCardView.show(status);
        }

        public void addTimeline(StatusTimeline statusTimeline) {
            Set<Status> statusSet = new TreeSet<Status>(statusList);
            statusSet.addAll(statusTimeline.statuses);
            statusList.clear();
            statusList.addAll(statusSet);
            notifyDataSetChanged();
        }
    }

    class StatusCardViewHolder extends ViewHolder {

        StatusCardView statusCardView;

        StatusCardViewHolder(StatusCardView statusCardView) {
            super(statusCardView);
            this.statusCardView = statusCardView;
        }
    }
}
