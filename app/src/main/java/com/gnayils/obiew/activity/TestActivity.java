package com.gnayils.obiew.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gnayils.obiew.R;
import com.gnayils.obiew.util.Popup;
import com.gnayils.obiew.view.MaterialCircleImageView;
import com.gnayils.obiew.view.MaterialProgressDrawable;

/**
 * Created by Gnayils on 19/11/2016.
 */

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void popupErrorDialog(View v) {
        Popup.errorDialog("title", "error message");
    }

    public void popupWarningDialog(View v) {
        Popup.waringDialog("title", "warning message");
    }

    public void popupInfoDialog(View v) {
        Popup.infoDialog("title", "info message");
    }

    public void popupConfirmDialog(View v) {
        Popup.confirmDialog("title", "confirm message", "可以", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            }
        }, "不可以", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            }
        });
    }

    public void popupProgressDialog(View v) {
        Popup.indeterminateProgressDialog("title", "progress message", true);
    }
}
