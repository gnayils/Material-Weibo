package com.gnayils.obiew.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
 * Created by Gnayils on 08/04/2017.
 */

public class StatusTimelineView extends RecyclerView {

    private RecyclerViewAdapter recyclerViewAdapter;

    public StatusTimelineView(Context context) {
        this(context, null);
    }

    public StatusTimelineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusTimelineView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter();
        setAdapter(recyclerViewAdapter);
    }

    public void show(StatusTimeline statusTimeline) {
        recyclerViewAdapter.addTimeline(statusTimeline);
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecycleViewHolder> {

        private List<Status> statusList = new ArrayList<Status>();

        public RecyclerViewAdapter() {

        }

        @Override
        public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecycleViewHolder holder = new RecycleViewHolder(new StatusCardView(parent.getContext()));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecycleViewHolder holder, int position) {
            Status status = statusList.get(position);
            holder.statusCardView.show(status);
        }

        @Override
        public int getItemCount() {
            return statusList.size();
        }

        public void addTimeline(StatusTimeline statusTimeline) {
            Set<Status> statusSet = new TreeSet<Status>(statusList);
            statusSet.addAll(statusTimeline.statuses);
            statusList.clear();
            statusList.addAll(statusSet);
            notifyDataSetChanged();
        }

    }

    class RecycleViewHolder extends RecyclerView.ViewHolder {

        StatusCardView statusCardView;

        RecycleViewHolder(StatusCardView statusCardView) {
            super(statusCardView);
            this.statusCardView = statusCardView;
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(dp2px(statusCardView.getContext(), 8), dp2px(statusCardView.getContext(), 4), dp2px(statusCardView.getContext(), 8), dp2px(statusCardView.getContext(), 4));
            this.statusCardView.setLayoutParams(layoutParams);
            this.statusCardView.setRadius(dp2px(statusCardView.getContext(), 4));
        }
    }

}
