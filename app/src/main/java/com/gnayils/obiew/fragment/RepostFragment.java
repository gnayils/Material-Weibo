package com.gnayils.obiew.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gnayils.obiew.R;
import com.gnayils.obiew.interfaces.RepostInterface;
import com.gnayils.obiew.view.CommentView;
import com.gnayils.obiew.view.RepostView;
import com.gnayils.obiew.weibo.bean.Comment;
import com.gnayils.obiew.weibo.bean.Repost;
import com.gnayils.obiew.weibo.bean.RepostTimeline;
import com.gnayils.obiew.weibo.bean.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.gnayils.obiew.util.ViewUtils.dp2px;

/**
 * Created by Gnayils on 11/03/2017.
 */

public class RepostFragment extends Fragment implements RepostInterface.View{

    private Status status;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RepostInterface.Presenter presenter;

    public static final String ARGS_KEY_STATUS = "ARGS_KEY_STATUS";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = (Status) getArguments().get(ARGS_KEY_STATUS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_repost, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
        return recyclerView;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.loadRepost(status.id, true);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.unsubscribe();
    }

    @Override
    public void show(RepostTimeline repostTimeline) {
        recyclerViewAdapter.add(repostTimeline);
    }

    @Override
    public void showLoadingIndicator(boolean refreshing) {

    }

    @Override
    public void setPresenter(RepostInterface.Presenter presenter) {
        this.presenter = presenter;
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        private List<Repost> repostList = new ArrayList<>();

        public RecyclerViewAdapter() {
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerViewHolder holder = new RecyclerViewHolder(new RepostView(parent.getContext()));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            Repost repost = repostList.get(position);
            holder.repostView.show(repost);
        }

        public void add(RepostTimeline repostTimeline) {
            Set<Repost> commentSet = new TreeSet<>(repostList);
            commentSet.addAll(repostTimeline.reposts);
            repostList.clear();
            repostList.addAll(commentSet);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return repostList.size();
        }
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        RepostView repostView;

        RecyclerViewHolder(RepostView repostView) {
            super(repostView);
            this.repostView = repostView;
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(dp2px(repostView.getContext(), 8), dp2px(repostView.getContext(), 4), dp2px(repostView.getContext(), 8), dp2px(repostView.getContext(), 4));
            this.repostView.setLayoutParams(layoutParams);
            this.repostView.setRadius(dp2px(repostView.getContext(), 4));
        }
    }

    public static RepostFragment newInstance(Status status) {
        RepostFragment fragment = new RepostFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_KEY_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }
}
