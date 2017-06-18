package com.gnayils.obiew.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gnayils.obiew.R;
import com.gnayils.obiew.activity.UserProfileActivity;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.weibo.bean.Comment;
import com.gnayils.obiew.weibo.bean.Comments;

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
                getResources().getColor(android.R.color.white), getResources().getColor(R.color.black_alpha_1A), ViewUtils.dp2px(getContext(), 64)));
        addItemDecoration(dividerItemDecoration);
        setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter();
        setAdapter(recyclerViewAdapter);
    }

    public void show(boolean isLatest, Comments comments) {
        recyclerViewAdapter.add(isLatest, comments);
    }


    static class RecyclerViewAdapter extends LoadMoreAdapter<CommentViewHolder> {

        private List<Comment> commentList = new ArrayList<>();

        @Override
        public int getItemsCount() {
            return commentList.size();
        }

        @Override
        public CommentViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
            CommentView commentView = new CommentView(parent.getContext());
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            commentView.setLayoutParams(layoutParams);
            commentView.setRadius(0);
            commentView.setElevation(0);
            return new CommentViewHolder(commentView);
        }

        @Override
        public void onBindItemViewHolder(final CommentViewHolder holder, int position) {
            final Comment comment = commentList.get(position);
            holder.commentView.show(comment);
            holder.commentView.userAvatarView
                    .avatarCircleImageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    UserProfileActivity.start(holder.commentView.getContext(), comment.user);
                }
            });
        }

        @Override
        public CommentViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
            return new CommentViewHolder(new DefaultFooterView(parent, parent.getContext()));
        }

        @Override
        public void onBindFooterViewHolder(CommentViewHolder holder, int position) {
            holder.defaultFooterView.progressDrawable.stop();
            holder.defaultFooterView.progressDrawable.start();
        }

        public void add(boolean isLatest, Comments comments) {
            Set<Comment> commentSet = new TreeSet<>();
            if(isLatest) {
                commentList.clear();
                commentSet.addAll(comments.comments);
            } else {
                commentSet.addAll(commentList);
                commentSet.addAll(comments.comments);
                commentList.clear();
            }
            commentList.addAll(commentSet);
            notifyDataSetChanged();
        }
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        CommentView commentView;
        DefaultFooterView defaultFooterView;

        CommentViewHolder(CommentView commentView) {
            super(commentView);
            this.commentView = commentView;
        }

        CommentViewHolder(DefaultFooterView defaultFooterView) {
            super(defaultFooterView);
            this.defaultFooterView = defaultFooterView;
        }
    }
}
