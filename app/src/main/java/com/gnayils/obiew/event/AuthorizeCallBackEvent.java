package com.gnayils.obiew.event;

import android.content.Intent;

/**
 * Created by Gnayils on 13/11/2016.
 */

public class AuthorizeCallBackEvent {

    public int requestCode;
    public int resultCode;
    public Intent data;

    public AuthorizeCallBackEvent(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }
}
