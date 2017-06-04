package com.gnayils.obiew.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.DisplayMetrics;
import android.util.Size;
import android.util.TypedValue;

import com.afollestad.materialdialogs.Theme;
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
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static int px2dp(Context context, int px) {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getActionBarHeight(Context context)  {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return 0;
    }

    public static Size getScreenSize(Context context) {
        return new Size(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels);
    }

    public static Drawable getDrawableByAttrId(Context context, int attrId) {
        TypedArray typedArray = context.obtainStyledAttributes(R.style.AppTheme, new int[] { attrId });
        Drawable drawable = typedArray.getDrawable(0);
        typedArray.recycle();
        return drawable;
    }

    public static int getResourceIdByAttrId(Context context, int attrId) {
        TypedArray typedArray = context.obtainStyledAttributes(R.style.AppTheme, new int[] { attrId });
        int resourceId = typedArray.getResourceId(0, 0);
        typedArray.recycle();
        return resourceId;
    }

    public static Drawable getTranslucentDrawable(Context context, int resId, int alpha) {
        Drawable drawable = context.getResources().getDrawable(resId, context.getTheme());
        Drawable mutatedDrawable = drawable.mutate();
        mutatedDrawable.setAlpha(alpha);
        return mutatedDrawable;
    }

    public static Drawable getTintedDrawable(Context context, int resId, int color) {
        return getTintedDrawable(context, resId, color, PorterDuff.Mode.SRC_ATOP);
    }

    public static Drawable getTintedDrawable(Context context, int resId, int color, PorterDuff.Mode mode) {
        Drawable drawable = context.getResources().getDrawable(resId, context.getTheme());
        return tintDrawable(drawable, color, mode);
    }

    public static Drawable tintDrawable(Drawable drawable, int color) {
        return tintDrawable(drawable, color, PorterDuff.Mode.SRC_ATOP);
    }

    public static Drawable tintDrawable(Drawable drawable, int color, PorterDuff.Mode mode) {
        Drawable mutatedDrawable = drawable.mutate();
        mutatedDrawable.setColorFilter(color, mode);
        return mutatedDrawable;
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

    public static Drawable createDividerDrawable(Context context, int thickness, int backgroundColor, int foregroundColor, int foregroundLeftPadding) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(backgroundColor);
        background.setShape(GradientDrawable.RECTANGLE);
        background.setSize(0, thickness);
        GradientDrawable foreground = new GradientDrawable();
        foreground.setColor(foregroundColor);
        foreground.setShape(GradientDrawable.RECTANGLE);
        foreground.setSize(0, thickness);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{background, foreground});
        layerDrawable.setLayerInset(1, foregroundLeftPadding, 0, 0, 0);
        return layerDrawable;
    }

}
