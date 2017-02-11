package com.gnayils.obiew.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.ViewUtils;

/**
 * Created by Gnayils on 30/01/2017.
 */

public class StatusCardView extends CardView {

    public AvatarView userAvatarView;
    public TextView screenNameTextView;
    public TextView statusTimeTextView;
    public TextView sourceTextTextView;
    public TextView statusSourceTextView;
    public TextView statusTextTextView;
    public StatusPicturesView statusPicturesView;

    public StatusCardView(Context context) {
        this(context, null);
    }

    public StatusCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(ViewUtils.dp2px(8), ViewUtils.dp2px(4), ViewUtils.dp2px(8), ViewUtils.dp2px(4));
        setLayoutParams(layoutParams);

        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT));
        relativeLayout.setPadding(ViewUtils.dp2px(8),ViewUtils.dp2px(8),ViewUtils.dp2px(8),ViewUtils.dp2px(8));

        userAvatarView = new AvatarView(context);
        userAvatarView.setId(View.generateViewId());
        RelativeLayout.LayoutParams avatarViewLayoutParams = new RelativeLayout.LayoutParams(ViewUtils.dp2px(62), ViewUtils.dp2px(62));
        avatarViewLayoutParams.addRule(RelativeLayout.ALIGN_LEFT | RelativeLayout.ALIGN_TOP);
        userAvatarView.setLayoutParams(avatarViewLayoutParams);

        screenNameTextView = new TextView(context);
        screenNameTextView.setText("用户名");
        screenNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        RelativeLayout.LayoutParams userNameTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        userNameTextViewLayoutParams.setMargins(ViewUtils.dp2px(8), ViewUtils.dp2px(8), 0, 0);
        userNameTextViewLayoutParams.addRule(RelativeLayout.ALIGN_TOP, userAvatarView.getId());
        userNameTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, userAvatarView.getId());
        screenNameTextView.setLayoutParams(userNameTextViewLayoutParams);

        statusTimeTextView = new TextView(context);
        statusTimeTextView.setText("15分钟前");
        statusTimeTextView.setId(View.generateViewId());
        statusTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        statusTimeTextView.setTextColor(getResources().getColor(R.color.colorSecondaryText));
        RelativeLayout.LayoutParams statusTimeTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        statusTimeTextViewLayoutParams.setMargins(ViewUtils.dp2px(8), 0, 0, ViewUtils.dp2px(8));
        statusTimeTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, userAvatarView.getId());
        statusTimeTextViewLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, userAvatarView.getId());
        statusTimeTextView.setLayoutParams(statusTimeTextViewLayoutParams);

        sourceTextTextView = new TextView(context);
        sourceTextTextView.setText("来自");
        sourceTextTextView.setId(View.generateViewId());
        sourceTextTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        sourceTextTextView.setTextColor(getResources().getColor(R.color.colorSecondaryText));
        RelativeLayout.LayoutParams sourceTextTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sourceTextTextViewLayoutParams.setMargins(ViewUtils.dp2px(8), 0, 0, 0);
        sourceTextTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, statusTimeTextView.getId());
        sourceTextTextViewLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, statusTimeTextView.getId());
        sourceTextTextView.setLayoutParams(sourceTextTextViewLayoutParams);

        statusSourceTextView = new TextView(context);
        statusSourceTextView.setText("微博 weibo.com");
        statusSourceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        statusSourceTextView.setTextColor(getResources().getColor(R.color.colorSecondaryText));
        RelativeLayout.LayoutParams sourceTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sourceTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, sourceTextTextView.getId());
        sourceTextViewLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, sourceTextTextView.getId());
        statusSourceTextView.setLayoutParams(sourceTextViewLayoutParams);

        statusTextTextView = new TextView(context);
        statusTextTextView.setId(View.generateViewId());
        statusTextTextView.setText("微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容");
        statusTextTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        statusTextTextView.setMovementMethod(LinkMovementMethod.getInstance());
        RelativeLayout.LayoutParams statusContextTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        statusContextTextViewLayoutParams.addRule(RelativeLayout.BELOW, userAvatarView.getId());
        statusTextTextView.setLayoutParams(statusContextTextViewLayoutParams);

        statusPicturesView = new StatusPicturesView(context);
        RelativeLayout.LayoutParams statusPicturesViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        statusPicturesViewLayoutParams.setMargins(0, ViewUtils.dp2px(4), 0, 0);
        statusPicturesViewLayoutParams.addRule(RelativeLayout.BELOW, statusTextTextView.getId());
        statusPicturesView.setLayoutParams(statusPicturesViewLayoutParams);

        relativeLayout.addView(userAvatarView);
        relativeLayout.addView(screenNameTextView);
        relativeLayout.addView(statusTimeTextView);
        relativeLayout.addView(sourceTextTextView);
        relativeLayout.addView(statusSourceTextView);
        relativeLayout.addView(statusTextTextView);
        relativeLayout.addView(statusPicturesView);

        addView(relativeLayout);
    }
}
