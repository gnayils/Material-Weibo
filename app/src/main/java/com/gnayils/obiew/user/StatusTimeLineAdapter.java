package com.gnayils.obiew.user;

import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.StateSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;
import com.gnayils.obiew.bean.Status;
import com.gnayils.obiew.bean.Timeline;
import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.view.StatusCardView;
import com.gnayils.obiew.weibo.StatusTextDecorator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gnayils on 01/02/2017.
 */

public class StatusTimelineAdapter extends RecyclerView.Adapter<StatusTimelineAdapter.StatusCardViewHolder> {

    public static final String TAG = StatusTimelineAdapter.class.getSimpleName();

    private List<Status> statusList = new ArrayList<Status>();
    private DateFormat destinationDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.ENGLISH);

    public StatusTimelineAdapter() {

    }

    @Override
    public StatusTimelineAdapter.StatusCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StatusCardViewHolder holder = new StatusCardViewHolder(new StatusCardView(parent.getContext()));
        return holder;
    }

    @Override
    public void onBindViewHolder(StatusTimelineAdapter.StatusCardViewHolder holder, int position) {
        Status status = statusList.get(position);
        BitmapLoader.getInstance().loadBitmap(status.user.avatar_large, holder.statusCardView.userAvatarView.avatarCircleImageView);
        holder.statusCardView.userAvatarView.verifiedIconImageView.setVisibility(status.user.verified ? View.VISIBLE : View.INVISIBLE);
        if(status.user.verified) {
            holder.statusCardView.screenNameTextView.setTextColor(App.resources().getColor(R.color.colorVerifiedScreenName));
            switch(status.user.verified_type) {
                case 0:
                    holder.statusCardView.userAvatarView.verifiedIconImageView.setImageResource(R.drawable.avatar_vip_golden);
                    break;
                case 1:
                    holder.statusCardView.userAvatarView.verifiedIconImageView.setImageResource(R.drawable.avatar_vip);
                    break;
                case 2:
                    holder.statusCardView.userAvatarView.verifiedIconImageView.setImageResource(R.drawable.avatar_enterprise_vip);
                    break;
            }
        } else {
            holder.statusCardView.screenNameTextView.setTextColor(App.resources().getColor(R.color.colorPrimaryText));
        }
        holder.statusCardView.screenNameTextView.setText(status.user.screen_name);
        try {
            Date createdDate = Status.DATE_FORMAT.parse(status.created_at);
            holder.statusCardView.statusTimeTextView.setText(destinationDateFormat.format(createdDate));
        } catch (ParseException e) {
            Log.e(TAG, "format status date failed", e);
            holder.statusCardView.statusTimeTextView.setText("");
        }
        Spannable statusSource = (Spannable) Html.fromHtml(status.source);
        StatusTextDecorator.replaceUrlSpan(statusSource);
        holder.statusCardView.statusSourceTextView.setText(statusSource);
        holder.statusCardView.statusTextTextView.setText(StatusTextDecorator.decorate(status.text), TextView.BufferType.SPANNABLE);
        holder.statusCardView.statusPicturesView.setPictureUrls(status.pic_urls);
        if(status.retweeted_status == null) {
            holder.statusCardView.retweetedStatusView.setVisibility(View.GONE);
        } else {
            holder.statusCardView.retweetedStatusView.setVisibility(View.VISIBLE);
            holder.statusCardView.retweetedStatusTextTextView.setText(StatusTextDecorator.decorate("@" + status.retweeted_status.user.screen_name +  ": " + status.retweeted_status.text), TextView.BufferType.SPANNABLE);
            holder.statusCardView.retweetedStatusPicturesView.setPictureUrls(status.retweeted_status.pic_urls);
        }
        holder.statusCardView.repostButton.setText(String.valueOf(status.reposts_count));
        holder.statusCardView.commentButton.setText(String.valueOf(status.comments_count));
        holder.statusCardView.likeButton.setText(String.valueOf(status.attitudes_count));
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public void addTimeline(Timeline timeline) {
        Set<Status> statusSet = new TreeSet<Status>(statusList);
        statusSet.addAll(timeline.statuses);
        statusList.clear();
        statusList.addAll(statusSet);
        notifyDataSetChanged();
    }

    class StatusCardViewHolder extends RecyclerView.ViewHolder {

        public StatusCardView statusCardView;

        public StatusCardViewHolder(StatusCardView statusCardView) {
            super(statusCardView);
            this.statusCardView = statusCardView;
        }
    }

}
