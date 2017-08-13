package com.gnayils.obiew.util;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.gnayils.obiew.Obiew;
import com.gnayils.obiew.R;

import java.util.List;

/**
 * Created by Gnayils on 19/02/2017.
 */

public class Popup {

    public static void toast(String message) {
        Toast.makeText(Obiew.getAppContext(), message, Toast.LENGTH_LONG).show();
    }


    public static MaterialDialog singleChooseDialog(String title, int preselectedIndex, List items, MaterialDialog.ListCallbackSingleChoice callbackSingleChoice) {
        return new MaterialDialog.Builder(Obiew.getCurrentActivity()).title(title)
                .items(items)
                .itemsCallbackSingleChoice(preselectedIndex, callbackSingleChoice)
                .titleColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorPrimaryInverse))
                .backgroundColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorViewBackground))
                .choiceWidgetColor(new ColorStateList(new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                }, new int[]{
                        ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorAccentInverse),
                        ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorSecondaryText)
                }))
                .show();
    }

    public static MaterialDialog errorDialog(String title, String message) {
        return new MaterialDialog.Builder(Obiew.getCurrentActivity()).title(title).content(message)
                .titleColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorPrimaryInverse))
                .backgroundColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorViewBackground))
                .positiveColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorAccentInverse))
                .icon(ViewUtils.getTintedDrawable(Obiew.getAppContext(), R.drawable.ic_error, ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorPrimaryInverse)))
                .positiveText("确定")
                .canceledOnTouchOutside(false)
                .show();
    }

    public static MaterialDialog waringDialog(String title, String message) {
        return new MaterialDialog.Builder(Obiew.getCurrentActivity()).title(title).content(message)
                .titleColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorPrimaryInverse))
                .backgroundColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorViewBackground))
                .positiveColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorAccentInverse))
                .icon(ViewUtils.getTintedDrawable(Obiew.getAppContext(), R.drawable.ic_warning, ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorPrimaryInverse)))
                .positiveText("确定")
                .canceledOnTouchOutside(false)
                .show();
    }

    public static MaterialDialog infoDialog(String title, String message) {
        return new MaterialDialog.Builder(Obiew.getCurrentActivity()).title(title).content(message)
                .titleColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorPrimaryInverse))
                .backgroundColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorViewBackground))
                .positiveColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorAccentInverse))
                .icon(ViewUtils.getTintedDrawable(Obiew.getAppContext(), R.drawable.ic_info, ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorPrimaryInverse)))
                .positiveText("确定")
                .show();
    }

    public static MaterialDialog confirmDialog(String title, String message, String positiveText, MaterialDialog.SingleButtonCallback onPositiveCallback, String negativeText, MaterialDialog.SingleButtonCallback onNegativeCallback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(Obiew.getCurrentActivity()).title(title).content(message)
                .titleColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorPrimaryInverse))
                .backgroundColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorViewBackground))
                .positiveColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorAccentInverse))
                .negativeColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorAccentInverse))
                .icon(ViewUtils.getTintedDrawable(Obiew.getAppContext(), R.drawable.ic_info, ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorPrimaryInverse)))
                .canceledOnTouchOutside(false);
        if (positiveText != null && !positiveText.trim().isEmpty()) {
            builder.positiveText(positiveText);
            builder.onPositive(onPositiveCallback);
        }
        if (negativeText != null && !negativeText.trim().isEmpty()) {
            builder.negativeText(negativeText);
            builder.onNegative(onNegativeCallback);
        }
        return builder.show();
    }

    public static MaterialDialog indeterminateProgressDialog(String title, String message, boolean horizontalStyle) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(Obiew.getCurrentActivity()).title(title).content(message)
                .titleColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorPrimaryInverse))
                .backgroundColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorViewBackground))
                .positiveColor(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorAccentInverse))
                .progress(true, 0)
                .progressIndeterminateStyle(horizontalStyle)
                .canceledOnTouchOutside(false);
        MaterialDialog materialDialog = builder.build();
        materialDialog.getProgressBar().getProgressDrawable().setColorFilter(ViewUtils.getColorByAttrId(Obiew.getAppContext(), R.attr.themeColorAccentInverse), PorterDuff.Mode.SRC_IN);
        materialDialog.show();
        return materialDialog;
    }

}
