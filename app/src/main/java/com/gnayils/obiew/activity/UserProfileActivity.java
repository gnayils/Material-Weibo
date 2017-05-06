package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gnayils.obiew.R;
import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.interfaces.BasePresenter;
import com.gnayils.obiew.interfaces.StatusInterface;
import com.gnayils.obiew.presenter.StatusPresenter;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.view.ImageTimelineView;
import com.gnayils.obiew.view.AvatarView;
import com.gnayils.obiew.view.LoadMoreRecyclerView;
import com.gnayils.obiew.view.StatusTimelineView;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.StatusTimeline;
import com.gnayils.obiew.weibo.bean.User;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gnayils on 26/03/2017.
 */

public class UserProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, StatusInterface.View {

    public static final String ARGS_KEY_USER = "ARGS_KEY_USER";

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.image_view_cover)
    protected ImageView coverImageView;
    @Bind(R.id.avatar_view)
    protected AvatarView avatarView;
    @Bind(R.id.text_view_screen_name)
    protected TextView screenNameTextView;
    @Bind(R.id.text_view_description)
    protected TextView descriptionTextView;
    @Bind(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.app_bar_layout)
    protected AppBarLayout appBarLayout;
    @Bind(R.id.collapsing_toolbar_layout)
    protected CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.tab_layout)
    protected TabLayout tabLayout;
    @Bind(R.id.view_pager)
    protected ViewPager viewPager;


    protected StatusTimelineView statusTimelineView;
    protected ImageTimelineView imageTimelineView;

    private StatusInterface.Presenter statusPresenter;

    private int appBarCurrentVerticalOffset;

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        user = (User) getIntent().getSerializableExtra(ARGS_KEY_USER);
        swipeRefreshLayout.setProgressViewOffset(false, -swipeRefreshLayout.getProgressCircleDiameter(), ViewUtils.getStatusBarHeight(this) * 2);
        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                return 0 > appBarCurrentVerticalOffset;
            }
        });
        appBarLayout.addOnOffsetChangedListener(this);
        collapsingToolbarLayout.setTitle(user.screen_name);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if(user.cover_image_phone != null && !user.cover_image_phone.isEmpty()) {
            String coverImageUrl = user.cover_image_phone;
            if(coverImageUrl.indexOf(";") != 0) {
                String[] coverImageUrls = coverImageUrl.split(";");
                coverImageUrl = coverImageUrls[(int) (Math.random() * coverImageUrls.length)];
            }
            Glide.with(this).load(coverImageUrl).placeholder(R.drawable.bg_cover_default).into(coverImageView);
        }
        Glide.with(this).load(user.avatar_large).into(avatarView.avatarCircleImageView);
        screenNameTextView.setText(user.screen_name);
        descriptionTextView.setText(user.description);

        statusTimelineView = new StatusTimelineView(this);
        imageTimelineView = new ImageTimelineView(this);
        statusPresenter = new StatusPresenter(this);
        viewPager.setAdapter(new ViewPagerAdapter());
        tabLayout.setupWithViewPager(viewPager);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                statusPresenter.loadStatusTimeline(true, user, Status.FEATURE_ALL);
                statusPresenter.loadStatusTimeline(true, user, Status.FEATURE_IMAGE);
            }
        });
        statusTimelineView.setOnLoadMoreListener(new LoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                statusPresenter.loadStatusTimeline(false, user, Status.FEATURE_ALL);
            }
        });
        imageTimelineView.setOnLoadMoreListener(new LoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                statusPresenter.loadStatusTimeline(false, user, Status.FEATURE_IMAGE);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        statusPresenter.unsubscribe();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        appBarCurrentVerticalOffset = verticalOffset;
        tabLayout.getBackground().setAlpha((int) (Math.abs(verticalOffset) / (appBarLayout.getTotalScrollRange() / 255d)));
        if(Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
            tabLayout.setElevation(ViewUtils.dp2px(this, 6));
        } else if(verticalOffset == 0) {
            tabLayout.setElevation(0);
        }
    }

    @Override
    public void setPresenter(BasePresenter presenter) {
        this.statusPresenter = (StatusInterface.Presenter) presenter;
    }

    @Override
    public void show(StatusTimeline statusTimeline, int feature) {
        if(feature == Status.FEATURE_ALL) {
            statusTimelineView.show(statusTimeline);
        } else if(feature == Status.FEATURE_IMAGE) {
            imageTimelineView.show(statusTimeline);
        }
    }

    @Override
    public void showStatusLoadingIndicator(boolean isLoadingLatest, final boolean refreshing) {
        if(isLoadingLatest) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(refreshing);
                }
            });
        }
    }

    public static void start(Context context, User user) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(ARGS_KEY_USER, user);
        context.startActivity(intent);
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
                    view = statusTimelineView;
                    break;
                case 1:
                    view = imageTimelineView;
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
                case 0: return "微博";
                case 1: return "相册";
                default: return null;
            }
        }
    }
}
