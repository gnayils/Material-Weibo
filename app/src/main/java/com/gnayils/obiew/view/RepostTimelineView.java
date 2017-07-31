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
import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.bean.Repost;
import com.gnayils.obiew.weibo.bean.Reposts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gnayils on 08/04/2017.
 */

public class RepostTimelineView extends LoadMoreRecyclerView<Repost, RepostView> {

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
    }

    @Override
    public RepostView createView(ViewGroup parent, int viewType) {
        RepostView repostView = new RepostView(parent.getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        repostView.setLayoutParams(layoutParams);
        repostView.setRadius(0);
        repostView.setElevation(0);
        return repostView;
    }

    @Override
    public void bindView(final RepostView repostView, final Repost repost, int position) {
        repostView.show(repost);
        repostView.userAvatarView
                .avatarCircleImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.start(repostView.getContext(), repost.user);
            }
        });
    }
}
