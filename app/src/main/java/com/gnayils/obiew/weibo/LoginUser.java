package com.gnayils.obiew.weibo;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.weibo.bean.User;

/**
 * Created by Gnayils on 25/03/2017.
 */

public class LoginUser {

    private static User loginUser;

    public static void setUser(User user) {
        loginUser = user;
    }

    public static User getUser() {
        return loginUser;
    }

}
