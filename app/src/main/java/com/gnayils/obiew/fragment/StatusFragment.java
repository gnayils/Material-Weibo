package com.gnayils.obiew.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gnayils.obiew.R;
import com.gnayils.obiew.interfaces.StatusInterface;
import com.gnayils.obiew.interfaces.UserInterface;
import com.gnayils.obiew.view.StatusCardView;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.StatusTimeline;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.gnayils.obiew.util.ViewUtils.dp2px;

/**
 * Created by Gnayils on 31/01/2017.
 */

public class StatusFragment extends Fragment implements StatusInterface.View {

    public static final String TAG = StatusFragment.class.getSimpleName();

    @Bind(R.id.recycler_view)
    protected RecyclerView recyclerView;
    @Bind(R.id.swipy_refresh_layout)
    protected SwipyRefreshLayout swipyRefreshLayout;

    private RecycleViewAdapter recycleViewAdapter;

    private StatusInterface.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View statusView = inflater.inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.bind(this, statusView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleViewAdapter = new RecycleViewAdapter();
        recyclerView.setAdapter(recycleViewAdapter);
        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(direction == SwipyRefreshLayoutDirection.TOP) {
                    presenter.loadStatusTimeline(true);
                } else if(direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    presenter.loadStatusTimeline(false);
                }

            }
        });
        return statusView;
    }

    public void show(StatusTimeline statusTimeline) {
        recycleViewAdapter.addTimeline(statusTimeline);
    }

    @Override
    public void showLoadingIndicator(final boolean active) {
        swipyRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipyRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void setPresenter(StatusInterface.Presenter presenter) {
        this.presenter = presenter;
    }


    class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewHolder> {

        private List<Status> statusList = new ArrayList<Status>();

        public RecycleViewAdapter() {

        }

        @Override
        public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecycleViewHolder holder = new RecycleViewHolder(new StatusCardView(parent.getContext()));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecycleViewHolder holder, int position) {
            Status status = statusList.get(position);
            holder.statusCardView.show(status);
        }

        @Override
        public int getItemCount() {
            return statusList.size();
        }

        public void addTimeline(StatusTimeline statusTimeline) {
            Set<Status> statusSet = new TreeSet<Status>(statusList);
            statusSet.addAll(statusTimeline.statuses);
            statusList.clear();
            statusList.addAll(statusSet);
            notifyDataSetChanged();
        }

    }

    class RecycleViewHolder extends RecyclerView.ViewHolder {

        StatusCardView statusCardView;

        RecycleViewHolder(StatusCardView statusCardView) {
            super(statusCardView);
            this.statusCardView = statusCardView;
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(dp2px(statusCardView.getContext(), 8), dp2px(statusCardView.getContext(), 4), dp2px(statusCardView.getContext(), 8), dp2px(statusCardView.getContext(), 4));
            this.statusCardView.setLayoutParams(layoutParams);
            this.statusCardView.setRadius(dp2px(statusCardView.getContext(), 4));
        }
    }
}
