package com.gnayils.obiew.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spannable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gnayils.obiew.App;
import com.gnayils.obiew.R;
import com.gnayils.obiew.activity.StatusDetailActivity;
import com.gnayils.obiew.activity.UserProfileActivity;
import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.TextDecorator;
import com.gnayils.obiew.weibo.TouchableLinkMovementMethod;

import static com.gnayils.obiew.util.ViewUtils.*;
/**
 * Created by Gnayils on 30/01/2017.
 */

public class StatusCardView extends CardView {


    public static final String TAG = StatusCardView.class.getSimpleName();

    public Status status;

    public LinearLayout rootView;
    public AvatarView userAvatarView;
    public TextView screenNameTextView;
    public TextView statusTimeTextView;
    public TextView statusSourceTextView;
    public TextView statusTextTextView;
    public StatusPicturesView statusPicturesView;

    public LinearLayout retweetedStatusView;
    public TextView retweetedStatusTextTextView;
    public StatusPicturesView retweetedStatusPicturesView;

    public LinearLayout commentLayout;
    public CenteredDrawableButton repostButton;
    public CenteredDrawableButton commentButton;
    public CenteredDrawableButton likeButton;

    public OnClickListener statusViewOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Status whichStatus = null;
            if(v.getId() == rootView.getId()) {
                whichStatus = status;
            } else if(v.getId() == retweetedStatusView.getId()) {
                whichStatus = status.retweeted_status;
            }
            StatusDetailActivity.start(getContext(), whichStatus);
        }
    };

    public OnClickListener avatarCircleImageViewOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            UserProfileActivity.start(getContext(), status.user);
        }
    };


    public StatusCardView(Context context) {
        this(context, null);
    }

    public StatusCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        rootView = new LinearLayout(context);
        rootView.setId(View.generateViewId());
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT));
        rootView.setOnClickListener(statusViewOnClickListener);
        rootView.setBackground(getDrawableByAttribute(context, R.attr.selectableItemBackground));

            RelativeLayout userInfoLayout = new RelativeLayout(context);
            userInfoLayout.setPadding(dp2px(context, 8), dp2px(context, 8), dp2px(context, 8), dp2px(context, 4));
            LinearLayout.LayoutParams userInfoLayoutLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            userInfoLayout.setLayoutParams(userInfoLayoutLayoutParams);

                userAvatarView = new AvatarView(context);
                userAvatarView.setId(View.generateViewId());
                RelativeLayout.LayoutParams avatarViewLayoutParams = new RelativeLayout.LayoutParams(dp2px(context, 48), dp2px(context, 48));
                avatarViewLayoutParams.addRule(RelativeLayout.ALIGN_LEFT | RelativeLayout.ALIGN_TOP);
                userAvatarView.setLayoutParams(avatarViewLayoutParams);
                userAvatarView.avatarCircleImageView.setForegroundResource(R.drawable.fg_avatar_mask);
                userAvatarView.avatarCircleImageView.setOnClickListener(avatarCircleImageViewOnClickListener);

                screenNameTextView = new TextView(context);
                screenNameTextView.setText("用户名");
                screenNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                RelativeLayout.LayoutParams userNameTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                userNameTextViewLayoutParams.setMargins(dp2px(context, 8), dp2px(context, 4), 0, 0);
                userNameTextViewLayoutParams.addRule(RelativeLayout.ALIGN_TOP, userAvatarView.getId());
                userNameTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, userAvatarView.getId());
                screenNameTextView.setLayoutParams(userNameTextViewLayoutParams);

                statusTimeTextView = new TextView(context);
                statusTimeTextView.setText("15分钟前");
                statusTimeTextView.setId(View.generateViewId());
                statusTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                statusTimeTextView.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                RelativeLayout.LayoutParams statusTimeTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                statusTimeTextViewLayoutParams.setMargins(dp2px(context, 8), 0, 0, dp2px(context, 4));
                statusTimeTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, userAvatarView.getId());
                statusTimeTextViewLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, userAvatarView.getId());
                statusTimeTextView.setLayoutParams(statusTimeTextViewLayoutParams);

                statusSourceTextView = new TextView(context);
                statusSourceTextView.setText("微博 weibo.com");
                statusSourceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                statusSourceTextView.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                RelativeLayout.LayoutParams sourceTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                sourceTextViewLayoutParams.setMargins(dp2px(context, 8), 0, 0, 0);
                sourceTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, statusTimeTextView.getId());
                sourceTextViewLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, statusTimeTextView.getId());
                statusSourceTextView.setLayoutParams(sourceTextViewLayoutParams);

            userInfoLayout.addView(userAvatarView);
            userInfoLayout.addView(screenNameTextView);
            userInfoLayout.addView(statusTimeTextView);
            userInfoLayout.addView(statusSourceTextView);

            statusTextTextView = new TextView(context);
            statusTextTextView.setId(View.generateViewId());
            statusTextTextView.setText("微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容");
            statusTextTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            statusTextTextView.setOnTouchListener(TouchableLinkMovementMethod.getTouchListener());
            statusTextTextView.setPadding(dp2px(context, 8), dp2px(context, 4), dp2px(context, 8), dp2px(context, 4));
            LinearLayout.LayoutParams statusTextTextViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            statusTextTextView.setLayoutParams(statusTextTextViewLayoutParams);

            statusPicturesView = new StatusPicturesView(context);
            statusPicturesView.setPadding(dp2px(context, 8), dp2px(context, 4), dp2px(context, 8), dp2px(context, 4));
            LinearLayout.LayoutParams statusPicturesViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            statusPicturesView.setLayoutParams(statusPicturesViewLayoutParams);

            retweetedStatusView = new LinearLayout(context);
            retweetedStatusView.setId(View.generateViewId());
            retweetedStatusView.setOnClickListener(statusViewOnClickListener);
            retweetedStatusView.setOrientation(LinearLayout.VERTICAL);
            retweetedStatusView.setBackground(createRippleDrawable(getResources().getColor(R.color.colorRetweetStatusViewBg), 0));

            LinearLayout.LayoutParams retweetedStatusViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            retweetedStatusView.setLayoutParams(retweetedStatusViewLayoutParams);
            retweetedStatusTextTextView = new TextView(context);
            retweetedStatusTextTextView.setText("微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容微博内容");
            retweetedStatusTextTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            retweetedStatusTextTextView.setOnTouchListener(TouchableLinkMovementMethod.getTouchListener());
            retweetedStatusTextTextView.setPadding(dp2px(context, 8), dp2px(context, 4), dp2px(context, 8), dp2px(context, 4));
            LinearLayout.LayoutParams retweetedStatusTextTextViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            retweetedStatusTextTextView.setLayoutParams(retweetedStatusTextTextViewLayoutParams);
            retweetedStatusPicturesView = new StatusPicturesView(context);
            retweetedStatusPicturesView.setPadding(dp2px(context, 8), dp2px(context, 4), dp2px(context, 8), dp2px(context, 4));
            LinearLayout.LayoutParams retweetedStatusPicturesViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            retweetedStatusPicturesView.setLayoutParams(retweetedStatusPicturesViewLayoutParams);
            retweetedStatusView.addView(retweetedStatusTextTextView);
            retweetedStatusView.addView(retweetedStatusPicturesView);

            commentLayout = new LinearLayout(context);
            commentLayout.setOrientation(LinearLayout.HORIZONTAL);
            commentLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                LinearLayout.LayoutParams hotrankButtonsLayoutParams = new LinearLayout.LayoutParams(0, dp2px(context, 36));
                hotrankButtonsLayoutParams.weight = 1;

                repostButton = new CenteredDrawableButton(context);
                repostButton.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                repostButton.setBackground(getDrawableByAttribute(context, R.attr.selectableItemBackground));
                repostButton.setCompoundDrawablePadding(dp2px(context, 2));
                repostButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                repostButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hotrank_repost, 0, 0, 0);
                repostButton.setLayoutParams(hotrankButtonsLayoutParams);

                commentButton = new CenteredDrawableButton(context);
                commentButton.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                commentButton.setCompoundDrawablePadding(dp2px(context, 2));
                commentButton.setBackground(getDrawableByAttribute(context, R.attr.selectableItemBackground));
                commentButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                commentButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hotrank_comment, 0, 0, 0);
                commentButton.setLayoutParams(hotrankButtonsLayoutParams);

                likeButton = new CenteredDrawableButton(context);
                likeButton.setTextColor(getResources().getColor(R.color.colorSecondaryText));
                likeButton.setCompoundDrawablePadding(dp2px(context, 2));
                likeButton.setBackground(getDrawableByAttribute(context, R.attr.selectableItemBackground));
                likeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hotrank_like, 0, 0, 0);
                likeButton.setLayoutParams(hotrankButtonsLayoutParams);

            commentLayout.addView(repostButton);
            commentLayout.addView(commentButton);
            commentLayout.addView(likeButton);

        rootView.addView(userInfoLayout);
        rootView.addView(statusTextTextView);
        rootView.addView(statusPicturesView);
        rootView.addView(retweetedStatusView);
        rootView.addView(commentLayout);

        addView(rootView);
    }

    public void show(Status status) {
        this.status = status;
        Glide.with(getContext()).load(status.user.avatar_large).into(userAvatarView.avatarCircleImageView);
        userAvatarView.verifiedIconImageView.setVisibility(status.user.verified ? View.VISIBLE : View.INVISIBLE);
        if(status.user.verified) {
            screenNameTextView.setTextColor(App.resources().getColor(R.color.colorVerifiedScreenName));
            switch(status.user.verified_type) {
                case 0:
                    userAvatarView.verifiedIconImageView.setImageResource(R.drawable.avatar_vip_golden);
                    break;
                case 1:
                    userAvatarView.verifiedIconImageView.setImageResource(R.drawable.avatar_vip);
                    break;
                case 2:
                    userAvatarView.verifiedIconImageView.setImageResource(R.drawable.avatar_enterprise_vip);
                    break;
            }
        } else {
            screenNameTextView.setTextColor(App.resources().getColor(R.color.colorPrimaryText));
        }
        screenNameTextView.setText(status.user.screen_name);
        statusTimeTextView.setText(Weibo.Date.format(status.created_at));
        statusSourceTextView.setText(status.getSpannableSource());
        statusTextTextView.setText(status.getSpannableText(), TextView.BufferType.SPANNABLE);
        statusPicturesView.setPictureUrls(status.pic_urls);
        if(status.retweeted_status == null) {
            retweetedStatusView.setVisibility(View.GONE);
        } else {
            retweetedStatusView.setVisibility(View.VISIBLE);
            retweetedStatusTextTextView.setText(status.retweeted_status.getSpannableText(), TextView.BufferType.SPANNABLE);
            retweetedStatusPicturesView.setPictureUrls(status.retweeted_status.pic_urls);
        }
        repostButton.setText(String.valueOf(status.reposts_count));
        commentButton.setText(String.valueOf(status.comments_count));
        likeButton.setText(String.valueOf(status.attitudes_count));
    }
}
