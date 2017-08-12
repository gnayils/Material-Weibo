package com.gnayils.obiew.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.view.AvatarView;
import com.gnayils.obiew.weibo.Account;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {

    @Bind(R.id.avatar_view)
    AvatarView avatarView;
    @Bind(R.id.button_login)
    Button loginButton;

    @Bind(R.id.content_view)
    LinearLayout contentView;
    @Bind(R.id.frame_layout_bottom_part)
    FrameLayout bottomPartFrameLayout;
    @Bind(R.id.frame_layout_top_part)
    FrameLayout topPartFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        inflateBackground();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.start(SplashActivity.this);
            }
        });

        /*
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(contentView, "backgroundColor",
                new ArgbEvaluator(),
                getResources().getColor(R.color.colorPrimaryDark),
                Color.parseColor("#4A148C"));
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setDuration(800);
        objectAnimator.start();
        */
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
                ViewUtils.getColorByAttrId(this, R.attr.themeColorPrimaryDark),
                ViewUtils.getColorByAttrId(this, R.attr.themeColorPrimary)
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
                ViewUtils.getColorByAttrId(this, R.attr.themeColorPrimary),
                ViewUtils.getColorByAttrId(this, R.attr.themeColorPrimaryDark)
        });
        gradientDrawable.setCornerRadii(new float[]{
                size.getWidth() / 2, size.getWidth() / 4, size.getWidth() / 2, size.getWidth() / 4, 0, 0, 0, 0
        });
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        bottomPartFrameLayout.setBackground(gradientDrawable);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        context.startActivity(intent);
    }
}
