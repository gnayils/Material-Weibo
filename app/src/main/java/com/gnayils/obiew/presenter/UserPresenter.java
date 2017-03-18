package com.gnayils.obiew.presenter;

import android.app.Activity;
import android.util.Log;

import com.gnayils.obiew.interfaces.UserInterface;
import com.gnayils.obiew.weibo.bean.StatusTimeline;
import com.gnayils.obiew.weibo.api.StatusAPI;
import com.gnayils.obiew.weibo.api.UserAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.User;
import com.gnayils.obiew.event.AuthorizeResultEvent;
import com.gnayils.obiew.weibo.TokenKeeper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gnayils on 12/11/2016.
 */

public class UserPresenter implements UserInterface.Presenter {

    public static final String TAG = UserPresenter.class.getSimpleName();

    private UserInterface.View userProfileView;

    private UserAuthorizeHandler userAuthorizeHandler = new UserAuthorizeHandler();;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();


    public UserPresenter(UserInterface.View userProfileView) {
        this.userProfileView = userProfileView;
        this.userProfileView.setPresenter(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        compositeSubscription.clear();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void OnAuthorizeResult(AuthorizeResultEvent event) {
        if(event.isSuccessful) {
            WeiboAPI.get(UserAPI.class)
                    .show(TokenKeeper.getToken().getToken(), TokenKeeper.getToken().getUid())
                    .subscribe(new Subscriber<User>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "login failed: ", e);
                }

                @Override
                public void onNext(User user) {
                    userProfileView.show(user);
                }
            });
        } else if(event.isCancelled) {
            Log.i(TAG, "login cancelled");
        } else if(event.isFailed) {
            Log.e(TAG, "login failed: " + event.failedCause);
        }
    }

    @Override
    public void requestLogin(Activity activity) {
        userAuthorizeHandler.requestLogin(activity);
    }

    @Override
    public void requestSignUp(Activity activity) {
        userAuthorizeHandler.requestSignup(activity);
    }


}
