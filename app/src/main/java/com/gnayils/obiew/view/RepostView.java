package com.gnayils.obiew.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gnayils.obiew.Obiew;
import com.gnayils.obiew.R;
import com.gnayils.obiew.activity.UserProfileActivity;
import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.WeiboSpanMovementMethod;
import com.gnayils.obiew.weibo.bean.Repost;

import static com.gnayils.obiew.util.ViewUtils.dp2px;
import static com.gnayils.obiew.util.ViewUtils.getDrawableByAttrId;

/**
 * Created by Gnayils on 12/03/2017.
 */

public class RepostView extends CardView {

    private Repost repost;

    private RelativeLayout rootView;
    private AvatarView userAvatarView;
    private TextView screenNameTextView;
    private TextView repostTimeTextView;
    private TextView repostTextTextView;

    public OnClickListener avatarCircleImageViewOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            UserProfileActivity.start(getContext(), repost.user);
        }
    };

    public RepostView(Context context) {
        this(context, null);
    }

    public RepostView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RepostView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        rootView = new RelativeLayout(context);
        rootView.setPadding(dp2px(context, 8), dp2px(context, 8), dp2px(context, 8), dp2px(context, 8));
        LinearLayout.LayoutParams userInfoLayoutLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(userInfoLayoutLayoutParams);
        rootView.setClickable(true);
        rootView.setBackground(getDrawableByAttrId(context, R.attr.selectableItemBackground));

        userAvatarView = new AvatarView(context);
        userAvatarView.setId(View.generateViewId());
        RelativeLayout.LayoutParams avatarViewLayoutParams = new RelativeLayout.LayoutParams(dp2px(context, 48), dp2px(context, 48));
        avatarViewLayoutParams.addRule(RelativeLayout.ALIGN_LEFT | RelativeLayout.ALIGN_TOP);
        userAvatarView.setLayoutParams(avatarViewLayoutParams);
        userAvatarView.avatarCircleImageView.setImageResource(R.drawable.ic_avatar);
        userAvatarView.avatarCircleImageView.setOnClickListener(avatarCircleImageViewOnClickListener);

        screenNameTextView = new TextView(context);
        screenNameTextView.setText("用户名");
        screenNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        RelativeLayout.LayoutParams userNameTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        userNameTextViewLayoutParams.setMargins(dp2px(context, 8), dp2px(context, 4), 0, 0);
        userNameTextViewLayoutParams.addRule(RelativeLayout.ALIGN_TOP, userAvatarView.getId());
        userNameTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, userAvatarView.getId());
        screenNameTextView.setLayoutParams(userNameTextViewLayoutParams);

        repostTimeTextView = new TextView(context);
        repostTimeTextView.setText("15分钟前");
        repostTimeTextView.setId(View.generateViewId());
        repostTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        repostTimeTextView.setTextColor(getResources().getColor(R.color.black_alpha_80));
        RelativeLayout.LayoutParams statusTimeTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        statusTimeTextViewLayoutParams.setMargins(dp2px(context, 8), 0, 0, dp2px(context, 4));
        statusTimeTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, userAvatarView.getId());
        statusTimeTextViewLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, userAvatarView.getId());
        repostTimeTextView.setLayoutParams(statusTimeTextViewLayoutParams);

        repostTextTextView = new TextView(context);
        repostTextTextView.setText("微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发");
        repostTextTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.repost_text_size));
        repostTextTextView.setOnTouchListener(WeiboSpanMovementMethod.getTouchListener());
        RelativeLayout.LayoutParams commentTextTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        commentTextTextViewLayoutParams.addRule(RelativeLayout.BELOW, userAvatarView.getId());
        commentTextTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, userAvatarView.getId());
        commentTextTextViewLayoutParams.setMargins(dp2px(context, 8), dp2px(context, 8), 0, 0);
        repostTextTextView.setLayoutParams(commentTextTextViewLayoutParams);

        rootView.addView(userAvatarView);
        rootView.addView(screenNameTextView);
        rootView.addView(repostTimeTextView);
        rootView.addView(repostTextTextView);

        addView(rootView);
    }

    public void show(Repost repost) {
        this.repost = repost;
        Glide.with(getContext()).load(repost.user.avatar_large).into(userAvatarView.avatarCircleImageView);
        userAvatarView.verifiedIconImageView.setVisibility(repost.user.verified ? View.VISIBLE : View.INVISIBLE);
        if(repost.user.verified) {
            screenNameTextView.setTextColor(Obiew.getAppResources().getColor(R.color.colorAccent));
            switch(repost.user.verified_type) {
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
            screenNameTextView.setTextColor(Obiew.getAppResources().getColor(R.color.black_alpha_CC));
        }
        screenNameTextView.setText(repost.user.screen_name);
        repostTimeTextView.setText(Weibo.format.date(repost.created_at));
        repostTextTextView.setText(repost.getSpannableText(), TextView.BufferType.SPANNABLE);
    }
}
