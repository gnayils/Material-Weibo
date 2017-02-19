package com.gnayils.obiew.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.weibo.TouchableLinkMovementMethod;

import static com.gnayils.obiew.util.ViewUtils.*;
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

    public LinearLayout retweetedStatusView;
    public TextView retweetedStatusTextTextView;
    public StatusPicturesView retweetedStatusPicturesView;

    public CenteredDrawableButton repostButton;
    public CenteredDrawableButton commentButton;
    public CenteredDrawableButton likeButton;

    public StatusCardView(Context context) {
        this(context, null);
    }

    public StatusCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground(createRippleDrawable(Color.WHITE, getResources().getDimension(R.dimen.cardview_default_radius)));

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dp2px(4), dp2px(2), dp2px(4), dp2px(2));
        setLayoutParams(layoutParams);

        LinearLayout statusContentLayout = new LinearLayout(context);
        statusContentLayout.setOrientation(LinearLayout.VERTICAL);
        statusContentLayout.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT));

            RelativeLayout userInfoLayout = new RelativeLayout(context);
            userInfoLayout.setPadding(dp2px(8), dp2px(8), dp2px(8), dp2px(4));
            LinearLayout.LayoutParams userInfoLayoutLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            userInfoLayout.setLayoutParams(userInfoLayoutLayoutParams);

                userAvatarView = new AvatarView(context);
                userAvatarView.setId(View.generateViewId());
                RelativeLayout.LayoutParams avatarViewLayoutParams = new RelativeLayout.LayoutParams(dp2px(48), dp2px(48));
                avatarViewLayoutParams.addRule(RelativeLayout.ALIGN_LEFT | RelativeLayout.ALIGN_TOP);
                userAvatarView.setLayoutParams(avatarViewLayoutParams);

                screenNameTextView = new TextView(context);
                screenNameTextView.setText("用户名");
                screenNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                RelativeLayout.LayoutParams userNameTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                userNameTextViewLayoutParams.setMargins(dp2px(8), dp2px(4), 0, 0);
                userNameTextViewLayoutParams.addRule(RelativeLayout.ALIGN_TOP, userAvatarView.getId());
                userNameTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, userAvatarView.getId());
                screenNameTextView.setLayoutParams(userNameTextViewLayoutParams);

                statusTimeTextView = new TextView(context);
                statusTimeTextView.setText("15分钟前");
                statusTimeTextView.setId(View.generateViewId());
                statusTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                statusTimeTextView.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                RelativeLayout.LayoutParams statusTimeTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                statusTimeTextViewLayoutParams.setMargins(dp2px(8), 0, 0, dp2px(4));
                statusTimeTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, userAvatarView.getId());
                statusTimeTextViewLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, userAvatarView.getId());
                statusTimeTextView.setLayoutParams(statusTimeTextViewLayoutParams);

                sourceTextTextView = new TextView(context);
                sourceTextTextView.setText("来自");
                sourceTextTextView.setId(View.generateViewId());
                sourceTextTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                sourceTextTextView.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                RelativeLayout.LayoutParams sourceTextTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                sourceTextTextViewLayoutParams.setMargins(dp2px(8), 0, 0, 0);
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

            userInfoLayout.addView(userAvatarView);
            userInfoLayout.addView(screenNameTextView);
            userInfoLayout.addView(statusTimeTextView);
            userInfoLayout.addView(sourceTextTextView);
            userInfoLayout.addView(statusSourceTextView);

            statusTextTextView = new TextView(context);
            statusTextTextView.setId(View.generateViewId());
            statusTextTextView.setText("微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容");
            statusTextTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            statusTextTextView.setMovementMethod(TouchableLinkMovementMethod.getInstance());
            statusTextTextView.setPadding(dp2px(8), dp2px(4), dp2px(8), dp2px(4));
            LinearLayout.LayoutParams statusTextTextViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            statusTextTextView.setLayoutParams(statusTextTextViewLayoutParams);

            statusPicturesView = new StatusPicturesView(context);
            statusPicturesView.setPadding(dp2px(8), dp2px(4), dp2px(8), dp2px(4));
            LinearLayout.LayoutParams statusPicturesViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            statusPicturesView.setLayoutParams(statusPicturesViewLayoutParams);

            retweetedStatusView = new LinearLayout(context);
            retweetedStatusView.setOrientation(LinearLayout.VERTICAL);
            retweetedStatusView.setBackgroundColor(getResources().getColor(R.color.colorWindowBackground));
            LinearLayout.LayoutParams retweetedStatusViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            retweetedStatusView.setLayoutParams(retweetedStatusViewLayoutParams);
            retweetedStatusTextTextView = new TextView(context);
            retweetedStatusTextTextView.setId(View.generateViewId());
            retweetedStatusTextTextView.setText("微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容");
            retweetedStatusTextTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            retweetedStatusTextTextView.setMovementMethod(TouchableLinkMovementMethod.getInstance());
            retweetedStatusTextTextView.setPadding(dp2px(8), dp2px(4), dp2px(8), dp2px(4));
            LinearLayout.LayoutParams retweetedStatusTextTextViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            retweetedStatusTextTextView.setLayoutParams(retweetedStatusTextTextViewLayoutParams);
            retweetedStatusPicturesView = new StatusPicturesView(context);
            retweetedStatusPicturesView.setPadding(dp2px(8), dp2px(4), dp2px(8), dp2px(4));
            LinearLayout.LayoutParams retweetedStatusPicturesViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            retweetedStatusPicturesView.setLayoutParams(retweetedStatusPicturesViewLayoutParams);
            retweetedStatusView.addView(retweetedStatusTextTextView);
            retweetedStatusView.addView(retweetedStatusPicturesView);

            LinearLayout hotrankLayout = new LinearLayout(context);
            hotrankLayout.setOrientation(LinearLayout.HORIZONTAL);
            hotrankLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                LinearLayout.LayoutParams hotrankButtonsLayoutParams = new LinearLayout.LayoutParams(0, dp2px(36));
                hotrankButtonsLayoutParams.weight = 1;

                repostButton = new CenteredDrawableButton(context);
                repostButton.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                repostButton.setBackground(createRippleDrawable(Color.WHITE, 0));
                repostButton.setCompoundDrawablePadding(dp2px(2));
                repostButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                repostButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hotrank_repost, 0, 0, 0);
                repostButton.setLayoutParams(hotrankButtonsLayoutParams);

                commentButton = new CenteredDrawableButton(context);
                commentButton.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                commentButton.setCompoundDrawablePadding(dp2px(2));
                commentButton.setBackground(createRippleDrawable(Color.WHITE, 0));
                commentButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                commentButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hotrank_comment, 0, 0, 0);
                commentButton.setLayoutParams(hotrankButtonsLayoutParams);

                likeButton = new CenteredDrawableButton(context);
                likeButton.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                likeButton.setCompoundDrawablePadding(dp2px(2));
                likeButton.setBackground(createRippleDrawable(Color.WHITE, 0));
                likeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hotrank_like, 0, 0, 0);
                likeButton.setLayoutParams(hotrankButtonsLayoutParams);

            hotrankLayout.addView(repostButton);
            hotrankLayout.addView(commentButton);
            hotrankLayout.addView(likeButton);

        statusContentLayout.addView(userInfoLayout);
        statusContentLayout.addView(statusTextTextView);
        statusContentLayout.addView(statusPicturesView);
        statusContentLayout.addView(retweetedStatusView);
        statusContentLayout.addView(hotrankLayout);

        addView(statusContentLayout);

    }
}
