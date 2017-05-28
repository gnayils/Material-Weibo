package com.gnayils.obiew.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.weibo.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gnayils on 08/04/2017.
 */

public class UserRecyclerView extends RecyclerView {

    private RecyclerViewAdapter recyclerViewAdapter;
    private OnItemClickListener onItemClickListener;

    public UserRecyclerView(Context context) {
        this(context, null);
    }

    public UserRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ViewUtils.createDividerDrawable(getContext(), ViewUtils.dp2px(getContext(), 1),
                getResources().getColor(android.R.color.white), getResources().getColor(R.color.colorDivider), ViewUtils.dp2px(getContext(), 64)));
        addItemDecoration(dividerItemDecoration);
        setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter();
        setAdapter(recyclerViewAdapter);
    }

    public void show(List<User> userList) {
        recyclerViewAdapter.add(userList);
    }


    class RecyclerViewAdapter extends Adapter<ViewHolder> {

        private List<User> userList = new ArrayList<>();

        public RecyclerViewAdapter() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserItemViewHolder(new UserItemView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            User user = userList.get(position);
            ((UserItemViewHolder)holder).userItemView.show(user);
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        private void add(List<User> userList) {
            this.userList.addAll(userList);
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class UserItemViewHolder extends ViewHolder {

        UserItemView userItemView;

        UserItemViewHolder(final UserItemView userItemView) {
            super(userItemView);
            this.userItemView = userItemView;
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            this.userItemView.setLayoutParams(layoutParams);
            this.userItemView.setRadius(0);
            this.userItemView.setElevation(0);
            this.userItemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null) {
                        onItemClickListener.onItemClick(userItemView.user);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {

        void onItemClick(User user);
    }
}
