package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gnayils.obiew.R;
import com.gnayils.obiew.util.Popup;
import com.gnayils.obiew.view.AvatarView;
import com.gnayils.obiew.view.StatusTimelineView;
import com.gnayils.obiew.weibo.Account;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.StatusTimeline;
import com.gnayils.obiew.weibo.service.StatusService;
import com.gnayils.obiew.weibo.service.SubscriberAdapter;

import junit.framework.Test;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.floating_action_button) FloatingActionButton floatingActionButton;
    @Bind(R.id.navigation_view) NavigationView navigationView;
    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.status_timeline_view) StatusTimelineView statusTimelineView;
    @Bind(R.id.bottom_navigation_view) BottomNavigationView bottomNavigationView;

    private ImageView coverImageView;
    private AvatarView avatarView;
    private TextView screenNameTextView;
    private TextView descriptionTextView;
    private Button statusCountButton;
    private Button followCountButton;
    private Button followerCountButton;

    private StatusService statusService = new StatusService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        coverImageView = (ImageView) headerView.findViewById(R.id.image_view_cover);
        avatarView = (AvatarView) headerView.findViewById(R.id.avatar_view);
        screenNameTextView = (TextView) headerView.findViewById(R.id.text_view_screen_name);
        descriptionTextView = (TextView) headerView.findViewById(R.id.text_view_description);
        statusCountButton = (Button) headerView.findViewById(R.id.button_status_count);
        followCountButton = (Button) headerView.findViewById(R.id.button_follow_count);
        followerCountButton = (Button) headerView.findViewById(R.id.button_follower_count);
        avatarView.avatarCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.start(MainActivity.this, Account.user);
            }
        });
        if (Account.user.cover_image_phone != null && !Account.user.cover_image_phone.isEmpty()) {
            String coverImageUrl = Account.user.cover_image_phone;
            if (coverImageUrl.indexOf(";") != 0) {
                String[] coverImageUrls = coverImageUrl.split(";");
                coverImageUrl = coverImageUrls[(int) (Math.random() * coverImageUrls.length)];
            }
            Glide.with(this).load(coverImageUrl).into(coverImageView);
        }
        Glide.with(this).load(Account.user.avatar_large).into(avatarView.avatarCircleImageView);
        screenNameTextView.setText(Account.user.screen_name);
        descriptionTextView.setText(Account.user.description == null || Account.user.description.isEmpty() ? "暂无介绍" : Account.user.description);
        statusCountButton.setText(Account.user.statuses_count + "\n微博");
        followCountButton.setText(Account.user.friends_count + "\n关注");
        followerCountButton.setText(Account.user.followers_count + "\n粉丝");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showHomeTimeline(true);
            }
        });
        statusTimelineView.setOnLoadMoreListener(new StatusTimelineView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
               showHomeTimeline(false);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublishActivity.start(MainActivity.this);
            }
        });
        showHomeTimeline(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        statusService.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showHomeTimeline(final boolean loadLatest) {
        statusService.showHomeTimeline(loadLatest, Status.FEATURE_ALL,
                new SubscriberAdapter<StatusTimeline>() {

                    @Override
                    public void onSubscribe() {
                        if(loadLatest) {
                            swipeRefreshLayout.setRefreshing(true);
                        }
                    }

                    @Override
                    public void onUnsubscribe() {
                        if(loadLatest) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Popup.toast("获取微博失败: " + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusTimeline statusTimeline) {
                        statusTimelineView.show(statusTimeline);
                    }
                });
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
