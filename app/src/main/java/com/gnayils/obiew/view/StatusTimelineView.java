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

public class StatusTimelineView extends LoadMoreRecyclerView {

    private StatusTimelineAdapter statusTimelineAdapter;

    public StatusTimelineView(Context context) {
        this(context, null);
    }

    public StatusTimelineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusTimelineView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new LinearLayoutManager(getContext()));
        statusTimelineAdapter = new StatusTimelineAdapter(this);
        setAdapter(statusTimelineAdapter);

    }

    public void show(boolean isLatest, Statuses statuses) {
        statusTimelineAdapter.add(isLatest, statuses);
    }

    static class StatusTimelineAdapter extends LoadMoreRecyclerView.LoadMoreAdapter<StatusViewHolder> implements OnClickListener, StatusPicturesView.OnPictureItemClickListener {

        List<Status> statusList = new ArrayList<Status>();
        RecyclerView recyclerView;

        StatusTimelineAdapter(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public int getItemsCount() {
            return statusList.size();
        }

        @Override
        public StatusViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
            StatusView statusView = new StatusView(parent.getContext());
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(dp2px(statusView.getContext(), 8), dp2px(statusView.getContext(), 4), dp2px(statusView.getContext(), 8), dp2px(statusView.getContext(), 4));
            statusView.setLayoutParams(layoutParams);
            statusView.setRadius(dp2px(statusView.getContext(), 4));
            StatusViewHolder holder = new StatusViewHolder(statusView);
            holder.statusView.rootView.setOnClickListener(this);
            holder.statusView.userAvatarView.avatarCircleImageView.setOnClickListener(this);
            holder.statusView.retweetedStatusView.setOnClickListener(this);
            holder.statusView.statusPicturesView.setOnPictureItemClickListener(this);
            holder.statusView.retweetedStatusPicturesView.setOnPictureItemClickListener(this);
            holder.statusView.videoPreviewView.coverImageView.setOnClickListener(this);
            holder.statusView.retweetedVideoPreviewView.coverImageView.setOnClickListener(this);
            holder.statusView.repostButton.setOnClickListener(this);
            holder.statusView.commentButton.setOnClickListener(this);
            return holder;
        }

        @Override
        public void onBindItemViewHolder(StatusViewHolder holder, int position) {
            final Status status = statusList.get(position);
            holder.statusView.setTag(position);
            holder.statusView.show(status);
        }

        @Override
        public StatusViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
            return new StatusViewHolder(new DefaultFooterView(parent, parent.getContext()));
        }

        @Override
        public void onBindFooterViewHolder(StatusViewHolder holder, int position) {
            holder.defaultFooterView.progressDrawable.stop();
            holder.defaultFooterView.progressDrawable.start();
        }

        public void add(boolean isLatest, Statuses statuses) {
            Set<Status> statusSet = new TreeSet<>();
            if (isLatest) {
                statusList.clear();
                statusSet.addAll(statuses.statuses);
            } else {
                statusSet.addAll(statusList);
                statusSet.addAll(statuses.statuses);
                statusList.clear();
            }
            statusList.addAll(statusSet);
            notifyDataSetChanged();
        }

        @Override
        public void onClick(View v) {
            int position = getPositionFromStatusViewTag(v);
            if(position == -1) {
                return;
            }
            ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder == null || !(viewHolder instanceof StatusViewHolder)) {
                return;
            }
            Status status = statusList.get(position);
            StatusViewHolder holder = (StatusViewHolder) viewHolder;
            if (v == holder.statusView.rootView) {
                if (status.user != null) {
                    StatusDetailActivity.start(v.getContext(), status);
                }
            } else if (v == holder.statusView.userAvatarView.avatarCircleImageView) {
                UserProfileActivity.start(v.getContext(), status.user);
            } else if (v == holder.statusView.retweetedStatusView) {
                if (status.retweeted_status.user != null) {
                    StatusDetailActivity.start(v.getContext(), status.retweeted_status);
                }
            } else if (v == holder.statusView.videoPreviewView.coverImageView) {
                PlayerActivity.start(v.getContext(), status.videoUrls.video);
            } else if (v == holder.statusView.retweetedVideoPreviewView.coverImageView) {
                PlayerActivity.start(v.getContext(), status.retweeted_status.videoUrls.video);
            } else if (v == holder.statusView.repostButton) {
                if (status.user != null) {
                    StatusDetailActivity.start(v.getContext(), status, false, false);
                }
            } else if (v == holder.statusView.commentButton) {
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
            ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder == null || !(viewHolder instanceof StatusViewHolder)) {
                return;
            }
            Status status = statusList.get(position);
            StatusViewHolder holder = (StatusViewHolder) viewHolder;
            if (view == holder.statusView.statusPicturesView) {
                PicturePagerActivity.start(v.getContext(), index, status.pic_urls);
            } else if (view == holder.statusView.retweetedStatusPicturesView) {
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


    static class StatusViewHolder extends RecyclerView.ViewHolder {

        StatusView statusView;
        LoadMoreRecyclerView.DefaultFooterView defaultFooterView;

        StatusViewHolder(StatusView statusView) {
            super(statusView);
            this.statusView = statusView;
        }

        StatusViewHolder(LoadMoreRecyclerView.DefaultFooterView defaultFooterView) {
            super(defaultFooterView);
            this.defaultFooterView = defaultFooterView;
        }
    }
}
