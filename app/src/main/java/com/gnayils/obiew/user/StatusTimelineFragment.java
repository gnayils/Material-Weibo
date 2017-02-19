package com.gnayils.obiew.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gnayils.obiew.R;
import com.gnayils.obiew.bean.Timeline;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import android.support.v7.widget.RecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gnayils on 31/01/2017.
 */

public class StatusTimelineFragment extends Fragment implements UserInterface.StatusTimelineView {

    public static final String TAG = StatusTimelineFragment.class.getSimpleName();

    @Bind(R.id.recycler_view_status_timeline)
    protected RecyclerView statusTimelineRecyclerView;
    @Bind(R.id.swipy_refresh_layout_timeline)
    protected SwipyRefreshLayout statusTimelineSwipyRefreshLayout;

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
        statusTimelineSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(direction == SwipyRefreshLayoutDirection.TOP) {
                    userPresenter.loadStatusTimeline(true);
                } else if(direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    userPresenter.loadStatusTimeline(false);
                }

            }
        });
        return statusView;
    }

    public void showStatusTimeline(Timeline timeline) {
        statusTimelineAdapter.addTimeline(timeline);
    }

    @Override
    public void showLoadingIndicator(final boolean active) {
        statusTimelineSwipyRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                statusTimelineSwipyRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void setPresenter(UserInterface.Presenter presenter) {
        userPresenter = presenter;
    }
}
