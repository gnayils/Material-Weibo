package com.gnayils.obiew.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.view.AvatarView;
import com.gnayils.obiew.weibo.Account;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @Bind(R.id.avatar_view)
    AvatarView avatarView;
    @Bind(R.id.button_login)
    Button loginButton;

    @Bind(R.id.frame_layout_bottom_part)
    FrameLayout bottomPartFrameLayout;
    @Bind(R.id.frame_layout_top_part)
    FrameLayout topPartFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        inflateBackground();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.start(SplashActivity.this);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Account.loadCache(this)) {
            loginButton.setVisibility(View.INVISIBLE);
            Bitmap avatar = BitmapFactory.decodeByteArray(Account.user.avatarBytes, 0, Account.user.avatarBytes.length);
            avatarView.avatarCircleImageView.setImageBitmap(avatar);
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainActivity.start(SplashActivity.this);
                    SplashActivity.this.finish();
                }
            }, 500);
        } else {
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    private void inflateBackground() {
        Size size = ViewUtils.getScreenSize(this);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(new int[]{
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorPrimary)
        });
        gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        gradientDrawable.setCornerRadii(new float[]{
                0, 0, 0, 0, size.getWidth() / 2, size.getWidth() / 3, size.getWidth() / 2, size.getWidth() / 3
        });
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        topPartFrameLayout.setBackground(gradientDrawable);

        gradientDrawable = new GradientDrawable();
        gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        gradientDrawable.setColors(new int[]{
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark)
        });
        gradientDrawable.setCornerRadii(new float[]{
                size.getWidth() / 2, size.getWidth() / 4, size.getWidth() / 2, size.getWidth() / 4, 0, 0, 0, 0
        });
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        bottomPartFrameLayout.setBackground(gradientDrawable);
    }
}
