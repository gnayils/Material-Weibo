package com.gnayils.obiew.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
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
                    int[] positions = ((StaggeredGridLayoutManager)getLayoutManager()).findLastVisibleItemPositions(null);
                    if(positions != null) {
                        lastVisibleItemPosition = positions[positions.length - 1];
                    }
                } else {
                    throw new UnsupportedOperationException("only support two layout manager: [LinearLayoutManager, StaggeredGridLayoutManager]");
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


    public static abstract class LoadMoreAdapter<VH extends ViewHolder> extends Adapter<VH> {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_FOOTER = 1;

        @Override
        public final int getItemCount() {
            return getItemsCount() == 0 ? 0 : getItemsCount() + 1;
        }

        @Override
        public final int getItemViewType(int position) {
            if(position < getItemsCount()) {
                return TYPE_ITEM;
            } else {
                return TYPE_FOOTER;
            }
        }

        @Override
        public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH viewHolder = null;
            if(viewType == TYPE_ITEM) {
                viewHolder = onCreateItemViewHolder(parent, viewType);
            } else if (viewType == TYPE_FOOTER) {
                viewHolder = onCreateFooterViewHolder(parent, viewType);
            }
            return viewHolder;
        }

        @Override
        public final void onBindViewHolder(final VH holder, int position) {
            if(getItemViewType(position) == TYPE_ITEM){
                onBindItemViewHolder(holder, position);
            } else if(getItemViewType(position) == TYPE_FOOTER) {
                onBindFooterViewHolder(holder, position);
            }
        }

        public abstract int getItemsCount() ;

        public abstract VH onCreateItemViewHolder(ViewGroup parent, int viewType);

        public abstract void onBindItemViewHolder(VH holder, int position);

        public abstract VH onCreateFooterViewHolder(ViewGroup parent, int viewType);

        public abstract void onBindFooterViewHolder(VH holder, int position);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {

        void onLoadMore();

    }

    public static class DefaultFooterView extends FrameLayout {

        public final MaterialProgressDrawable progressDrawable;

        public DefaultFooterView(View parent, @NonNull Context context) {
            this(parent, context, null);
        }

        public DefaultFooterView(View parent, @NonNull Context context, @Nullable AttributeSet attrs) {
            this(parent, context, attrs, 0);
        }

        public DefaultFooterView(View parent, @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
            this(parent, context, attrs, defStyleAttr, 0);
        }

        public DefaultFooterView(View parent, @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);

            RecyclerView.LayoutParams frameLayoutLayoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            setLayoutParams(frameLayoutLayoutParams);

            MaterialCircleImageView loadingIndicatorView = new MaterialCircleImageView(context, 0xFFFAFAFA);
            FrameLayout.LayoutParams loadingIndicatorViewLayoutParams =new FrameLayout.LayoutParams(dp2px(context, 40), dp2px(context, 40));
            int loadingIndicatorViewMargin = dp2px(context, 8);
            loadingIndicatorViewLayoutParams.setMargins(loadingIndicatorViewMargin, loadingIndicatorViewMargin, loadingIndicatorViewMargin, loadingIndicatorViewMargin);
            loadingIndicatorViewLayoutParams.gravity = Gravity.CENTER;
            loadingIndicatorView.setLayoutParams(loadingIndicatorViewLayoutParams);

            progressDrawable = new MaterialProgressDrawable(context, parent);
            progressDrawable.setBackgroundColor(0xFFFAFAFA);
            progressDrawable.setAlpha(255);
            progressDrawable.showArrow(true);

            loadingIndicatorView.setImageDrawable(progressDrawable);

            addView(loadingIndicatorView);
        }
    }
}
