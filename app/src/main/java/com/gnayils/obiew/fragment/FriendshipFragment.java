package com.gnayils.obiew.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.gnayils.obiew.R;
import com.gnayils.obiew.interfaces.BasePresenter;
import com.gnayils.obiew.interfaces.FriendshipInterface;
import com.gnayils.obiew.presenter.FriendshipPresenter;
import com.gnayils.obiew.view.UserRecyclerView;
import com.gnayils.obiew.weibo.Account;
import com.gnayils.obiew.weibo.bean.User;
import com.gnayils.obiew.weibo.bean.Users;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FriendshipFragment extends Fragment implements FriendshipInterface.View {

    @Bind(R.id.user_recycler_view)
    protected UserRecyclerView userRecyclerView;
    @Bind(R.id.progress_bar)
    protected ProgressBar progressBar;

    private OnFriendClickListener listener;

    private FriendshipInterface.Presenter friendshipPresenter;

    public FriendshipFragment() {
    }

    public static FriendshipFragment newInstance() {
        FriendshipFragment fragment = new FriendshipFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendshipPresenter = new FriendshipPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_users, container, false);
        ButterKnife.bind(this, rootView);
        userRecyclerView.setOnItemClickListener(new UserRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                if(listener != null) {
                    listener.onFriendClick(user);
                }
            }
        });
        friendshipPresenter.friends(Account.accessToken.uid);
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
        friendshipPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(BasePresenter presenter) {
        friendshipPresenter = (FriendshipInterface.Presenter) presenter;
    }

    @Override
    public void setLoadingIndicatorVisible(boolean visible) {
        if(visible) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void show(Users users) {
        userRecyclerView.show(users.users);
    }

    public interface OnFriendClickListener {

        void onFriendClick(User user);

    }
}
