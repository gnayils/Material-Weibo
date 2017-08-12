package com.gnayils.obiew.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;

import com.gnayils.obiew.R;
import com.gnayils.obiew.Preferences;
import com.gnayils.obiew.view.SwipeBackLayout;

/**
 * Created by Gnayils on 09/07/2017.
 */

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = BaseActivity.class.getSimpleName();
    public static final String ARGS_KEY_RECREATED = "ARGS_KEY_RECREATED";

    private int themeResource;
    protected SwipeBackLayout swipeBackLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(haveNoTheme()) {
            setTheme(themeResource = Preferences.getThemeResource());
        }
        super.onCreate(savedInstanceState);
        swipeBackLayout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.view_swipe_back, null);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        swipeBackLayout.attachToActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(haveNoTheme() && Preferences.getThemeResource() != themeResource) {
            recreate();
        }
    }

    public void recreate() {
        Intent intent = getIntent();
        intent.putExtra(ARGS_KEY_RECREATED, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public boolean isRecreated() {
        return getIntent().getBooleanExtra(ARGS_KEY_RECREATED, false);
    }

    private boolean haveNoTheme() {
        try {
            ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), 0);
            return activityInfo.theme == 0;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, String.format("get the activity [%s] info failed", getComponentName().getClassName()));
        }
        return false;
    }
}
