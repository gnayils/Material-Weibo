package com.gnayils.obiew.view;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gnayils.obiew.R;
import com.gnayils.obiew.activity.PicturePagerActivity;
import com.gnayils.obiew.weibo.bean.PicUrls;
import com.gnayils.obiew.bmpldr.BitmapLoader;
import static com.gnayils.obiew.util.ViewUtils.*;

import java.util.List;

/**
 * Created by Gnayils on 26/11/2016.
 */

public class StatusPicturesView extends ViewGroup implements View.OnClickListener{

    public final int MARGIN_IN_IMAGES = dp2px(getContext(), 4);

    private int imageViewVisibleCount;
    private List<PicUrls> picUrlsList;

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
            ForegroundImageView imageView = new ForegroundImageView(getContext());
            imageView.setLayoutParams(new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setForegroundResource(R.drawable.fg_status_picture_thumbnail_mask);
            imageView.setBackgroundResource(R.drawable.bg_status_picture_thumbnail);
            imageView.setClipToOutline(true);
            imageView.setOnClickListener(this);
            addView(imageView);
        }
    }

    public void setPictureUrls(List<PicUrls> picUrlsList) {
        if(picUrlsList == null) {
            setVisibility(View.GONE);
            return;
        }
        if(picUrlsList.size() > getChildCount()) {
            setVisibility(View.GONE);
            throw new IllegalArgumentException("picture videoUrls too much for the StatusPicturesView");
        }
        this.picUrlsList = picUrlsList;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.setVisibility(View.GONE);
            if(i < picUrlsList.size()) {
                PicUrls picUrls = picUrlsList.get(i);
                child.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(picUrls.middleThumbnailPic()).into((ImageView)child);
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
                    LayoutParams layoutParams = child.getLayoutParams();
                    layoutParams.width = (int) (imageSize * 3 * 0.625f);
                    layoutParams.height = (int) (layoutParams.width * 0.75f);
                    child.setLayoutParams(layoutParams);
                } else {
                    LayoutParams layoutParams = child.getLayoutParams();
                    layoutParams.width = imageSize;
                    layoutParams.height = imageSize;
                    child.setLayoutParams(layoutParams);
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
        for(int i = 0; i < imageViewVisibleCount; i ++) {
            if(v == getChildAt(i)) {
                PicturePagerActivity.start(getContext(), i, picUrlsList);
                break;
            }
        }
    }
}
