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
import com.gnayils.obiew.weibo.bean.Comment;
import com.gnayils.obiew.weibo.bean.CommentTimeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gnayils on 08/04/2017.
 */

public class CommentTimelineView extends LoadMoreRecyclerView {

    private RecyclerViewAdapter recyclerViewAdapter;

    public CommentTimelineView(Context context) {
        this(context, null);
    }

    public CommentTimelineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentTimelineView(Context context, @Nullable AttributeSet attrs, int defStyle) {
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

    public void show(CommentTimeline commentTimeline) {
        recyclerViewAdapter.add(commentTimeline);
    }


    class RecyclerViewAdapter extends LoadMoreAdapter {

        private List<Comment> commentList = new ArrayList<>();

        public RecyclerViewAdapter() {

        }

        @Override
        public int getActualItemCount() {
            return commentList.size();
        }

        @Override
        public ViewHolder onCreateActualViewHolder(ViewGroup parent, int viewType) {
            return new CommentViewHolder(new CommentView(parent.getContext()));
        }

        @Override
        public void onBindActualViewHolder(ViewHolder holder, int position) {
            Comment comment = commentList.get(position);
            ((CommentViewHolder)holder).commentView.show(comment);
        }

        public void add(CommentTimeline commentTimeline) {
            Set<Comment> commentSet = new TreeSet<>(commentList);
            commentSet.addAll(commentTimeline.comments);
            commentList.clear();
            commentList.addAll(commentSet);
            notifyDataSetChanged();
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        CommentView commentView;

        CommentViewHolder(CommentView commentView) {
            super(commentView);
            this.commentView = commentView;
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            this.commentView.setLayoutParams(layoutParams);
            this.commentView.setRadius(0);
            this.commentView.setElevation(0);
        }
    }
}
