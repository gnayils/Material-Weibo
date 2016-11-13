package com.gnayils.obiew.user;

import android.app.Activity;

import com.gnayils.obiew.WeiboInterface;
import com.gnayils.obiew.bean.User;
import com.gnayils.obiew.event.AuthorizeResultEvent;
import com.gnayils.obiew.util.TokenKeeper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gnayils on 12/11/2016.
 */

public class UserProfilePresenter implements UserProfileInterface.Presenter {

    private UserProfileInterface.View userProfileView;
    private UserAuthorizeHandler userAuthorizeHandler;

    public UserProfilePresenter(UserProfileInterface.View userProfileView) {
        this.userProfileView = userProfileView;
        this.userProfileView.setPresenter(this);
        this.userAuthorizeHandler = new UserAuthorizeHandler();
        EventBus.getDefault().register(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void OnAuthorizeResult(AuthorizeResultEvent event) {
        if(event.isSuccessful) {
            WeiboInterface.getInstance().getUserInterface().getUser(TokenKeeper.getToken().getToken(), TokenKeeper.getToken().getUid())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<User>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(User user) {
                    System.out.println("onNext: " + user.getScreen_name());

                }
            });
        } else if(event.isCancelled) {

        } else if(event.isFailed) {

        }
    }

    @Override
    public void requestLogin(Activity activity) {
        userAuthorizeHandler.requestLogin(activity);
    }

    @Override
    public void requestSignup(Activity activity) {
        userAuthorizeHandler.requestSignup(activity);
    }
}
