package com.gnayils.obiew.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.ViewUtils;

/**
 * Created by Gnayils on 17/11/2016.
 */

public class AvatarView extends FrameLayout {

    public static final float VERIFIED_ICON_IMAGE_VIEW_SIZE_RATIO = 20f / 64f;

    public CircleImageView avatarCircleImageView;
    public ImageView verifiedIconImageView;

    public AvatarView(Context context) {
        this(context, null);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        avatarCircleImageView = new CircleImageView(context);
        avatarCircleImageView.setForegroundResource(ViewUtils.getResourceIdByAttrId(context, R.attr.selectableItemBackground));
        avatarCircleImageView.setImageResource(R.drawable.ic_avatar_default);
        LayoutParams avatarCircleImageViewLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        avatarCircleImageViewLayoutParam.gravity = Gravity.CENTER;
        avatarCircleImageView.setLayoutParams(avatarCircleImageViewLayoutParam);

        addView(avatarCircleImageView);

        verifiedIconImageView = new ImageView(context);
        addView(verifiedIconImageView);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AvatarView, defStyleAttr, defStyleRes);
        Drawable avatarImage = typedArray.getDrawable(R.styleable.AvatarView_avatarImage);
        if(avatarImage != null) {
            avatarCircleImageView.setImageDrawable(avatarImage);
        }
        Drawable verifiedIcon = typedArray.getDrawable(R.styleable.AvatarView_verifiedIcon);
        if(verifiedIcon != null) {
            verifiedIconImageView.setImageDrawable(verifiedIcon);
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));

        int verifiedIconImageViewSize = (int) (size * VERIFIED_ICON_IMAGE_VIEW_SIZE_RATIO);
        LayoutParams verifiedIconImageViewLayoutParam = new LayoutParams(verifiedIconImageViewSize, verifiedIconImageViewSize);
        verifiedIconImageViewLayoutParam.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        verifiedIconImageView.setLayoutParams(verifiedIconImageViewLayoutParam);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
