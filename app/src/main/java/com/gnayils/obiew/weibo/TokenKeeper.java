/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gnayils.obiew.weibo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gnayils.obiew.App;
import com.gnayils.obiew.weibo.bean.AccessToken;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

public class TokenKeeper {

    private static final String PREFERENCES_NAME = "shared_preferences_access_token";

    private static final String KEY_UID = "uid";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_EXPIRES_IN = "expires_in";

    private static AccessToken token;

    public static void writeToken(AccessToken token) {
        SharedPreferences pref = App.context().getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putString(KEY_UID, token.uid);
        editor.putString(KEY_ACCESS_TOKEN, token.access_token);
        editor.putLong(KEY_EXPIRES_IN, token.expires_in);
        editor.commit();
        TokenKeeper.token = token;
    }

    public static AccessToken readToken() {
        token = new AccessToken();
        SharedPreferences pref = App.context().getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        token.uid = pref.getString(KEY_UID, "");
        token.access_token = pref.getString(KEY_ACCESS_TOKEN, "");
        token.expires_in = pref.getLong(KEY_EXPIRES_IN, 0);
        return token;
    }

    public static AccessToken getToken() {
        if(token == null) {
            readToken();
        }
        return token;
    }

    public static void clear() {
        SharedPreferences pref = App.context().getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        token = null;
    }
}
