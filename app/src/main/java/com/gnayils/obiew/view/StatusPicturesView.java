package com.gnayils.obiew.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.acitivty.PicturePagerActivity;
import com.gnayils.obiew.bean.Status;
import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.util.ViewUtils;

import java.util.List;

/**
 * Created by Gnayils on 26/11/2016.
 */

public class StatusPicturesView extends ViewGroup implements View.OnClickListener{

    public static final int MARGIN_IN_IMAGES = ViewUtils.dp2px(4);

    private int imageViewVisibleCount;
    private List<Status.PicUrls> picUrlsList;

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
            //imageView.setImageDrawable(getResources().getDrawableWithAttribute(R.drawable.bg_cover_default, getContext().getTheme()));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackground(ViewUtils.createRippleDrawable(getResources().getColor(R.color.colorThumbnailBg), ViewUtils.dp2px(2)));
            imageView.setClipToOutline(true);
            imageView.setOnClickListener(this);
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
        this.picUrlsList = picUrlsList;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.setVisibility(View.GONE);
            if(i < picUrlsList.size()) {
                Status.PicUrls picUrls = picUrlsList.get(i);
                child.setVisibility(View.VISIBLE);
                BitmapLoader.getInstance().loadBitmap(picUrls.thumbnail_pic.replace("/thumbnail/", "/bmiddle/" /*"large"*/), (ImageView) child);
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

        int imageSize = (MeasureSpec.getSize(widthMeasureSpec) - MARGIN_IN_IMAGES * 2 - getPaddingLeft() - getPaddingRight()) / 3 ;

        if(imageViewVisibleCount > 0) {
            for (int i = 0; i < imageViewVisibleCount; i++) {
                View child = getChildAt(i);
                if(imageViewVisibleCount == 1) {
                    child.setLayoutParams(new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT));
                } else {
                    child.setLayoutParams(new MarginLayoutParams(imageSize, imageSize));
                }
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }

            int rows = (int) Math.ceil(imageViewVisibleCount / 3d);
            View child = getChildAt(0);
            setMeasuredDimension(widthMeasureSpec, resolveSizeAndState(child.getMeasuredHeight() * rows + (rows - 1) * MARGIN_IN_IMAGES + getPaddingTop() + getPaddingBottom(), heightMeasureSpec, child.getMeasuredState()));
        } else {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int childLeft =  getPaddingLeft();
        int childTop = getPaddingTop();

        for (int i = 0; i < imageViewVisibleCount; i++) {

            View child = getChildAt(i);
            child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
            childLeft = child.getRight() + MARGIN_IN_IMAGES;
            childTop = child.getTop();

            if(imageViewVisibleCount == 4 && i == 1) {
                childLeft = getPaddingLeft();
                childTop = child.getBottom() + MARGIN_IN_IMAGES;
            } else if(5 <= imageViewVisibleCount && imageViewVisibleCount <= 9 && (i == 2 || i == 5)) {
                childLeft = getPaddingLeft();
                childTop = child.getBottom() + MARGIN_IN_IMAGES;
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), PicturePagerActivity.class);
        int position = 0;
        for(int i=0; i<imageViewVisibleCount; i++) {
            if(v == getChildAt(i)) {
                position = i;
            }
        }
        intent.putExtra(PicturePagerActivity.EXTRA_CURRENT_PICTURE_POSITION, position);
        String[] picUrls = new String[picUrlsList.size()];
        for(int i=0; i<picUrlsList.size(); i++) {
            picUrls[i] = picUrlsList.get(i).thumbnail_pic;
        }
        intent.putExtra(PicturePagerActivity.EXTRA_PICTURE_URLS, picUrls);
        getContext().startActivity(intent);
    }
}
