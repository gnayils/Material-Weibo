package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gnayils.obiew.R;
import com.gnayils.obiew.view.LoadMoreRecyclerView;
import com.gnayils.obiew.view.StatusTimelineView;
import com.gnayils.obiew.weibo.bean.Statuses;
import com.gnayils.obiew.weibo.service.StatusService;
import com.gnayils.obiew.weibo.service.SubscriberAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MentionActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.status_timeline_view)
    StatusTimelineView statusTimelineView;

    private StatusService statusService = new StatusService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mention);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showMentionTimeline(true);
            }
        });
        statusTimelineView.setOnLoadMoreListener(new LoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                showMentionTimeline(false);
            }
        });
        showMentionTimeline(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        statusService.unsubscribe();
    }

    private void showMentionTimeline(final boolean loadLatest) {
        statusService.showMentionTimeline(loadLatest,
                new SubscriberAdapter<Statuses>() {

                    @Override
                    public void onSubscribe() {
                        if (loadLatest) {
                            swipeRefreshLayout.setRefreshing(true);
                        }
                    }

                    @Override
                    public void onUnsubscribe() {
                        if (loadLatest) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onNext(Statuses statuses) {
                        statusTimelineView.show(loadLatest, statuses);
                    }
                });
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MentionActivity.class);
        context.startActivity(intent);
    }
}
