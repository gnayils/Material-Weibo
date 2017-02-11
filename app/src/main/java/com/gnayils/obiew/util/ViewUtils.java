package com.gnayils.obiew.util;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;

import com.gnayils.obiew.App;

/**
 * Created by Administrator on 8/13/2016.
 */
public class ViewUtils {

    public static int adjustColor(int color, float offsetFactor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= offsetFactor;
        return Color.HSVToColor(hsv);
    }

    public static int dp2px(int dp) {
        return dp2px(App.resources().getDisplayMetrics(), dp);
    }

    public static int px2dp(int px) {
        return px2dp(App.resources().getDisplayMetrics(), px);
    }

    public static int dp2px(DisplayMetrics displayMetrics, int dp) {
        return (int) (dp * ((float) displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int px2dp(DisplayMetrics displayMetrics, int px) {
        return (int) (px / ((float) displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
