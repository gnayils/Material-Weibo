package com.gnayils.obiew.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gnayils.obiew.R;
import com.gnayils.obiew.view.AvatarView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @Bind(R.id.avatar_view)
    protected AvatarView avatarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

    }
}
