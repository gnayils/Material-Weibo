package com.gnayils.obiew;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;
import android.util.Log;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    private final String TAG = this.getClass().getSimpleName();
    public ApplicationTest() {
        super(Application.class);

    }

    public void testMemoryClass() {
        ActivityManager am = (ActivityManager) this.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        Log.v(TAG, "Memory class: " + am.getMemoryClass());
        Log.v(TAG, "Large Memory class: " + am.getLargeMemoryClass());
    }

}