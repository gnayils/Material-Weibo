package com.gnayils.obiew.util;

import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

/**
 * Created by Gnayils on 28/05/2017.
 */

public class BottomNavigationViewHelper {

    public static void centerMenuIcon(BottomNavigationView view) {
        BottomNavigationMenuView menuView = null;
        try {
            Field field = view.getClass().getDeclaredField("mMenuView");
            field.setAccessible(true);
            menuView = (BottomNavigationMenuView) field.get(view);
            if (menuView != null) {
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView menuItemView = (BottomNavigationItemView) menuView.getChildAt(i);
                    AppCompatImageView icon = (AppCompatImageView) menuItemView.getChildAt(0);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) icon.getLayoutParams();
                    params.gravity = Gravity.CENTER;
                    menuItemView.setShiftingMode(true);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to get mMenuView field", e);
        }
    }
}
