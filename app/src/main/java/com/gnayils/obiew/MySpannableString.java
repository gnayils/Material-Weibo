package com.gnayils.obiew;

import android.text.SpannableString;

import java.io.Serializable;

/**
 * Created by Gnayils on 06/05/2017.
 */

public class MySpannableString extends SpannableString implements Serializable {

    public MySpannableString(CharSequence source) {
        super(source);
    }

}
