package com.gnayils.obiew.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.ViewHelper;
import static com.gnayils.obiew.util.ViewHelper.*;

/**
 * Created by Gnayils on 9/25/16.
 */
public class ItemView extends RelativeLayout {

    public ItemView(Context context) {
        super(context);
    }

    public ItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setBackground(getResources().getDrawable(R.drawable.bg_ripple, context.getTheme()));
        setClickable(true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemView);
        Drawable icon = typedArray.getDrawable(R.styleable.ItemView_icon);
        String title = typedArray.getString(R.styleable.ItemView_title);
        String description = typedArray.getString(R.styleable.ItemView_description);
        Drawable moreIcon = typedArray.getDrawable(R.styleable.ItemView_moreIcon);


        ImageView iconImageView = new ImageView(context);
        iconImageView.setId(View.generateViewId());
        iconImageView.setImageDrawable(icon);
        LayoutParams layoutParams = new LayoutParams(dp2px(24), dp2px(24));
        layoutParams.setMargins(dp2px(8), 0, 0, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        iconImageView.setLayoutParams(layoutParams);
        addView(iconImageView);

        TextView titleTextView = new TextView(context);
        titleTextView.setTextSize(16);
        titleTextView.setText(title);
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dp2px(8), 0, 0, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, iconImageView.getId());
        titleTextView.setLayoutParams(layoutParams);
        addView(titleTextView);

        ImageView moreIconImageView = new ImageView(context);
        moreIconImageView.setId(View.generateViewId());
        moreIconImageView.setImageDrawable(moreIcon);
        layoutParams = new LayoutParams(dp2px(16), dp2px(16));
        layoutParams.setMargins(0, 0, dp2px(8), 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        moreIconImageView.setLayoutParams(layoutParams);
        addView(moreIconImageView);

        TextView descriptionTextView = new TextView(context);
        descriptionTextView.setTextSize(12);
        descriptionTextView.setTextColor(getResources().getColor(R.color.colorSecondaryText));
        descriptionTextView.setText(description);
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, dp2px(8), 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.LEFT_OF, moreIconImageView.getId());
        descriptionTextView.setLayoutParams(layoutParams);
        addView(descriptionTextView);

        typedArray.recycle();
    }
}
