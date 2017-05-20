package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gnayils.obiew.R;
import com.gnayils.obiew.fragment.PictureFragment;
import com.gnayils.obiew.weibo.bean.PicUrls;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gnayils on 04/03/2017.
 */

public class PicturePagerActivity extends AppCompatActivity {

    public static final String ARGS_KEY_CURRENT_PICTURE_POSITION = "ARGS_KEY_CURRENT_PICTURE_POSITION";
    public static final String ARGS_KEY_PICTURE_URLS = "ARGS_KEY_PICTURE_URLS";

    @Bind(R.id.view_pager)
    protected ViewPager viewPager;

    private List<PicUrls> pictureUrls;
    private int currentPicturePosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pictureUrls = (List<PicUrls>) getIntent().getSerializableExtra(ARGS_KEY_PICTURE_URLS);
        currentPicturePosition = getIntent().getIntExtra(ARGS_KEY_CURRENT_PICTURE_POSITION, 0);
        setContentView(R.layout.activity_picture_pager);
        ButterKnife.bind(this);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(currentPicturePosition);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public static void start(Context context, int currentPicturePosition, List<PicUrls> picUrlsList) {
        Intent intent = new Intent(context, PicturePagerActivity.class);
        intent.putExtra(ARGS_KEY_CURRENT_PICTURE_POSITION, currentPicturePosition);
        intent.putExtra(ARGS_KEY_PICTURE_URLS, (Serializable) picUrlsList);
        context.startActivity(intent);
    }

    class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PicturePagerActivity.this.pictureUrls.size();
        }

        @Override
        public Fragment getItem(int position) {
            return PictureFragment.newInstance(pictureUrls.get(position));
        }
    }
}
