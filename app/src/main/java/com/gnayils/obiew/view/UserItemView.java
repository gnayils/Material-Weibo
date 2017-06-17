package com.gnayils.obiew.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gnayils.obiew.Obiew;
import com.gnayils.obiew.R;
import com.gnayils.obiew.weibo.bean.User;

import static com.gnayils.obiew.util.ViewUtils.dp2px;
import static com.gnayils.obiew.util.ViewUtils.getDrawableByAttrId;

/**
 * Created by Gnayils on 12/03/2017.
 */

public class UserItemView extends CardView {

    public User user;

    public RelativeLayout rootView;
    public AvatarView userAvatarView;
    public TextView screenNameTextView;
    public TextView descriptionTextView;

    public UserItemView(Context context) {
        this(context, null);
    }

    public UserItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        rootView = new RelativeLayout(context);
        rootView.setPadding(dp2px(context, 8), dp2px(context, 8), dp2px(context, 8), dp2px(context, 8));
        LinearLayout.LayoutParams userInfoLayoutLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(userInfoLayoutLayoutParams);
        rootView.setBackground(getDrawableByAttrId(context, R.attr.selectableItemBackground));

        userAvatarView = new AvatarView(context);
        userAvatarView.setId(View.generateViewId());
        RelativeLayout.LayoutParams avatarViewLayoutParams = new RelativeLayout.LayoutParams(dp2px(context, 48), dp2px(context, 48));
        avatarViewLayoutParams.addRule(RelativeLayout.ALIGN_LEFT | RelativeLayout.ALIGN_TOP);
        userAvatarView.setLayoutParams(avatarViewLayoutParams);

        screenNameTextView = new TextView(context);
        screenNameTextView.setText("用户名");
        screenNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        RelativeLayout.LayoutParams userNameTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        userNameTextViewLayoutParams.setMargins(dp2px(context, 8), dp2px(context, 4), 0, 0);
        userNameTextViewLayoutParams.addRule(RelativeLayout.ALIGN_TOP, userAvatarView.getId());
        userNameTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, userAvatarView.getId());
        screenNameTextView.setLayoutParams(userNameTextViewLayoutParams);

        descriptionTextView = new TextView(context);
        descriptionTextView.setText("简介");
        descriptionTextView.setId(View.generateViewId());
        descriptionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        descriptionTextView.setTextColor(getResources().getColor(R.color.black_alpha_80));
        descriptionTextView.setSingleLine();
        descriptionTextView.setEllipsize(TextUtils.TruncateAt.END);
        RelativeLayout.LayoutParams statusTimeTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        statusTimeTextViewLayoutParams.setMargins(dp2px(context, 8), 0, 0, dp2px(context, 4));
        statusTimeTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, userAvatarView.getId());
        statusTimeTextViewLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, userAvatarView.getId());
        descriptionTextView.setLayoutParams(statusTimeTextViewLayoutParams);

        rootView.addView(userAvatarView);
        rootView.addView(screenNameTextView);
        rootView.addView(descriptionTextView);

        addView(rootView);
    }

    public void show(User user) {
        this.user = user;
        Glide.with(getContext()).load(user.avatar_large).into(userAvatarView.avatarCircleImageView);
        userAvatarView.verifiedIconImageView.setVisibility(user.verified ? View.VISIBLE : View.INVISIBLE);
        if(user.verified) {
            screenNameTextView.setTextColor(Obiew.getAppResources().getColor(R.color.colorAccent));
            switch(user.verified_type) {
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
        screenNameTextView.setText(user.screen_name);
        descriptionTextView.setText(user.description);
    }
}
