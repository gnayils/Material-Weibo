package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.interfaces.BasePresenter;
import com.gnayils.obiew.interfaces.CommentInterface;
import com.gnayils.obiew.interfaces.RepostInterface;
import com.gnayils.obiew.presenter.CommentPresenter;
import com.gnayils.obiew.presenter.RepostPresenter;
import com.gnayils.obiew.util.Popup;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.view.CommentTimelineView;
import com.gnayils.obiew.view.LoadMoreRecyclerView;
import com.gnayils.obiew.view.RepostTimelineView;
import com.gnayils.obiew.view.StatusCardView;
import com.gnayils.obiew.weibo.bean.CommentTimeline;
import com.gnayils.obiew.weibo.bean.RepostTimeline;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.service.CommentService;
import com.gnayils.obiew.weibo.service.StatusService;
import com.gnayils.obiew.weibo.service.SubscriberAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StatusDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String ARGS_KEY_STATUS = "ARGS_KEY_STATUS";

    private Status status;

    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.status_card_view) StatusCardView statusCardView;
    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.text_view_like_count) TextView likeCountTextView;
    @Bind(R.id.view_pager) ViewPager viewPager;

    private CommentService commentService = new CommentService();
    private StatusService statusService = new StatusService();
    private CommentTimelineView commentTimelineView;
    private RepostTimelineView repostTimelineView;

    private int appBarCurrentVerticalOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("微博正文");
        status = (Status) getIntent().getSerializableExtra(ARGS_KEY_STATUS);
        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                return 0 > appBarCurrentVerticalOffset;
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        appBarLayout.addOnOffsetChangedListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });
        statusCardView.setBackgroundColor(Color.TRANSPARENT);
        statusCardView.commentLayout.setVisibility(View.GONE);
        statusCardView.show(status);
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);
        likeCountTextView.setText(status.attitudes_count + " 赞");
        commentTimelineView = new CommentTimelineView(this);
        repostTimelineView = new RepostTimelineView(this);

        commentTimelineView.setOnLoadMoreListener(new LoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                showCommentTimeline(false);
            }
        });
        repostTimelineView.setOnLoadMoreListener(new LoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                showRepostTimeline(false);
            }
        });
        onRefresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        statusService.unsubscribe();
        commentService.unsubscribe();
    }

    @Override
    public void onRefresh() {
        showCommentTimeline(true);
        showRepostTimeline(true);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        appBarCurrentVerticalOffset = verticalOffset;
    }

    private void showCommentTimeline(final boolean loadLatest) {
        commentService.showCommentTimeline(status, loadLatest, new SubscriberAdapter<CommentTimeline>(){

            @Override
            public void onSubscribe() {
                if(loadLatest) {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            public void onError(Throwable e) {
                Popup.toast("获取微博评论失败: " + e.getMessage());
            }

            @Override
            public void onNext(CommentTimeline commentTimeline) {
                commentTimelineView.show(commentTimeline);
            }

            @Override
            public void onUnsubscribe() {
                if(loadLatest) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void showRepostTimeline(final boolean loadLatest) {
        statusService.showRepostTimeline(status, loadLatest, new SubscriberAdapter<RepostTimeline>(){

            @Override
            public void onSubscribe() {
                if(loadLatest) {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            public void onError(Throwable e) {
                Popup.toast("获取微博转发失败: " + e.getMessage());
            }

            @Override
            public void onNext(RepostTimeline repostTimeline) {
                repostTimelineView.show(repostTimeline);
            }

            @Override
            public void onUnsubscribe() {
                if(loadLatest) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    class ViewPagerAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            View view = null;
            switch (position) {
                case 0:
                    view = repostTimelineView;
                    break;
                case 1:
                    view = commentTimelineView;
                    break;
            }
            if(view != null) {
                collection.addView(view);
            }
            return view;
        }
        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return status.reposts_count + " 转发";
                case 1: return status.comments_count + " 评论";
                default: return null;
            }
        }
    }

    public static void start(Context context, Status status) {
        Intent intent = new Intent(context, StatusDetailActivity.class);
        intent.putExtra(ARGS_KEY_STATUS, status);
        context.startActivity(intent);
    }
}
