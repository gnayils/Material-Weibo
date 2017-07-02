package com.gnayils.obiew.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.gnayils.obiew.R;
import com.gnayils.obiew.view.UserRecyclerView;
import com.gnayils.obiew.weibo.Account;
import com.gnayils.obiew.weibo.bean.User;
import com.gnayils.obiew.weibo.bean.Users;
import com.gnayils.obiew.weibo.service.FriendshipService;
import com.gnayils.obiew.weibo.service.SubscriberAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FriendFragment extends Fragment {

    @Bind(R.id.recycler_view)
    protected UserRecyclerView recyclerView;
    @Bind(R.id.progress_bar)
    protected ProgressBar progressBar;

    private OnFriendClickListener listener;

    private FriendshipService friendshipService = new FriendshipService();

    public FriendFragment() {
    }

    public static FriendFragment newInstance() {
        FriendFragment fragment = new FriendFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_friend, container, false);
        ButterKnife.bind(this, rootView);
        recyclerView.setOnItemClickListener(new UserRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                if(listener != null) {
                    listener.onFriendClick(user);
                }
            }
        });
        friendshipService.friends(Account.accessToken.uid, new SubscriberAdapter<Users>(){
            @Override
            public void onSubscribe() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNext(Users users) {
                recyclerView.show(users.users);
            }

            @Override
            public void onUnsubscribe() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFriendClickListener) {
            listener = (OnFriendClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFriendClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        friendshipService.unsubscribe();
    }

    public interface OnFriendClickListener {

        void onFriendClick(User user);

    }
}
