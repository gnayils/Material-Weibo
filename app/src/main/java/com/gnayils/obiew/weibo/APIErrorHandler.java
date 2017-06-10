package com.gnayils.obiew.weibo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gnayils.obiew.Obiew;
import com.gnayils.obiew.activity.LoginActivity;
import com.gnayils.obiew.util.Popup;
import com.gnayils.obiew.weibo.bean.APIError;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import rx.functions.Action1;

import static com.gnayils.obiew.weibo.APIErrorHandler.ExceptionHandler.HTTP_EXCEPTION;
import static com.gnayils.obiew.weibo.APIErrorHandler.ExceptionHandler.IO_EXCEPTION;

/**
 * Created by Gnayils on 10/06/2017.
 */

public class APIErrorHandler implements Action1<Throwable> {

    public static final String TAG = APIErrorHandler.class.getName();

    private static Handler handler = new ExceptionHandler();

    private Retrofit retrofit;

    public APIErrorHandler(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    @Override
    public void call(Throwable cause) {
        Log.e(TAG, "error occurred on api invocation", cause);
        if (cause instanceof HttpException) {
            HttpException httpException = (HttpException) cause;
            Converter<ResponseBody, APIError> converter = retrofit.responseBodyConverter(APIError.class, new Annotation[0]);
            try {
                APIError apiError = converter.convert(httpException.response().errorBody());
                handler.sendMessage(handler.obtainMessage(HTTP_EXCEPTION, apiError));
            } catch (IOException e) {
                Log.e(TAG, "convert http response error body to APIError failed", e);
            }
        } else if (cause instanceof IOException) {
            handler.sendMessage(handler.obtainMessage(IO_EXCEPTION, cause));
        } else {
            Log.e(TAG, "cannot handle this error", cause);
        }
    }

    static class ExceptionHandler extends Handler {

        public static final int HTTP_EXCEPTION = 0;
        public static final int IO_EXCEPTION = 1;

        public ExceptionHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HTTP_EXCEPTION:
                    if(msg.obj instanceof APIError) {
                        handleHttpException((APIError) msg.obj);
                    }
                    break;
                case IO_EXCEPTION:
                    if(msg.obj instanceof IOException) {
                        Popup.toast(String.format("网络异常: %s", ((IOException) msg.obj).getMessage()));
                    }
                    break;
                default:
                    Log.e(TAG, "cannot handle this message: " + msg.what);
                    break;
            }
        }

        private void handleHttpException(APIError apiError) {
            switch (apiError.error_code) {
                case APIError.INVALID_ACCESS_TOKEN:
                case APIError.EXPIRED_TOKEN:
                    Popup.confirmDialog("授权过期", "请重新登录微博账号进行授权", "确定", new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Account.clearCache(Obiew.getAppContext());
                            LoginActivity.start(Obiew.getAppContext());
                        }
                    }, null, null);
                    break;
                default:
                    Popup.toast(String.format("操作失败: %s[%d]", apiError.error, apiError.error_code));
                    break;
            }
        }
    }
}
