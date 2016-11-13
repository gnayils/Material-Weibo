package com.gnayils.obiew.util;

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
        DisplayMetrics metrics = App.resources().getDisplayMetrics();
        return (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int px2dp(int px) {
        DisplayMetrics metrics = App.resources().getDisplayMetrics();
        return (int) (px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
