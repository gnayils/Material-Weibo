package com.gnayils.obiew;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Gnayils on 09/07/2017.
 */

public class Preferences {

    public static final int STYLE_LIGHT_THEME = 0;
    public static final int STYLE_NIGHT_THEME = 1;

    public static final int[] THEME_RESOURCES = { R.style.AppTheme_Light, R.style.AppTheme_Dark };

    public static void toggleTheme() {
        PreferenceManager.getDefaultSharedPreferences(Obiew.getAppContext())
                .edit()
                .putInt("theme_style", getThemeStyle() == STYLE_LIGHT_THEME ? STYLE_NIGHT_THEME : STYLE_LIGHT_THEME)
                .commit();
    }

    private static int getThemeStyle() {
        int themeStyle = PreferenceManager.getDefaultSharedPreferences(Obiew.getAppContext())
                .getInt("theme_style", STYLE_LIGHT_THEME);
        return themeStyle;
    }

    public static boolean isNightTheme() {
        return getThemeResource() == R.style.AppTheme_Dark;
    }

    public static int getThemeResource() {
        return THEME_RESOURCES[getThemeStyle()];
    }

    public static String[] getAccount() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Obiew.getAppContext());
        return new String[]{sharedPreferences.getString("username", null), sharedPreferences.getString("password", null)};
    }

    public static void saveAccount(String username, String password) {
        PreferenceManager.getDefaultSharedPreferences(Obiew.getAppContext())
                .edit()
                .putString("username", username).putString("password", password)
                .commit();
    }

}
