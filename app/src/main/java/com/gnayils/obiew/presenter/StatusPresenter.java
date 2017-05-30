package com.gnayils.obiew.presenter;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;
import com.gnayils.obiew.interfaces.StatusInterface;
import com.gnayils.obiew.util.Popup;
import com.gnayils.obiew.util.Sync;
import com.gnayils.obiew.weibo.TextDecorator;
import com.gnayils.obiew.weibo.VideoURLFinder;
import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.api.StatusAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.StatusTimeline;
import com.gnayils.obiew.weibo.bean.UploadedPic;
import com.gnayils.obiew.weibo.bean.User;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gnayils on 18/03/2017.
 */

public class StatusPresenter implements StatusInterface.Presenter {

    public static final String TAG = StatusPresenter.class.getSimpleName();

    private StatusInterface.View statusView;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private long homeTimelineSinceId = 0;
    private int[] userTimelinePages = new int[Status.FEATURES_COUNT];

    public StatusPresenter(StatusInterface.View statusView) {
        this.statusView = statusView;
        this.statusView.setPresenter(this);
        Arrays.fill(userTimelinePages, 1);
    }

    @Override
    public void loadStatusTimeline(final boolean isLoadingLatest) {
        Subscription subscription = WeiboAPI.get(StatusAPI.class)
                .homeTimeline(isLoadingLatest ? 0L : homeTimelineSinceId, 0L)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, true);
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, false);
                    }
                })
                .doOnNext(new Action1<StatusTimeline>() {
                    @Override
                    public void call(StatusTimeline statusTimeline) {
                        Log.d(TAG, "VideoURLFinder find start");
                        long startTime = SystemClock.uptimeMillis();
                        try {
                            VideoURLFinder.find(statusTimeline);
                        } catch (Throwable throwable) {
                            throw Exceptions.propagate(throwable);
                        }
                        Log.d(TAG, "VideoURLFinder find end, took times: " + (SystemClock.uptimeMillis() - startTime));
                    }
                })
                .doOnNext(new Action1<StatusTimeline>() {
                    @Override
                    public void call(StatusTimeline statusTimeline) {
                        Log.d(TAG, "TextDecorator decorate start");
                        long startTime = SystemClock.uptimeMillis();
                        TextDecorator.decorate(statusTimeline);
                        Log.d(TAG, "TextDecorator decorate end, took times: " + (SystemClock.uptimeMillis() - startTime));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusTimeline>() {

                    @Override
                    public void onCompleted() {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, false);
                        Log.e(TAG, "update statusText timeline failed: ", e);
                    }

                    @Override
                    public void onNext(StatusTimeline timeline) {
                        Log.d(TAG, "update statusText timeline successful");
                        homeTimelineSinceId = timeline.max_id;
                        statusView.show(timeline, Status.FEATURE_ALL);
                    }
                });
        compositeSubscription.add(subscription);
    }

    @Override
    public void loadStatusTimeline(final boolean isLoadingLatest, User user, final int feature) {
        Subscription subscription = WeiboAPI.get(StatusAPI.class)
                .userTimeline(user.id, feature, 20, userTimelinePages[feature]++)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, true);
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, false);
                    }
                })
                .doOnNext(new Action1<StatusTimeline>() {
                    @Override
                    public void call(StatusTimeline statusTimeline) {
                        Log.d(TAG, "VideoURLFinder find start");
                        long startTime = SystemClock.uptimeMillis();
                        try {
                            VideoURLFinder.find(statusTimeline);
                        } catch (Throwable throwable) {
                            throw Exceptions.propagate(throwable);
                        }
                        Log.d(TAG, "VideoURLFinder find end, took times: " + (SystemClock.uptimeMillis() - startTime));
                    }
                })
                .doOnNext(new Action1<StatusTimeline>() {
                    @Override
                    public void call(StatusTimeline statusTimeline) {
                        Log.d(TAG, "TextDecorator decorate start");
                        long startTime = SystemClock.uptimeMillis();
                        TextDecorator.decorate(statusTimeline);
                        Log.d(TAG, "TextDecorator decorate end, took times: " + (SystemClock.uptimeMillis() - startTime));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusTimeline>() {

                    @Override
                    public void onCompleted() {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        statusView.showStatusLoadingIndicator(isLoadingLatest, false);
                        Log.e(TAG, "update statusText timeline failed: ", e);
                    }

                    @Override
                    public void onNext(StatusTimeline timeline) {
                        Log.d(TAG, "update statusText timeline successful");
                        statusView.show(timeline, feature);
                    }
                });
        compositeSubscription.add(subscription);
    }

    public void upload(String statusText, List<String> selectedPicturePath) {
        if(statusText != null && !statusText.trim().isEmpty()) {
            if(selectedPicturePath == null || selectedPicturePath.isEmpty()) {
                WeiboAPI.get(StatusAPI.class).update(App.context().getString(R.string.app_key), statusText)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Status>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "update status failed", e);
                            }

                            @Override
                            public void onNext(Status status) {
                                Log.e(TAG, "update status successful");
                            }
                        });
            }  else if (!selectedPicturePath.isEmpty() && selectedPicturePath.size() == 1) {
                RequestBody statusRequestBody = RequestBody.create(MultipartBody.FORM, statusText);
                File imageFile = new File(selectedPicturePath.get(0));
                RequestBody imageRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("pic", imageFile.getName(), imageRequestBody);
                WeiboAPI.get(StatusAPI.class).upload(statusRequestBody, imagePart)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Status>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "upload status with one image failed", e);
                            }

                            @Override
                            public void onNext(Status status) {
                                Log.e(TAG, "upload status with one image successful");
                            }
                        });
            } else if (!selectedPicturePath.isEmpty() && selectedPicturePath.size() > 1) {
                PublishStatusTask publishStatusTask  = new PublishStatusTask(statusText, selectedPicturePath);
                publishStatusTask.execute();
            }
        }
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        compositeSubscription.clear();
    }


    static class PublishStatusTask extends AsyncTask<Void, Void, Status> {

        String statusText;
        List<String> selectedPicturePath;
        CompositeSubscription compositeSubscription = new CompositeSubscription();
        com.gnayils.obiew.weibo.bean.Status result;
        Throwable exception;
        StatusAPI statusAPI = WeiboAPI.get(StatusAPI.class);

        PublishStatusTask(String statusText, List<String> selectedPicturePath) {
            this.statusText = statusText;
            this.selectedPicturePath = selectedPicturePath;
        }

        @Override
        protected com.gnayils.obiew.weibo.bean.Status doInBackground(Void... params) {
            final StringBuffer picIds = new StringBuffer();
            for(final String path : selectedPicturePath) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), new File(path));
                Subscription subscription = statusAPI.uploadPic(requestBody)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<UploadedPic>() {

                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "upload picture failed: ", e);
                                exception = new Throwable(e);
                                Sync.notifyAll(selectedPicturePath);
                            }

                            @Override
                            public void onNext(UploadedPic uploadedPic) {
                                picIds.append(uploadedPic.pic_id);
                                picIds.append(",");
                                Sync.notifyAll(selectedPicturePath);
                            }
                        });
                compositeSubscription.add(subscription);
                Sync.wait(selectedPicturePath);
                if(exception != null) {
                    return null;
                }
            }
            Subscription subscription = statusAPI.uploadUrlText(App.context().getString(R.string.app_key), statusText, picIds.substring(0, picIds.length() - 1))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<com.gnayils.obiew.weibo.bean.Status>() {

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "publish status failed: ", e);
                            exception = new Throwable(e);
                            Sync.notifyAll(statusText);
                        }

                        @Override
                        public void onNext(com.gnayils.obiew.weibo.bean.Status  status) {
                            result = status;
                            Sync.notifyAll(statusText);
                        }
                    });
            compositeSubscription.add(subscription);
            Sync.wait(statusText);
            if(exception != null) {
                return null;
            }
            return result;
        }

        @Override
        protected void onCancelled() {
            compositeSubscription.clear();
        }

        @Override
        protected void onPostExecute(com.gnayils.obiew.weibo.bean.Status status) {
            if(exception != null) {
                Popup.toast("Publish status failed: " + exception.getMessage());
            }
            if(status != null) {
                Popup.toast("Publish status successful");
            }
        }
    }
}
