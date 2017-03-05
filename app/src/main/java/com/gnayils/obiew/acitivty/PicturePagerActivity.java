package com.gnayils.obiew.acitivty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.gnayils.obiew.R;
import com.gnayils.obiew.fragment.PictureFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gnayils on 04/03/2017.
 */

public class PicturePagerActivity extends FragmentActivity {

    public static final String EXTRA_CURRENT_PICTURE_POSITION = "EXTRA_CURRENT_PICTURE_POSITION";
    public static final String EXTRA_PICTURE_URLS = "EXTRA_PICTURE_URLS";

    @Bind(R.id.view_pager)
    protected ViewPager viewPager;

    private String[] pictureUrls;
    private int currentPicturePosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pictureUrls = getIntent().getStringArrayExtra(EXTRA_PICTURE_URLS);
        currentPicturePosition = getIntent().getIntExtra(EXTRA_CURRENT_PICTURE_POSITION, 0);
        setContentView(R.layout.activity_picture_pager);
        ButterKnife.bind(this);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(currentPicturePosition);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PicturePagerActivity.this.pictureUrls.length;
        }

        @Override
        public Fragment getItem(int position) {
            return PictureFragment.newInstance(PicturePagerActivity.this.pictureUrls[position]);
        }
    }
}
