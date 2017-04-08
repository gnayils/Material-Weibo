package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gnayils.obiew.R;
import com.gnayils.obiew.fragment.CommentTimelineFragment;
import com.gnayils.obiew.fragment.RepostTimelineFragment;
import com.gnayils.obiew.interfaces.BasePresenter;
import com.gnayils.obiew.interfaces.CommentInterface;
import com.gnayils.obiew.interfaces.RepostInterface;
import com.gnayils.obiew.presenter.CommentPresenter;
import com.gnayils.obiew.presenter.RepostPresenter;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.view.StatusCardView;
import com.gnayils.obiew.weibo.bean.CommentTimeline;
import com.gnayils.obiew.weibo.bean.RepostTimeline;
import com.gnayils.obiew.weibo.bean.Status;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StatusDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, CommentInterface.View, RepostInterface.View {

    public static final String ARGS_KEY_STATUS = "ARGS_KEY_STATUS";

    private Status status;

    @Bind(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.app_bar_layout)
    protected AppBarLayout appBarLayout;
    @Bind(R.id.status_card_view)
    protected StatusCardView statusCardView;
    @Bind(R.id.tab_layout)
    protected TabLayout tabLayout;
    @Bind(R.id.view_pager)
    protected ViewPager viewPager;
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    private CommentInterface.Presenter commentPresenter;
    private CommentTimelineFragment commentTimelineFragment;
    private RepostInterface.Presenter repostPresenter;
    private RepostTimelineFragment repostTimelineFragment;

    private int appBarCurrentVerticalOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = (Status) getIntent().getSerializableExtra(ARGS_KEY_STATUS);
        setContentView(R.layout.activity_status_detail);
        ButterKnife.bind(this);
        swipeRefreshLayout.setProgressViewOffset(false, ViewUtils.getStatusBarHeight(this) + ViewUtils.getActionBarHeight(this), ViewUtils.getStatusBarHeight(this) + ViewUtils.getActionBarHeight(this) * 2);
        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                return 0 > appBarCurrentVerticalOffset;
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                commentPresenter.loadCommentTimeline(status.id, true);
                repostPresenter.loadRepostTimeline(status.id, true);
            }
        });
        appBarLayout.addOnOffsetChangedListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });
        statusCardView.setBackgroundColor(Color.TRANSPARENT);
        statusCardView.commentLayout.setVisibility(View.GONE);
        statusCardView.show(status);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        commentTimelineFragment = CommentTimelineFragment.newInstance();
        commentPresenter = new CommentPresenter(this);
        repostTimelineFragment = RepostTimelineFragment.newInstance();
        repostPresenter = new RepostPresenter(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        this.commentPresenter.unsubscribe();
        this.repostPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(BasePresenter presenter) {
        if(presenter instanceof CommentPresenter) {
            this.commentPresenter = (CommentInterface.Presenter) presenter;
        } else if(presenter instanceof RepostPresenter) {
            this.repostPresenter = (RepostInterface.Presenter) presenter;
        }
    }

    @Override
    public void show(RepostTimeline repostTimeline) {
        repostTimelineFragment.show(repostTimeline);
    }

    @Override
    public void showRepostLoadingIndicator(final boolean refreshing) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(refreshing);
            }
        });
    }

    @Override
    public void show(CommentTimeline commentTimeline) {
        commentTimelineFragment.show(commentTimeline);
    }

    @Override
    public void showCommentLoadingIndicator(final boolean refreshing) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(refreshing);
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        appBarCurrentVerticalOffset = verticalOffset;
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int i) {
            switch(i) {
                case 0:
                    return repostTimelineFragment;
                case 1:
                    return commentTimelineFragment;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return status.reposts_count + "转发";
                case 1: return status.comments_count + "评论";
            }
            return "";
        }
    }

    public static void start(Context context, Status status) {
        Intent intent = new Intent(context, StatusDetailActivity.class);
        intent.putExtra(ARGS_KEY_STATUS, status);
        context.startActivity(intent);
    }
}
