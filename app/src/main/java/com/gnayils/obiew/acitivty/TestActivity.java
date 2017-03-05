package com.gnayils.obiew.acitivty;

import android.app.Activity;
import android.os.Bundle;

import com.gnayils.obiew.R;

/**
 * Created by Gnayils on 19/11/2016.
 */

public class TestActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_status_card);
    }
}
