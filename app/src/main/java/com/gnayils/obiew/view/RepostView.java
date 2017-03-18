package com.gnayils.obiew.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;
import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.weibo.WeiboTextDecorator;
import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.bean.Comment;
import com.gnayils.obiew.weibo.bean.Repost;

import static com.gnayils.obiew.util.ViewUtils.dp2px;

/**
 * Created by Gnayils on 12/03/2017.
 */

public class RepostView extends CardView {

    private Repost repost;

    private RelativeLayout rootView;
    private AvatarView userAvatarView;
    private TextView screenNameTextView;
    private TextView statusTimeTextView;
    private TextView commentTextTextView;

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

        commentTextTextView = new TextView(context);
        commentTextTextView.setText("微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发微博转发");
        commentTextTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        RelativeLayout.LayoutParams commentTextTextViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        commentTextTextViewLayoutParams.addRule(RelativeLayout.BELOW, userAvatarView.getId());
        commentTextTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, userAvatarView.getId());
        commentTextTextViewLayoutParams.setMargins(dp2px(context, 8), dp2px(context, 8), 0, 0);
        commentTextTextView.setLayoutParams(commentTextTextViewLayoutParams);

        rootView.addView(userAvatarView);
        rootView.addView(screenNameTextView);
        rootView.addView(statusTimeTextView);
        rootView.addView(commentTextTextView);

        addView(rootView);
    }

    public void show(Repost repost) {
        this.repost = repost;
        BitmapLoader.getInstance().loadBitmap(repost.user.avatar_large, userAvatarView.avatarCircleImageView);
        userAvatarView.verifiedIconImageView.setVisibility(repost.user.verified ? View.VISIBLE : View.INVISIBLE);
        if(repost.user.verified) {
            screenNameTextView.setTextColor(App.resources().getColor(R.color.colorVerifiedScreenName));
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
            screenNameTextView.setTextColor(App.resources().getColor(R.color.colorPrimaryText));
        }
        screenNameTextView.setText(repost.user.screen_name);
        statusTimeTextView.setText(Weibo.Date.format(repost.created_at));
        commentTextTextView.setText(WeiboTextDecorator.decorate(repost.text), TextView.BufferType.SPANNABLE);
    }
}
