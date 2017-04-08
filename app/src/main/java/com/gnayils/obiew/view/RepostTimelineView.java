package com.gnayils.obiew.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.weibo.bean.Repost;
import com.gnayils.obiew.weibo.bean.RepostTimeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gnayils on 08/04/2017.
 */

public class RepostTimelineView extends RecyclerView {

    private RecyclerViewAdapter recyclerViewAdapter;

    public RepostTimelineView(Context context) {
        this(context, null);
    }

    public RepostTimelineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RepostTimelineView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ViewUtils.createDividerDrawable(getContext(), ViewUtils.dp2px(getContext(), 1),
                getResources().getColor(android.R.color.white), getResources().getColor(R.color.colorDivider), ViewUtils.dp2px(getContext(), 64)));
        addItemDecoration(dividerItemDecoration);
        setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter();
        setAdapter(recyclerViewAdapter);
    }

    public void show(RepostTimeline repostTimeline) {
        recyclerViewAdapter.add(repostTimeline);
    }


    class RecyclerViewAdapter extends Adapter<RecyclerViewHolder> {

        private List<Repost> repostList = new ArrayList<>();

        public RecyclerViewAdapter() {

        }

        @Override public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerViewHolder holder = new RecyclerViewHolder(new RepostView(parent.getContext()));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            Repost repost = repostList.get(position);
            holder.repostView.show(repost);
        }

        @Override
        public int getItemCount() {
            return repostList.size();
        }

        public void add(RepostTimeline repostTimeline) {
            Set<Repost> repostSet = new TreeSet<>(repostList);
            repostSet.addAll(repostTimeline.reposts);
            repostList.clear();
            repostList.addAll(repostSet);
            notifyDataSetChanged();
        }
    }

    class RecyclerViewHolder extends ViewHolder {

        RepostView repostView;

        RecyclerViewHolder(RepostView repostView) {
            super(repostView);
            this.repostView = repostView;
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            this.repostView.setLayoutParams(layoutParams);
            this.repostView.setRadius(0);
            this.repostView.setElevation(0);
        }
    }
}
