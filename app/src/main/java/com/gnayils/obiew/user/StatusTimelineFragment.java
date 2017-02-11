package com.gnayils.obiew.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gnayils.obiew.R;
import com.gnayils.obiew.bean.Timeline;
import com.gnayils.obiew.bean.User;
import com.gnayils.obiew.util.TokenKeeper;
import com.gnayils.obiew.weibo.StatusAPI;
import com.gnayils.obiew.weibo.WeiboAPI;

import android.support.v7.widget.RecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by Gnayils on 31/01/2017.
 */

public class StatusTimelineFragment extends Fragment implements UserInterface.StatusTimelineView {

    public static final String TAG = StatusTimelineFragment.class.getSimpleName();

    @Bind(R.id.recycler_view_status_timeline)
    protected RecyclerView statusTimelineRecyclerView;
    @Bind(R.id.swipe_refresh_layout_timeline)
    protected SwipeRefreshLayout statusTimelineSwipeRefreshLayout;

    private StatusTimelineAdapter statusTimelineAdapter;

    private UserInterface.Presenter userPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View statusView = inflater.inflate(R.layout.fragment_status_timeline, container, false);
        ButterKnife.bind(this, statusView);
        statusTimelineRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        statusTimelineAdapter = new StatusTimelineAdapter();
        statusTimelineRecyclerView.setAdapter(statusTimelineAdapter);
        statusTimelineSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userPresenter.loadStatusTimeline();
            }
        });
        return statusView;
    }

    public void showStatusTimeline(Timeline timeline) {
        statusTimelineAdapter.addTimeline(timeline);
    }

    @Override
    public void showLoadingIndicator(final boolean active) {
        statusTimelineSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                statusTimelineSwipeRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void setPresenter(UserInterface.Presenter presenter) {
        userPresenter = presenter;
    }
}
