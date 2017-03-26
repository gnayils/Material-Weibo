package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.fragment.StatusFragment;
import com.gnayils.obiew.interfaces.StatusInterface;
import com.gnayils.obiew.presenter.StatusPresenter;
import com.gnayils.obiew.view.AvatarView;
import com.gnayils.obiew.weibo.bean.User;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gnayils on 26/03/2017.
 */

public class UserProfileActivity extends AppCompatActivity {

    public static final String ARGS_KEY_USER = "ARGS_KEY_USER";

    @Bind(R.id.image_view_cover)
    protected ImageView coverImageView;
    @Bind(R.id.avatar_view)
    protected AvatarView avatarView;
    @Bind(R.id.text_view_screen_name)
    protected TextView screenNameTextView;
    @Bind(R.id.text_view_description)
    protected TextView descriptionTextView;

    private StatusInterface.Presenter statusPresenter;

    private User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra(ARGS_KEY_USER);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
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
}
