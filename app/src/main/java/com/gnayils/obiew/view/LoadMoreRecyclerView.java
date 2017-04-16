package com.gnayils.obiew.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import static com.gnayils.obiew.util.ViewUtils.dp2px;

/**
 * Created by Gnayils on 08/04/2017.
 */

public class LoadMoreRecyclerView extends RecyclerView {

    private OnLoadMoreListener onLoadMoreListener;

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnScrollListener(new OnScrollListener() {

            int lastVisibleItemPosition = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 == getAdapter().getItemCount()) {
                    if(onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(getLayoutManager() instanceof LinearLayoutManager) {
                    lastVisibleItemPosition = ((LinearLayoutManager)getLayoutManager()).findLastVisibleItemPosition();
                } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    int[] postions = ((StaggeredGridLayoutManager)getLayoutManager()).findLastVisibleItemPositions(null);
                    if(postions != null) {
                        lastVisibleItemPosition = postions[postions.length - 1];
                    }
                }
            }
        });
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(!(adapter instanceof LoadMoreAdapter)) {
            throw new IllegalArgumentException("the adapter must be a instance of LoadMoreAdapter");
        }
        super.setAdapter(adapter);
    }


    public static abstract class LoadMoreAdapter extends Adapter<ViewHolder> {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_FOOTER = 1;

        @Override
        public final int getItemCount() {
            return getActualItemCount() == 0 ? 0 : getActualItemCount() + 1;
        }

        @Override
        public final int getItemViewType(int position) {
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        }

        @Override
        public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder viewHolder = null;
            if(viewType == TYPE_ITEM) {
                viewHolder = onCreateActualViewHolder(parent, viewType);
            } else if (viewType == TYPE_FOOTER) {
                FrameLayout frameLayout = new FrameLayout(parent.getContext());
                LayoutParams frameLayoutLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                frameLayout.setLayoutParams(frameLayoutLayoutParams);

                MaterialCircleImageView loadingIndicatorView = new MaterialCircleImageView(parent.getContext(), 0xFFFAFAFA);
                FrameLayout.LayoutParams loadingIndicatorViewLayoutParams =new FrameLayout.LayoutParams(dp2px(parent.getContext(), 40), dp2px(parent.getContext(), 40));
                int loadingIndicatorViewMargin = dp2px(parent.getContext(), 8);
                loadingIndicatorViewLayoutParams.setMargins(loadingIndicatorViewMargin, loadingIndicatorViewMargin, loadingIndicatorViewMargin, loadingIndicatorViewMargin);
                loadingIndicatorViewLayoutParams.gravity = Gravity.CENTER;
                loadingIndicatorView.setLayoutParams(loadingIndicatorViewLayoutParams);

                MaterialProgressDrawable progressDrawable = new MaterialProgressDrawable(parent.getContext(), parent);
                progressDrawable.setBackgroundColor(0xFFFAFAFA);
                progressDrawable.setAlpha(255);
                progressDrawable.showArrow(true);

                loadingIndicatorView.setImageDrawable(progressDrawable);

                frameLayout.addView(loadingIndicatorView);

                viewHolder = new LoadingMoreIndicatorViewHolder(frameLayout, progressDrawable);
            }
            return viewHolder;
        }

        @Override
        public final void onBindViewHolder(final ViewHolder holder, int position) {
            if(holder instanceof LoadingMoreIndicatorViewHolder) {
                ((LoadingMoreIndicatorViewHolder)holder).progressDrawable.stop();
                ((LoadingMoreIndicatorViewHolder)holder).progressDrawable.start();
            } else {
                onBindActualViewHolder(holder, position);
            }
        }

        public abstract int getActualItemCount() ;

        public abstract ViewHolder onCreateActualViewHolder(ViewGroup parent, int viewType);

        public abstract void onBindActualViewHolder(ViewHolder holder, int position);

    }

    public static class LoadingMoreIndicatorViewHolder extends ViewHolder {

        MaterialProgressDrawable progressDrawable;

        public LoadingMoreIndicatorViewHolder(FrameLayout loadingIndicatorView, MaterialProgressDrawable progressDrawable) {
            super(loadingIndicatorView);
            this.progressDrawable = progressDrawable;
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {

        void onLoadMore();

    }
}
