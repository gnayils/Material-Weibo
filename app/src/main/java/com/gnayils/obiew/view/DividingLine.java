package com.gnayils.obiew.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.gnayils.obiew.R;

/**
 * Created by Gnayils on 24/10/2016.
 */
public class DividingLine extends View {

    private Paint paint = new Paint();
    private Path path = new Path();
    private int lineColor;
    private int paddingColor;

    public DividingLine(Context context) {
        this(context, null);
    }

    public DividingLine(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DividingLine(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DividingLine(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DividingLine);
        lineColor = typedArray.getColor(R.styleable.DividingLine_lineColor, 0x00000000);
        paddingColor = typedArray.getColor(R.styleable.DividingLine_paddingColor, 0x00000000);
        typedArray.recycle();

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{10, 0}, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paint.setStrokeWidth(h * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        path.moveTo(0, getHeight());
        path.lineTo(getPaddingLeft(), getHeight());
        paint.setColor(paddingColor);
        canvas.drawPath(path, paint);

        path.reset();
        path.moveTo(getPaddingLeft(), getHeight());
        path.lineTo(getWidth() - getPaddingRight(), getHeight());
        paint.setColor(lineColor);
        canvas.drawPath(path, paint);

        path.reset();
        path.moveTo(getWidth() - getPaddingRight(), getHeight());
        path.lineTo(getWidth(), getHeight());
        paint.setColor(paddingColor);
        canvas.drawPath(path, paint);
    }
}
