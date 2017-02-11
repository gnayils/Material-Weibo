package com.gnayils.obiew.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.bean.User;
import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.view.AvatarView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gnayils on 12/11/2016.
 */

public class UserProfileFragment extends Fragment implements UserInterface.ProfileView {

    @Bind(R.id.button_login)
    protected Button loginButton;
    @Bind(R.id.button_signup)
    protected Button signupButton;
    @Bind(R.id.text_view_weibo_number)
    protected TextView weiboNumberTextView;
    @Bind(R.id.text_view_follow_number)
    protected TextView followNumberTextView;
    @Bind(R.id.text_view_follower_number)
    protected TextView followerNumberTextView;
    @Bind(R.id.text_view_username)
    protected TextView usernameTextView;
    @Bind(R.id.text_view_about_me)
    protected TextView aboutMeTextView;
    @Bind(R.id.avatar_view_user)
    protected AvatarView userAvatarView;
    @Bind(R.id.linear_layout_login)
    protected ViewGroup linearLayoutLogin;
    @Bind(R.id.linear_layout_user_profile)
    protected ViewGroup linearLayoutUserCenter;

    private UserInterface.Presenter userPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View userProfileView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, userProfileView);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPresenter.requestLogin(getActivity());
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                userPresenter.requestSignUp(getActivity());
            }
        });
        return userProfileView;
    }

    @Override
    public void updateUser(User user) {
        weiboNumberTextView.setText(String.valueOf(user.statuses_count) + "\n微博");
        followNumberTextView.setText(String.valueOf(user.friends_count) + "\n关注");
        followerNumberTextView.setText(String.valueOf(user.followers_count) + "\n粉丝");
        usernameTextView.setText(user.screen_name);
        aboutMeTextView.setText(user.description == null || user.description.isEmpty() ? "暂无介绍" : user.description);
        BitmapLoader.getInstance().loadBitmap(user.avatar_large, userAvatarView.avatarCircleImageView);

        linearLayoutLogin.setVisibility(View.INVISIBLE);
        linearLayoutUserCenter.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPresenter(UserInterface.Presenter presenter) {
        userPresenter = presenter;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
