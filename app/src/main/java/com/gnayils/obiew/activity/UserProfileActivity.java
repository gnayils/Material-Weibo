package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.fragment.StatusFragment;
import com.gnayils.obiew.interfaces.StatusInterface;
import com.gnayils.obiew.presenter.StatusPresenter;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.view.AvatarView;
import com.gnayils.obiew.weibo.bean.User;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gnayils on 26/03/2017.
 */

public class UserProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

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
                return appBarLayout.getTotalScrollRange() == Math.abs(appBarCurrentVerticalOffset);
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
        BitmapLoader.getInstance().loadBitmap(user.cover_image_phone, coverImageView);
        BitmapLoader.getInstance().loadBitmap(user.avatar_large, avatarView.avatarCircleImageView);
        screenNameTextView.setText(user.screen_name);
        descriptionTextView.setText(user.description);

        StatusFragment statusFragment = StatusFragment.newInstance(StatusFragment.TIMELINE_TYPE_USER, user);
        statusPresenter = new StatusPresenter(statusFragment);
        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout_fragment_container, statusFragment).commit();
    }

    public static void start(Context context, User user) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(ARGS_KEY_USER, user);
        context.startActivity(intent);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        appBarCurrentVerticalOffset = verticalOffset;
    }
}
