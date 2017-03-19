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
import com.gnayils.obiew.interfaces.CommentInterface;
import com.gnayils.obiew.view.CommentView;
import com.gnayils.obiew.weibo.bean.Comment;
import com.gnayils.obiew.weibo.bean.CommentTimeline;
import com.gnayils.obiew.weibo.bean.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.gnayils.obiew.util.ViewUtils.dp2px;

/**
 * Created by Gnayils on 11/03/2017.
 */

public class CommentFragment extends Fragment implements CommentInterface.View{

    private Status status;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private CommentInterface.Presenter presenter;

    public static final String ARGS_KEY_STATUS = "ARGS_KEY_STATUS";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = (Status) getArguments().get(ARGS_KEY_STATUS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_comment, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
        return recyclerView;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.loadComment(status.id, true);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.unsubscribe();
    }

    public void show(CommentTimeline commentTimeline) {
        recyclerViewAdapter.add(commentTimeline);
    }

    @Override
    public void showLoadingIndicator(boolean refreshing) {

    }

    @Override
    public void setPresenter(CommentInterface.Presenter presenter) {
        this.presenter = presenter;
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        private List<Comment> commentList = new ArrayList<>();

        public RecyclerViewAdapter() {

        }

        @Override public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerViewHolder holder = new RecyclerViewHolder(new CommentView(parent.getContext()));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            Comment comment = commentList.get(position);
            holder.commentView.show(comment);
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        public void add(CommentTimeline commentTimeline) {
            Set<Comment> commentSet = new TreeSet<>(commentList);
            commentSet.addAll(commentTimeline.comments);
            commentList.clear();
            commentList.addAll(commentSet);
            notifyDataSetChanged();
        }
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        CommentView commentView;

        RecyclerViewHolder(CommentView commentView) {
            super(commentView);
            this.commentView = commentView;
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(dp2px(commentView.getContext(), 8), dp2px(commentView.getContext(), 4), dp2px(commentView.getContext(), 8), dp2px(commentView.getContext(), 4));
            this.commentView.setLayoutParams(layoutParams);
            this.commentView.setRadius(dp2px(commentView.getContext(), 4));
        }
    }

    public static CommentFragment newInstance(Status status) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_KEY_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }
}