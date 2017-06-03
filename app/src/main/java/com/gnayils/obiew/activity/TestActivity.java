package com.gnayils.obiew.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gnayils.obiew.R;
import com.gnayils.obiew.view.MaterialCircleImageView;
import com.gnayils.obiew.view.MaterialProgressDrawable;

/**
 * Created by Gnayils on 19/11/2016.
 */

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }
}
