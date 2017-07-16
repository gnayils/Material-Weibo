package com.gnayils.obiew.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.ViewUtils;

/**
 * Created by Gnayils on 20/05/2017.
 */

public class GiFHintImageView extends ForegroundImageView {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF backgroundRect;
    private int rectWidth;
    private int rectHeight;
    private int round;

    private boolean hintVisible = false;

    public GiFHintImageView(Context context) {
        this(context, null);
    }

    public GiFHintImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiFHintImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setTextSize(ViewUtils.dp2px(context, 12));
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        rectWidth = (int) paint.measureText("GIF") + ViewUtils.dp2px(context, 4);
        rectHeight = (int) (Math.abs(fontMetrics.ascent) + fontMetrics.descent);
        round = ViewUtils.dp2px(context, 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(hintVisible) {
            canvas.save();
            Rect drawingRect = new Rect();
            getDrawingRect(drawingRect);
            backgroundRect = new RectF(drawingRect.right - rectWidth, drawingRect.bottom - rectHeight, drawingRect.right, drawingRect.bottom);

            paint.setColor(getResources().getColor(R.color.accentColor));
            paint.setAlpha(180);
            canvas.drawRoundRect(backgroundRect, round, round, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText("GIF", backgroundRect.left + ViewUtils.dp2px(getContext(), 2), backgroundRect.top - paint.getFontMetrics().ascent, paint);
            canvas.restore();
        }
    }

    public void setHintVisible(boolean visible) {
        this.hintVisible = visible;
        invalidate();
    }
}
