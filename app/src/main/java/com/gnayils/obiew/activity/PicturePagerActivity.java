package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.gnayils.obiew.R;
import com.gnayils.obiew.fragment.PictureFragment;
import com.gnayils.obiew.util.ViewUtils;
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

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.view_pager)
    ViewPager viewPager;

    private List<PicUrls> pictureUrls;
    private int currentPicturePosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black_alpha_80));
        setContentView(R.layout.activity_picture_pager);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        pictureUrls = (List<PicUrls>) getIntent().getSerializableExtra(ARGS_KEY_PICTURE_URLS);
        currentPicturePosition = getIntent().getIntExtra(ARGS_KEY_CURRENT_PICTURE_POSITION, 0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ViewGroup.MarginLayoutParams toolbarLayoutLayoutParam = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
        toolbarLayoutLayoutParam.setMargins(0, ViewUtils.getStatusBarHeight(this), 0, 0);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(currentPicturePosition);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_picture_pager_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_save);
        menuItem.setIcon(ViewUtils.tintDrawable(menuItem.getIcon(), Color.WHITE));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
            Fragment fragment = PictureFragment.newInstance(pictureUrls.get(position));
            return fragment;
        }
    }


    public static void start(Context context, int currentPicturePosition, List<PicUrls> picUrlsList) {
        Intent intent = new Intent(context, PicturePagerActivity.class);
        intent.putExtra(ARGS_KEY_CURRENT_PICTURE_POSITION, currentPicturePosition);
        intent.putExtra(ARGS_KEY_PICTURE_URLS, (Serializable) picUrlsList);
        context.startActivity(intent);
    }
}
