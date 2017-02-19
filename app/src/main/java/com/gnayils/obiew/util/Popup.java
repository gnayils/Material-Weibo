package com.gnayils.obiew.util;

import android.widget.Toast;

import com.gnayils.obiew.App;

/**
 * Created by Gnayils on 19/02/2017.
 */

public class Popup {

    public static void toast(String message) {
        Toast.makeText(App.context(), message, Toast.LENGTH_LONG).show();
    }
}
