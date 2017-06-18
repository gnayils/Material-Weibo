package com.gnayils.obiew.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.ViewUtils;

/**
 * Created by Gnayils on 04/05/2017.
 */

public class PictureCardView extends CardView {

    public final GiFHintImageView imageView;

    public PictureCardView(Context context) {
        this(context, null);
    }

    public PictureCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PictureCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        imageView = new GiFHintImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setForegroundResource(ViewUtils.getResourceIdByAttrId(context, R.attr.selectableItemBackground));
        imageView.setClickable(true);
        CardView.LayoutParams imageViewLayoutParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.MATCH_PARENT);
        imageViewLayoutParams.gravity = Gravity.CENTER;
        imageView.setLayoutParams(imageViewLayoutParams);
        addView(imageView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
