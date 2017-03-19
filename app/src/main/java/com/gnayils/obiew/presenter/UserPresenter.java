package com.gnayils.obiew.presenter;

import com.gnayils.obiew.interfaces.UserInterface;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gnayils on 12/11/2016.
 */

public class UserPresenter implements UserInterface.Presenter {

    public static final String TAG = UserPresenter.class.getSimpleName();

    private UserInterface.View userProfileView;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();


    public UserPresenter(UserInterface.View userProfileView) {
        this.userProfileView = userProfileView;
        this.userProfileView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        compositeSubscription.clear();
    }

    @Override
    public void loadUser() {

    }
}
