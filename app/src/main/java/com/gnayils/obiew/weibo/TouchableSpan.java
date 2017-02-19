package com.gnayils.obiew.weibo;

import android.text.TextPaint;
import android.text.style.ClickableSpan;

/**
 * Created by Gnayils on 18/02/2017.
 */

public abstract class TouchableSpan extends ClickableSpan {

    private boolean isPressed;
    private int pressedBackgroundColor;
    private int normalTextColor;
    private int pressedTextColor;

    public TouchableSpan(int normalTextColor, int pressedTextColor, int pressedBackgroundColor) {
        this.normalTextColor = normalTextColor;
        this.pressedTextColor = pressedTextColor;
        this.pressedBackgroundColor = pressedBackgroundColor;
    }

    public void setPressed(boolean isSelected) {
        isPressed = isSelected;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(isPressed ? pressedTextColor : normalTextColor);
        ds.bgColor = isPressed ? pressedBackgroundColor : 0x00000000;
        ds.setUnderlineText(false);
    }
}