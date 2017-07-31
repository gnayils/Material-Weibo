package com.gnayils.obiew.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
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
import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.bean.Comment;
import com.gnayils.obiew.weibo.bean.Comments;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gnayils on 08/04/2017.
 */

public class CommentTimelineView extends LoadMoreRecyclerView<Comment, CommentView> {


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
                ViewUtils.getColorByAttrId(context, R.attr.themeColorViewBackground), ViewUtils.getColorByAttrId(context, R.attr.themeColorDivideLine), ViewUtils.dp2px(getContext(), 64)));
        addItemDecoration(dividerItemDecoration);
        setLayoutManager(linearLayoutManager);
    }

    @Override
    public CommentView createView(ViewGroup parent, int viewType) {
        CommentView commentView = new CommentView(parent.getContext());
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        commentView.setLayoutParams(layoutParams);
        commentView.setRadius(0);
        commentView.setElevation(0);
        return commentView;
    }

    @Override
    public void bindView(final CommentView commentView, final Comment comment, int position) {
        commentView.show(comment);
        commentView.userAvatarView.avatarCircleImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UserProfileActivity.start(commentView.getContext(), comment.user);
            }
        });
    }
}
