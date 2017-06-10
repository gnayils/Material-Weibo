package com.gnayils.obiew.weibo.service;

import android.os.AsyncTask;
import android.util.Log;

import com.gnayils.obiew.Obiew;
import com.gnayils.obiew.R;
import com.gnayils.obiew.util.Sync;
import com.gnayils.obiew.weibo.api.StatusAPI;
import com.gnayils.obiew.weibo.api.WeiboAPI;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.UploadedPic;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gnayils on 31/05/2017.
 */
public class StatusPublishTask extends AsyncTask<Void, Void, Status> {

    private static final String TAG = StatusPublishTask.class.getSimpleName();

    private String statusText;
    private List<String> selectedPhotoPaths;
    private SubscriberAdapter<com.gnayils.obiew.weibo.bean.Status> subscriberAdapter;

    private com.gnayils.obiew.weibo.bean.Status result;
    private Throwable exception;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private StatusAPI statusAPI = WeiboAPI.get(StatusAPI.class);

    public StatusPublishTask(String statusText, List<String> selectedPhotoPaths, SubscriberAdapter<com.gnayils.obiew.weibo.bean.Status> subscriberAdapter) {
        this.statusText = statusText;
        this.selectedPhotoPaths = selectedPhotoPaths;
        this.subscriberAdapter = subscriberAdapter;
    }

    @Override
    protected void onPreExecute() {
        subscriberAdapter.onSubscribe();
    }

    @Override
    protected com.gnayils.obiew.weibo.bean.Status doInBackground(Void... params) {
        final StringBuffer picIds = new StringBuffer();
        for (String photoPath : selectedPhotoPaths) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), new File(photoPath));
            Subscription subscription = statusAPI.uploadPic(requestBody)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<UploadedPic>() {

                        @Override
                        public void onCompleted() {
                            Sync.notifyAll(selectedPhotoPaths);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "upload picture failed: ", e);
                            exception = e;
                            Sync.notifyAll(selectedPhotoPaths);
                        }

                        @Override
                        public void onNext(UploadedPic uploadedPic) {
                            picIds.append(uploadedPic.pic_id);
                            picIds.append(",");
                        }
                    });
            subscriptions.add(subscription);
            Sync.wait(selectedPhotoPaths);
            if (exception != null) {
                subscriptions.clear();
                return null;
            }
        }
        Subscription subscription = statusAPI.uploadUrlText(Obiew.getAppResources().getString(R.string.app_key),
                statusText, picIds.substring(0, picIds.length() - 1))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<com.gnayils.obiew.weibo.bean.Status>() {

                    @Override
                    public void onCompleted() {
                        Sync.notifyAll(statusText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "publish status failed: ", e);
                        exception = e;
                        Sync.notifyAll(statusText);
                    }

                    @Override
                    public void onNext(com.gnayils.obiew.weibo.bean.Status status) {
                        result = status;
                    }
                });
        subscriptions.add(subscription);
        Sync.wait(statusText);
        if (exception != null) {
            subscriptions.clear();
            return null;
        }
        return result;
    }

    @Override
    protected void onCancelled() {
        subscriptions.clear();
        subscriberAdapter.onUnsubscribe();
    }

    @Override
    protected void onPostExecute(com.gnayils.obiew.weibo.bean.Status status) {
        if (exception != null) {
            subscriberAdapter.onError(exception);
            subscriberAdapter.onUnsubscribe();
        }
        if (status != null) {
            subscriberAdapter.onNext(status);
            subscriberAdapter.onCompleted();
            subscriberAdapter.onUnsubscribe();
        }
    }
}
