package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gnayils.obiew.R;
import com.gnayils.obiew.fragment.CommentFragment;
import com.gnayils.obiew.fragment.RepostFragment;
import com.gnayils.obiew.interfaces.CommentInterface;
import com.gnayils.obiew.interfaces.RepostInterface;
import com.gnayils.obiew.presenter.CommentPresenter;
import com.gnayils.obiew.presenter.RepostPresenter;
import com.gnayils.obiew.view.StatusCardView;
import com.gnayils.obiew.weibo.bean.Status;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StatusDetailActivity extends AppCompatActivity {

    public static final String ARGS_KEY_STATUS = "ARGS_KEY_STATUS";

    private Status status;

    @Bind(R.id.status_card_view)
    protected StatusCardView statusCardView;
    @Bind(R.id.tab_layout)
    protected TabLayout tabLayout;
    @Bind(R.id.view_pager)
    protected ViewPager viewPager;
    @Bind(R.id.tool_bar)
    protected Toolbar toolbar;

    private CommentInterface.Presenter commentPresenter;
    private CommentFragment commentFragment;
    private RepostInterface.Presenter repostPresenter;
    private RepostFragment repostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = (Status) getIntent().getSerializableExtra(ARGS_KEY_STATUS);
        setContentView(R.layout.activity_status_detail);
        ButterKnife.bind(this);
        statusCardView.setBackgroundColor(Color.TRANSPARENT);
        statusCardView.commentLayout.setVisibility(View.GONE);
        statusCardView.show(status);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });
        commentFragment = CommentFragment.newInstance(status);
        commentPresenter = new CommentPresenter(commentFragment);
        repostFragment = RepostFragment.newInstance(status);
        repostPresenter = new RepostPresenter(repostFragment);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    public static void start(Context context, Status status) {
        Intent intent = new Intent(context, StatusDetailActivity.class);
        intent.putExtra(ARGS_KEY_STATUS, status);
        context.startActivity(intent);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int i) {
            switch(i) {
                case 0:
                    return repostFragment;
                case 1:
                    return commentFragment;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return status.reposts_count + "转发";
                case 1: return status.comments_count + "评论";
            }
            return "";
        }
    }
}
