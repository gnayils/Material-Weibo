package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.gnayils.obiew.Obiew;
import com.gnayils.obiew.R;
import com.gnayils.obiew.Preferences;
import com.gnayils.obiew.util.Popup;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.view.AvatarView;
import com.gnayils.obiew.view.ItemView;
import com.gnayils.obiew.view.StatusTimelineView;
import com.gnayils.obiew.weibo.Account;
import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.bean.Group;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.Statuses;
import com.gnayils.obiew.weibo.service.FriendshipService;
import com.gnayils.obiew.weibo.service.StatusService;
import com.gnayils.obiew.weibo.service.SubscriberAdapter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MaterialDialog.ListCallbackSingleChoice {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.status_timeline_view)
    StatusTimelineView statusTimelineView;
    @Bind(R.id.fab_write_status)
    FloatingActionButton floatingActionButton;

    @Bind(R.id.image_view_cover)
    ImageView coverImageView;
    @Bind(R.id.image_view_night_mode)
    ImageView nightModeImageView;
    @Bind(R.id.avatar_view)
    AvatarView avatarView;
    @Bind(R.id.text_view_screen_name)
    TextView screenNameTextView;
    @Bind(R.id.text_view_description)
    TextView descriptionTextView;
    @Bind(R.id.button_status_count)
    Button statusCountButton;
    @Bind(R.id.button_following_count)
    Button followCountButton;
    @Bind(R.id.button_follower_count)
    Button followerCountButton;

    private List<Map.Entry<String, Group>> groupList = new ArrayList<>();
    private int currentSelectedGroupIndex = 0;

    private StatusService statusService = new StatusService();
    private FriendshipService friendshipService = new FriendshipService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeBackLayout.setEnableGesture(false);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("主页");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublishActivity.startForStatusPublishment(MainActivity.this);
            }
        });
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
        nightModeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
                Preferences.toggleTheme();
                getApplication().getTheme().applyStyle(Preferences.getThemeResource(), true);
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Statuses statuses = new Statuses();
                        statuses.statuses = statusTimelineView.getAdapterDataSet();
                        getIntent().putExtra("statues", statuses);
                        getIntent().putExtra("currentSelectedGroupIndex", currentSelectedGroupIndex);
                        recreate();
                    }
                }, 500);

            }
        });
        screenNameTextView.setText(Account.user.screen_name);
        descriptionTextView.setText(Account.user.description == null || Account.user.description.isEmpty() ? "暂无介绍" : Account.user.description);
        statusCountButton.setText(Account.user.statuses_count + "\n微博");
        followCountButton.setText(Weibo.format.followerCount(Account.user.friends_count) + "\n关注");
        followerCountButton.setText(Weibo.format.followerCount(Account.user.followers_count) + "\n粉丝");
        swipeRefreshLayout.setColorSchemeColors(ViewUtils.getColorByAttrId(this, R.attr.themeColorSecondaryText));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ViewUtils.getColorByAttrId(this, R.attr.themeColorViewBackground));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showCurrentTimeline(currentSelectedGroupIndex, true);
            }
        });
        statusTimelineView.setOnLoadMoreListener(new StatusTimelineView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                showCurrentTimeline(currentSelectedGroupIndex, false);
            }
        });

        for(String name : new String[]{"全部", "相互关注"}) {
            groupList.add(new AbstractMap.SimpleEntry<String, Group>(name, null) {@Override public String toString() { return getKey(); } });
        }
        if(Account.groups != null && Account.groups.lists != null) {
            for(Group group : Account.groups.lists) {
                groupList.add(new AbstractMap.SimpleEntry<String, Group>(group.name, group) {@Override public String toString() { return getKey(); } });
            }
        }

        if(isRecreated()) {
            Statuses statuses = (Statuses) getIntent().getSerializableExtra("statues");
            statusTimelineView.appendData(true, statuses.statuses);
            currentSelectedGroupIndex = getIntent().getIntExtra("currentSelectedGroupIndex", 0);
            getSupportActionBar().setTitle(groupList.get(currentSelectedGroupIndex).getKey());
        } else {
            onSelection(null, null, 0, null);
        }
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
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_option_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_change_group);
        menuItem.setIcon(ViewUtils.tintDrawable(menuItem.getIcon(), Color.WHITE));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_change_group:
                Popup.singleChooseDialog("好友分组", currentSelectedGroupIndex, groupList, this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
        currentSelectedGroupIndex = which;
        statusTimelineView.scrollToPosition(0);
        getSupportActionBar().setTitle(groupList.get(which).getKey());
        showCurrentTimeline(which, true);
        return true;
    }

    public void onItemViewClick(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (view.getId()) {
            case R.id.item_view_logout:
                Account.clearCache(this);
                SplashActivity.start(this);
                finish();
                break;
            default:
                break;
        }
    }

    private void showCurrentTimeline(int groupIndex, final boolean loadLatest) {
        SubscriberAdapter subscriberAdapter = new SubscriberAdapter<Statuses>() {

            @Override
            public void onSubscribe() {
                if (loadLatest) {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            public void onUnsubscribe() {
                if (loadLatest) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onNext(Statuses statuses) {
                statusTimelineView.appendData(loadLatest, statuses.statuses);
            }
        };
        if("全部".equals(groupList.get(groupIndex).getKey())) {
            statusService.showHomeTimeline(loadLatest, Status.FEATURE_ALL, subscriberAdapter);
        } else if("相互关注".equals(groupList.get(groupIndex).getKey())) {
            statusService.showBilateralTimeline(loadLatest, Status.FEATURE_ALL, subscriberAdapter);
        } else if(groupList.get(groupIndex).getValue() != null){
            friendshipService.showGroupTimeline(groupList.get(groupIndex).getValue(), loadLatest, Status.FEATURE_ALL, subscriberAdapter);
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
