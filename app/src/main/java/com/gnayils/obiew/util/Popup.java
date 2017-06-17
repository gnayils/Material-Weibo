package com.gnayils.obiew.util;

import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gnayils.obiew.Obiew;
import com.gnayils.obiew.R;

/**
 * Created by Gnayils on 19/02/2017.
 */

public class Popup {

    public static void toast(String message) {
        Toast.makeText(Obiew.getAppContext(), message, Toast.LENGTH_LONG).show();
    }

    public static MaterialDialog errorDialog(String title, String message) {
        return new MaterialDialog.Builder(Obiew.getCurrentActivity()).title(title).content(message)
                .titleColorRes(R.color.colorPrimary)
                .icon(ViewUtils.getTintedDrawable(Obiew.getAppContext(), R.drawable.ic_error, Obiew.getAppResources().getColor(R.color.colorPrimary)))
                .positiveText("确定")
                .canceledOnTouchOutside(false)
                .show();
    }

    public static MaterialDialog waringDialog(String title, String message) {
        return new MaterialDialog.Builder(Obiew.getCurrentActivity()).title(title).content(message)
                .titleColorRes(R.color.colorPrimary)
                .icon(ViewUtils.getTintedDrawable(Obiew.getAppContext(), R.drawable.ic_warning, Obiew.getAppResources().getColor(R.color.colorPrimary)))
                .positiveText("确定")
                .canceledOnTouchOutside(false)
                .show();
    }

    public static MaterialDialog infoDialog(String title, String message) {
        return new MaterialDialog.Builder(Obiew.getCurrentActivity()).title(title).content(message)
                .titleColorRes(R.color.colorPrimary)
                .icon(ViewUtils.getTintedDrawable(Obiew.getAppContext(), R.drawable.ic_info, Obiew.getAppResources().getColor(R.color.colorPrimary)))
                .positiveText("确定")
                .show();
    }

    public static MaterialDialog confirmDialog(String title, String message, String positiveText, MaterialDialog.SingleButtonCallback onPositiveCallback, String negativeText, MaterialDialog.SingleButtonCallback onNegativeCallback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(Obiew.getCurrentActivity()).title(title).content(message)
                .titleColorRes(R.color.colorPrimary)
                .icon(ViewUtils.getTintedDrawable(Obiew.getAppContext(), R.drawable.ic_info, Obiew.getAppResources().getColor(R.color.colorPrimary)))
                .canceledOnTouchOutside(false);
        if(positiveText != null && !positiveText.trim().isEmpty()) {
            builder.positiveText(positiveText);
            builder.onPositive(onPositiveCallback);
        }
        if(negativeText != null && !negativeText.trim().isEmpty()) {
            builder.negativeText(negativeText);
            builder.onNegative(onNegativeCallback);
        }
        return builder.show();
    }

    public static MaterialDialog indeterminateProgressDialog(String title, String message, boolean horizontalStyle) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(Obiew.getCurrentActivity()).title(title).content(message)
                .titleColorRes(R.color.colorPrimary)
                .progress(true, 0)
                .progressIndeterminateStyle(horizontalStyle)
                .canceledOnTouchOutside(false);
        return builder.show();
    }

}
