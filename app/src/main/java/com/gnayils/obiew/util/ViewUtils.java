package com.gnayils.obiew.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.DisplayMetrics;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;

/**
 * Created by Administrator on 8/13/2016.
 */
public class ViewUtils {

    public static int adjustColor(int color, float offsetFactor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] /= offsetFactor;
        return Color.HSVToColor(hsv);
    }

    public static int dp2px(Context context, int dp) {
        return (int) (dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int px2dp(Context context, int px) {
        return (int) (px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static Drawable getDrawableByAttribute(Context context, int attrId) {
        int[] attributes = new int[] { attrId };
        TypedArray typedArray = context.obtainStyledAttributes(attributes);
        Drawable drawable = typedArray.getDrawable(0);
        typedArray.recycle();
        return drawable;
    }

    public static RippleDrawable createRippleDrawable(int normalColor, float cornerRadius) {
        int pressedColor = adjustColor(normalColor, 1.25f);
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
                        normalColor
                }
        ), gradientDrawable, null);
        return rippleDrawable;
    }
}
