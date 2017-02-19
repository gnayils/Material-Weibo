package com.gnayils.obiew.user;

import android.app.Activity;
import android.util.Log;

import com.gnayils.obiew.bean.Timeline;
import com.gnayils.obiew.util.Popup;
import com.gnayils.obiew.weibo.StatusAPI;
import com.gnayils.obiew.weibo.UserAPI;
import com.gnayils.obiew.weibo.WeiboAPI;
import com.gnayils.obiew.bean.User;
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

    private UserInterface.ProfileView userProfileProfileView;
    private UserInterface.StatusTimelineView statusTimelineView;

    private UserAuthorizeHandler userAuthorizeHandler;

    private CompositeSubscription compositeSubscription;

    private long timelineSinceId = 0;

    public UserPresenter(UserInterface.ProfileView userProfileProfileView, UserInterface.StatusTimelineView statusTimelineView) {
        this.userProfileProfileView = userProfileProfileView;
        this.userProfileProfileView.setPresenter(this);
        this.statusTimelineView = statusTimelineView;
        this.statusTimelineView.setPresenter(this);

        userAuthorizeHandler = new UserAuthorizeHandler();

        compositeSubscription = new CompositeSubscription();

        EventBus.getDefault().register(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {
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
                    userProfileProfileView.updateUser(user);
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

    @Override
    public void loadStatusTimeline(boolean latest) {
        compositeSubscription.clear();
        Subscription subscription = WeiboAPI.get(StatusAPI.class).homeTimeline(TokenKeeper.getToken().getToken(), latest ? 0L : this.timelineSinceId, 0L)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        statusTimelineView.showLoadingIndicator(true);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Timeline>(){

                    @Override
                    public void onCompleted() {
                        statusTimelineView.showLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        statusTimelineView.showLoadingIndicator(false);
                        Log.e(TAG, "update time line failed: ", e);
                    }

                    @Override
                    public void onNext(Timeline timeline) {
                        UserPresenter.this.timelineSinceId = timeline.max_id;
                        statusTimelineView.showStatusTimeline(timeline);
                    }
                });
        compositeSubscription.add(subscription);

    }
}
