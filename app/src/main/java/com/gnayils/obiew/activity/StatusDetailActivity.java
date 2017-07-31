package com.gnayils.obiew.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.gnayils.obiew.R;
import com.gnayils.obiew.util.Popup;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.view.CommentTimelineView;
import com.gnayils.obiew.view.LoadMoreRecyclerView;
import com.gnayils.obiew.view.RepostTimelineView;
import com.gnayils.obiew.view.StatusPicturesView;
import com.gnayils.obiew.view.StatusView;
import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.bean.Comments;
import com.gnayils.obiew.weibo.bean.Reposts;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.service.CommentService;
import com.gnayils.obiew.weibo.service.StatusService;
import com.gnayils.obiew.weibo.service.SubscriberAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StatusDetailActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, StatusPicturesView.OnPictureItemClickListener {

    public static final String ARGS_KEY_STATUS = "ARGS_KEY_STATUS";
    public static final String ARGS_KEY_STATUS_VIEW_EXPANDED = "ARGS_KEY_STATUS_VIEW_EXPANDED";
    public static final String ARGS_KEY_COMMENT_TAB_SELECTED = "ARGS_KEY_COMMENT_TAB_SELECTED";

    private Status status;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.status_view)
    StatusView statusView;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.text_view_like_count)
    TextView likeCountTextView;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.content_scrim)
    View contentScrimView;
    @Bind(R.id.fam_actions)
    FloatingActionsMenu floatingActionsMenu;
    @Bind(R.id.fab_comment)
    FloatingActionButton commentFab;
    @Bind(R.id.fab_repost)
    FloatingActionButton repostFab;
    @Bind(R.id.fab_like)
    FloatingActionButton likeFab;

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
        status = (Status) getIntent().getSerializableExtra(ARGS_KEY_STATUS);
        appBarLayout.setExpanded(getIntent().getBooleanExtra(ARGS_KEY_STATUS_VIEW_EXPANDED, true));
        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                return 0 > appBarCurrentVerticalOffset;
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ViewUtils.getColorByAttrId(this, R.attr.themeColorSecondaryText));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ViewUtils.getColorByAttrId(this, R.attr.themeColorViewBackground));
        appBarLayout.addOnOffsetChangedListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        statusView.setRadius(0);
        statusView.commentLayout.setVisibility(View.GONE);
        statusView.show(status);
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.setCurrentItem(getIntent().getBooleanExtra(ARGS_KEY_COMMENT_TAB_SELECTED, true) ? 1 : 0);
        tabLayout.setupWithViewPager(viewPager);
        likeCountTextView.setText(Weibo.format.commentCount(status.attitudes_count) + " 赞");
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

        statusView.userAvatarView.avatarCircleImageView.setOnClickListener(this);
        statusView.retweetedStatusView.setOnClickListener(this);
        statusView.statusPicturesView.setOnPictureItemClickListener(this);
        statusView.retweetedStatusPicturesView.setOnPictureItemClickListener(this);
        statusView.videoPreviewView.coverImageView.setOnClickListener(this);
        statusView.retweetedVideoPreviewView.coverImageView.setOnClickListener(this);

        repostFab.setIconDrawable(ViewUtils.getTintedDrawable(this, R.drawable.ic_repost, Color.WHITE));
        commentFab.setIconDrawable(ViewUtils.getTintedDrawable(this, R.drawable.ic_comment, Color.WHITE));
        likeFab.setIconDrawable(ViewUtils.getTintedDrawable(this, R.drawable.ic_like, Color.WHITE));
        floatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(contentScrimView, "alpha", 0, 1).setDuration(200);
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        contentScrimView.setVisibility(View.VISIBLE);
                    }
                });
                objectAnimator.start();
            }

            @Override
            public void onMenuCollapsed() {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(contentScrimView, "alpha", 1, 0).setDuration(200);
                objectAnimator.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        contentScrimView.setVisibility(View.GONE);
                    }
                });
                objectAnimator.start();
            }
        });
        contentScrimView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenu.collapse();
            }
        });
        commentFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenu.collapse();
                PublishActivity.startForCommentPublishment(StatusDetailActivity.this, status);
            }
        });
        repostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenu.collapse();
                PublishActivity.startForRepostPublishment(StatusDetailActivity.this, status);
            }
        });
        likeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenu.collapse();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_status_detail_option_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_collect);
        menuItem.setIcon(ViewUtils.tintDrawable(menuItem.getIcon(), Color.WHITE));
        menuItem = menu.findItem(R.id.action_copy_link);
        menuItem.setIcon(ViewUtils.tintDrawable(menuItem.getIcon(), Color.WHITE));
        menuItem = menu.findItem(R.id.action_share);
        menuItem.setIcon(ViewUtils.tintDrawable(menuItem.getIcon(), Color.WHITE));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_collect:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
        commentService.showCommentTimeline(status, loadLatest, new SubscriberAdapter<Comments>() {

            @Override
            public void onSubscribe() {
                if (loadLatest) {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            public void onError(Throwable e) {
                Popup.toast("加载评论失败: " + e.getMessage());
            }

            @Override
            public void onNext(Comments comments) {
                commentTimelineView.appendData(loadLatest, comments.comments);
            }

            @Override
            public void onUnsubscribe() {
                if (loadLatest) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void showRepostTimeline(final boolean loadLatest) {
        statusService.showRepostTimeline(status, loadLatest, new SubscriberAdapter<Reposts>() {

            @Override
            public void onSubscribe() {
                if (loadLatest) {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            public void onError(Throwable e) {
                Popup.toast("加载转发失败: " + e.getMessage());
            }

            @Override
            public void onNext(Reposts reposts) {
                repostTimelineView.appendData(loadLatest, reposts.reposts);
            }

            @Override
            public void onUnsubscribe() {
                if (loadLatest) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == statusView.userAvatarView.avatarCircleImageView) {
            UserProfileActivity.start(v.getContext(), status.user);
        } else if (v == statusView.retweetedStatusView) {
            if (status.retweeted_status.user != null) {
                StatusDetailActivity.start(v.getContext(), status.retweeted_status);
            }
        } else if (v == statusView.videoPreviewView.coverImageView) {
            PlayerActivity.start(v.getContext(), status.videoUrls.video);
        } else if (v == statusView.retweetedVideoPreviewView.coverImageView) {
            PlayerActivity.start(v.getContext(), status.retweeted_status.videoUrls.video);
        }
    }

    @Override
    public void onPictureItemClick(StatusPicturesView view, View v, int index) {
        if (view == statusView.statusPicturesView) {
            PicturePagerActivity.start(v.getContext(), index, status.pic_urls);
        } else if (view == statusView.retweetedStatusPicturesView) {
            PicturePagerActivity.start(v.getContext(), index, status.retweeted_status.pic_urls);
        }
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
            if (view != null) {
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
            switch (position) {
                case 0:
                    return Weibo.format.commentCount(status.reposts_count) + " 转发";
                case 1:
                    return Weibo.format.commentCount(status.comments_count) + " 评论";
                default:
                    return null;
            }
        }
    }

    public static void start(Context context, Status status, boolean detailViewExpanded, boolean commentTabSelected) {
        Intent intent = new Intent(context, StatusDetailActivity.class);
        intent.putExtra(ARGS_KEY_STATUS, status);
        intent.putExtra(ARGS_KEY_STATUS_VIEW_EXPANDED, detailViewExpanded);
        intent.putExtra(ARGS_KEY_COMMENT_TAB_SELECTED, commentTabSelected);
        context.startActivity(intent);
    }

    public static void start(Context context, Status status) {
        start(context, status, true, true);
    }
}
