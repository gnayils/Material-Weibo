package com.gnayils.obiew.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.gnayils.obiew.R;
import com.gnayils.obiew.activity.UserProfileActivity;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.weibo.bean.Repost;
import com.gnayils.obiew.weibo.bean.Reposts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gnayils on 08/04/2017.
 */

public class RepostTimelineView extends LoadMoreRecyclerView {

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
                ViewUtils.getColorByAttrId(context, R.attr.themeColorViewBackground), ViewUtils.getColorByAttrId(context, R.attr.themeColorDivideLine), ViewUtils.dp2px(getContext(), 64)));
        addItemDecoration(dividerItemDecoration);
        setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter();
        setAdapter(recyclerViewAdapter);
    }

    public void show(boolean isLatest, Reposts reposts) {
        recyclerViewAdapter.add(isLatest, reposts);
    }


    static class RecyclerViewAdapter extends LoadMoreAdapter<RepostViewHolder> {

        private List<Repost> repostList = new ArrayList<>();

        @Override
        public int getItemsCount() {
            return repostList.size();
        }

        @Override
        public RepostViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
            RepostView repostView = new RepostView(parent.getContext());
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            repostView.setLayoutParams(layoutParams);
            repostView.setRadius(0);
            repostView.setElevation(0);
            return new RepostViewHolder(repostView);
        }

        @Override
        public void onBindItemViewHolder(final RepostViewHolder holder, int position) {
            final Repost repost = repostList.get(position);
            holder.repostView.show(repost);
            holder.repostView.userAvatarView
                    .avatarCircleImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserProfileActivity.start(holder.repostView.getContext(), repost.user);
                }
            });
        }

        @Override
        public RepostViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
            return new RepostViewHolder(new DefaultFooterView(parent, parent.getContext()));
        }

        @Override
        public void onBindFooterViewHolder(RepostViewHolder holder, int position) {
            holder.defaultFooterView.progressDrawable.stop();
            holder.defaultFooterView.progressDrawable.start();
        }

        public void add(boolean isLatest, Reposts reposts) {
            Set<Repost> repostSet = new TreeSet<>();
            if (isLatest) {
                repostList.clear();
                repostSet.addAll(reposts.reposts);
            } else {
                repostSet.addAll(repostList);
                repostSet.addAll(reposts.reposts);
                repostList.clear();
            }
            repostList.addAll(repostSet);
            notifyDataSetChanged();
        }
    }

    static class RepostViewHolder extends ViewHolder {

        RepostView repostView;
        DefaultFooterView defaultFooterView;

        RepostViewHolder(RepostView repostView) {
            super(repostView);
            this.repostView = repostView;
        }

        RepostViewHolder(DefaultFooterView defaultFooterView) {
            super(defaultFooterView);
            this.defaultFooterView = defaultFooterView;
        }
    }
}
