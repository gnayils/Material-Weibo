package com.gnayils.obiew.user;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * Created by Gnayils on 01/02/2017.
 */

public class StatusTimelineAdapter extends RecyclerView.Adapter<StatusTimelineAdapter.StatusCardViewHolder> {

    public static final String TAG = StatusTimelineAdapter.class.getSimpleName();

    private List<Status> statusList = new ArrayList<Status>();
    private DateFormat originalDateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
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
        holder.statusCardView.screenNameTextView.setText(status.user.screen_name);
        try {
            Date createdDate = originalDateFormat.parse(status.created_at);
            holder.statusCardView.statusTimeTextView.setText(destinationDateFormat.format(createdDate));
        } catch (ParseException e) {
            Log.e(TAG, "format status date failed", e);
            holder.statusCardView.statusTimeTextView.setText("");
        }
        if(status.source != null && !status.source.isEmpty()) {
            holder.statusCardView.sourceTextTextView.setVisibility(View.VISIBLE);
            Spannable statusSource = (Spannable) Html.fromHtml(status.source);
            StatusTextDecorator.replaceUrlSpan(statusSource);
            holder.statusCardView.statusSourceTextView.setText(statusSource);
        } else {
            holder.statusCardView.sourceTextTextView.setVisibility(View.INVISIBLE);
        }
        holder.statusCardView.statusTextTextView.setText(StatusTextDecorator.decorate(status.text));
        holder.statusCardView.statusPicturesView.setPictureUrls(status.pic_urls);
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public void addTimeline(Timeline timeline) {
        statusList.addAll(timeline.statuses);
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
