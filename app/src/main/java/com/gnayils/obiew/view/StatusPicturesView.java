package com.gnayils.obiew.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.bean.Status;
import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.util.ViewUtils;

import java.util.List;

/**
 * Created by Gnayils on 26/11/2016.
 */

public class StatusPicturesView extends ViewGroup {

    public static final int MARGIN_IN_IMAGES = ViewUtils.dp2px(4);

    private int imageViewVisibleCount;

    public StatusPicturesView(Context context) {
        this(context, null);
    }

    public StatusPicturesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusPicturesView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public StatusPicturesView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        for (int i = 0; i < 9; i++) {
            ImageView imageView = new ImageView(getContext());
//            imageView.setImageDrawable(getResources().getDrawable(R.drawable.bg_cover_default, getContext().getTheme()));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackground(getResources().getDrawable(R.drawable.bg_status_thumbnail, getContext().getTheme()));
            imageView.setClipToOutline(true);
            addView(imageView);
        }
    }

    public void setPictureUrls(List<Status.PicUrls> picUrlsList) {
        if(picUrlsList == null) {
            setVisibility(View.GONE);
            return;
        }
        if(picUrlsList.size() > getChildCount()) {
            setVisibility(View.GONE);
            throw new IllegalArgumentException("picture urls too much for the StatusPicturesView");
        }

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.setVisibility(View.GONE);
            if(i < picUrlsList.size()) {
                Status.PicUrls picUrls = picUrlsList.get(i);
                child.setVisibility(View.VISIBLE);
                BitmapLoader.getInstance().loadBitmap(picUrls.thumbnail_pic, (ImageView) child);
            }
        }

        imageViewVisibleCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            imageViewVisibleCount += getChildAt(i).getVisibility() == View.VISIBLE ? 1 : 0;
        }
        if(imageViewVisibleCount > 0) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int imageSize = (MeasureSpec.getSize(widthMeasureSpec) - MARGIN_IN_IMAGES * 2) / 3;

        if(imageViewVisibleCount > 0) {
            for (int i = 0; i < imageViewVisibleCount; i++) {
                View child = getChildAt(i);
//                if(imageViewVisibleCount == 1) {
//                    child.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//                } else {
                    child.setLayoutParams(new LayoutParams(imageSize, imageSize));
//                }
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }

            int rows = (int) Math.ceil(imageViewVisibleCount / 3d);
            View child = getChildAt(0);
            setMeasuredDimension(widthMeasureSpec, resolveSizeAndState(child.getMeasuredHeight() * rows + (rows - 1) * MARGIN_IN_IMAGES, heightMeasureSpec, child.getMeasuredState()));
        } else {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int childLeft = 0;
        int childTop = 0;

        for (int i = 0; i < getChildCount(); i++) {

            View child = getChildAt(i);
            child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
            childLeft = child.getRight() + MARGIN_IN_IMAGES;
            childTop = child.getTop();

            if(getChildCount() == 4 && i == 1) {
                childLeft = 0;
                childTop = child.getBottom() + MARGIN_IN_IMAGES;
            } else if(5 <= getChildCount() && getChildCount() <= 9 && (i == 2 || i == 5)) {
                childLeft = 0;
                childTop = child.getBottom() + MARGIN_IN_IMAGES;
            }
        }
    }

    @Override
    public StatusPicturesView.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new StatusPicturesView.LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof StatusPicturesView.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new StatusPicturesView.LayoutParams(p);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(FrameLayout.LayoutParams source) {
            super(source);
        }
    }
}
