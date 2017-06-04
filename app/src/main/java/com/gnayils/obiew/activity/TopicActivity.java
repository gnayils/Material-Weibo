package com.gnayils.obiew.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.Popup;
import com.gnayils.obiew.view.LoadMoreRecyclerView;
import com.gnayils.obiew.view.StatusTimelineView;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.StatusTimeline;
import com.gnayils.obiew.weibo.service.SearchService;
import com.gnayils.obiew.weibo.service.SubscriberAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TopicActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.status_timeline_view)
    StatusTimelineView statusTimelineView;

    private String topic;

    private SearchService searchService = new SearchService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getIntent().getData() != null) {
            String uri = getIntent().getData().toString();
            Matcher matcher  = Pattern.compile(getString(R.string.topic_regex)).matcher(uri);
            if(matcher.find()) {
                String group = matcher.group();
                topic = group.substring(1, group.length() - 1);
            }
        }
        if(topic != null && !topic.trim().isEmpty()) {
            getSupportActionBar().setTitle(topic);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showTopicTimeline(true);
            }
        });
        statusTimelineView.setOnLoadMoreListener(new LoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                showTopicTimeline(false);
            }
        });
        showTopicTimeline(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        searchService.unsubscribe();
    }

    private void showTopicTimeline(final boolean loadLatest) {
        if (topic != null && !topic.trim().isEmpty()) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        searchService.showTopicTimeline(loadLatest, topic,
                new SubscriberAdapter<StatusTimeline>() {

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
                    public void onError(Throwable e) {
                        Popup.toast("获取话题微博失败: " + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusTimeline statusTimeline) {
                        statusTimelineView.show(statusTimeline);
                    }
                });
    }
}
