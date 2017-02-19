package com.gnayils.obiew.util;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
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

    public static RippleDrawable createRippleDrawable(int normalColor, float cornerRadius) {
        int pressedColor = adjustColor(normalColor, 0.85f);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(normalColor);
        gradientDrawable.setCornerRadius(cornerRadius);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        RippleDrawable rippleDrawable = new RippleDrawable(new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_activated},
                        new int[]{}},
                new int[]{
                        pressedColor,
                        pressedColor,
                        pressedColor,
                        normalColor }
        ), gradientDrawable, null);
        return rippleDrawable;
    }


    public static RippleDrawable getPressedColorRippleDrawable(int normalColor, int pressedColor) {
        return new RippleDrawable(new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_activated},
                        new int[]{}},
                new int[]{
                        pressedColor,
                        pressedColor,
                        pressedColor,
                        normalColor }
        ), new ColorDrawable(normalColor), null);
    }
}
