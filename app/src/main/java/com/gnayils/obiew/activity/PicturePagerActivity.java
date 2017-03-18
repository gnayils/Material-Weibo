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

import com.gnayils.obiew.R;
import com.gnayils.obiew.fragment.PictureFragment;
import com.gnayils.obiew.weibo.bean.Status;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gnayils on 04/03/2017.
 */

public class PicturePagerActivity extends FragmentActivity {

    public static final String ARGS_KEY_CURRENT_PICTURE_POSITION = "ARGS_KEY_CURRENT_PICTURE_POSITION";
    public static final String ARGS_KEY_PICTURE_URLS = "ARGS_KEY_PICTURE_URLS";

    @Bind(R.id.view_pager)
    protected ViewPager viewPager;

    private List<Status.PicUrls> pictureUrls;
    private int currentPicturePosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pictureUrls = (List<Status.PicUrls>) getIntent().getSerializableExtra(ARGS_KEY_PICTURE_URLS);
        currentPicturePosition = getIntent().getIntExtra(ARGS_KEY_CURRENT_PICTURE_POSITION, 0);
        setContentView(R.layout.activity_picture_pager);
        ButterKnife.bind(this);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(currentPicturePosition);
    }

    public static void start(Context context, int currentPicturePosition, List<Status.PicUrls> picUrlsList) {
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
            return PictureFragment.newInstance(PicturePagerActivity.this.pictureUrls.get(position).thumbnail_pic);
        }
    }
}
