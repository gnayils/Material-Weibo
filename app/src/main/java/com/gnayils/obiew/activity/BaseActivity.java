package com.gnayils.obiew.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gnayils.obiew.Settings;

/**
 * Created by Gnayils on 09/07/2017.
 */

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = BaseActivity.class.getSimpleName();

    private int themeResource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(haveNoTheme()) {
            setTheme(themeResource = Settings.getThemeResource());
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(haveNoTheme() && Settings.getThemeResource() != themeResource) {
            recreate();
        }
    }

    public void recreate() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
