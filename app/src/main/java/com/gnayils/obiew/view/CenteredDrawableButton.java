package com.gnayils.obiew.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Gnayils on 19/02/2017.
 */

public class CenteredDrawableButton extends Button {


    public CenteredDrawableButton(Context context) {
        super(context);
    }

    public CenteredDrawableButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CenteredDrawableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            int drawableLeftWidth = 0, drawableRightWidth = 0;
            if (drawables[0] != null) {
                drawableLeftWidth = drawables[0].getIntrinsicWidth();
            }
            if(drawables[2] != null) {
                drawableRightWidth = drawables[2].getIntrinsicWidth();
            }
            float textWidth = getPaint().measureText(getText().toString());
            int drawablePadding = getCompoundDrawablePadding();
            float contentWidth = drawablePadding + drawableLeftWidth + drawablePadding + textWidth + drawablePadding + drawableRightWidth + drawablePadding;
            setPadding(0, 0, (int) (getWidth() - contentWidth), 0);
            canvas.translate((getWidth() - contentWidth) / 2, 0);
        }
        super.onDraw(canvas);
    }
}
