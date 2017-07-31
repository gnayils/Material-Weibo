package com.gnayils.obiew.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.gnayils.obiew.activity.PicturePagerActivity;
import com.gnayils.obiew.activity.PlayerActivity;
import com.gnayils.obiew.activity.StatusDetailActivity;
import com.gnayils.obiew.activity.UserProfileActivity;
import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.Statuses;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.gnayils.obiew.util.ViewUtils.dp2px;

/**
 * Created by Gnayils on 08/04/2017.
 */

public class StatusTimelineView extends LoadMoreRecyclerView<Status, StatusView> implements View.OnClickListener, StatusPicturesView.OnPictureItemClickListener {

    public StatusTimelineView(Context context) {
        this(context, null);
    }

    public StatusTimelineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusTimelineView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public StatusView createView(ViewGroup parent, int viewType) {
        StatusView statusView = new StatusView(parent.getContext());
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dp2px(statusView.getContext(), 8), dp2px(statusView.getContext(), 4), dp2px(statusView.getContext(), 8), dp2px(statusView.getContext(), 4));
        statusView.setLayoutParams(layoutParams);
        statusView.setRadius(dp2px(statusView.getContext(), 4));
        statusView.rootView.setOnClickListener(this);
        statusView.userAvatarView.avatarCircleImageView.setOnClickListener(this);
        statusView.retweetedStatusView.setOnClickListener(this);
        statusView.statusPicturesView.setOnPictureItemClickListener(this);
        statusView.retweetedStatusPicturesView.setOnPictureItemClickListener(this);
        statusView.videoPreviewView.coverImageView.setOnClickListener(this);
        statusView.retweetedVideoPreviewView.coverImageView.setOnClickListener(this);
        statusView.repostButton.setOnClickListener(this);
        statusView.commentButton.setOnClickListener(this);
        return statusView;
    }

    @Override
    public void bindView(StatusView statusView, Status status, int position) {
        statusView.setTag(position);
        statusView.show(status);
    }

    @Override
    public void onClick(View v) {
        int position = getPositionFromStatusViewTag(v);
        if(position == -1) {
            return;
        }
        ViewHolder viewHolder = findViewHolderForAdapterPosition(position);
        if (viewHolder == null ) {
            return;
        }
        Status status = getAdapterDataSet().get(position);
        if (v == ((StatusView)viewHolder.itemView).rootView) {
            if (status.user != null) {
                StatusDetailActivity.start(v.getContext(), status);
            }
        } else if (v == ((StatusView)viewHolder.itemView).userAvatarView.avatarCircleImageView) {
            UserProfileActivity.start(v.getContext(), status.user);
        } else if (v == ((StatusView)viewHolder.itemView).retweetedStatusView) {
            if (status.retweeted_status.user != null) {
                StatusDetailActivity.start(v.getContext(), status.retweeted_status);
            }
        } else if (v == ((StatusView)viewHolder.itemView).videoPreviewView.coverImageView) {
            PlayerActivity.start(v.getContext(), status.videoUrls.video);
        } else if (v == ((StatusView)viewHolder.itemView).retweetedVideoPreviewView.coverImageView) {
            PlayerActivity.start(v.getContext(), status.retweeted_status.videoUrls.video);
        } else if (v == ((StatusView)viewHolder.itemView).repostButton) {
            if (status.user != null) {
                StatusDetailActivity.start(v.getContext(), status, false, false);
            }
        } else if (v == ((StatusView)viewHolder.itemView).commentButton) {
            if (status.user != null) {
                StatusDetailActivity.start(v.getContext(), status, false, true);
            }
        }
    }

    @Override
    public void onPictureItemClick(StatusPicturesView view, View v, int index) {
        int position = getPositionFromStatusViewTag(v);
        if(position == -1) {
            return;
        }
        ViewHolder viewHolder = findViewHolderForAdapterPosition(position);
        if (viewHolder == null ) {
            return;
        }
        Status status = getAdapterDataSet().get(position);
        if (view == ((StatusView)viewHolder.itemView).statusPicturesView) {
            PicturePagerActivity.start(v.getContext(), index, status.pic_urls);
        } else if (view == ((StatusView)viewHolder.itemView).retweetedStatusPicturesView) {
            PicturePagerActivity.start(v.getContext(), index, status.retweeted_status.pic_urls);
        }
    }

    private int getPositionFromStatusViewTag(View v) {
        if(v == null) {
            return -1;
        }
        StatusView statusView = null;
        View parent = v;
        while (parent.getParent() != null && parent.getParent() instanceof View) {
            parent = (View) parent.getParent();
            if(parent instanceof StatusView) {
                statusView = (StatusView) parent;
                break;
            } else {
                continue;
            }
        }
        if(statusView == null) {
            return -1;
        } else if(statusView.getTag() instanceof Integer) {
            return (int) statusView.getTag();
        } else {
            return -1;
        }
    }
}
