package com.gnayils.obiew.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gnayils.obiew.R;
import com.gnayils.obiew.view.MaterialCircleImageView;
import com.gnayils.obiew.view.MaterialProgressDrawable;

/**
 * Created by Gnayils on 19/11/2016.
 */

public class TestActivity extends Activity{


    private MaterialCircleImageView mCircleView;
    private MaterialProgressDrawable mProgress;

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);

        mCircleView = new MaterialCircleImageView(this, 0xFFFAFAFA);
        ViewGroup.LayoutParams layoutParams =new ViewGroup.LayoutParams(200, 200);
        mCircleView.setLayoutParams(layoutParams);
        mProgress = new MaterialProgressDrawable(this, linearLayout);
        mProgress.setBackgroundColor(0xFFFAFAFA);
        mProgress.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);
        mProgress.setAlpha(255);
        mProgress.showArrow(true);
        mCircleView.setImageDrawable(mProgress);

        mCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mProgress.setStartEndTrim(0f, 1f);
                mProgress.start();
            }
        });

        linearLayout.addView(mCircleView);
    }
}
