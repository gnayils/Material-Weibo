package com.gnayils.obiew.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.gnayils.obiew.util.ViewUtils.dp2px;

/**
 * Created by Gnayils on 08/04/2017.
 */

public abstract class LoadMoreRecyclerView<B extends Comparable, V extends View> extends RecyclerView {

    private LoadMoreAdapter<B> loadMoreAdapter;
    private OnLoadMoreListener onLoadMoreListener;

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        loadMoreAdapter = new LoadMoreAdapter<B>() {

            @Override
            public LoadMoreViewHolder createItemViewHolder(ViewGroup parent, int viewType) {
                return new LoadMoreViewHolder(createView(parent, viewType));
            }

            @Override
            public void onBindViewHolder(LoadMoreViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                if(getItemViewType(position) == TYPE_FOOTER) {
                    if(hasMoreData && onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                }
            }

            @Override
            public void bindItemViewHolder(LoadMoreViewHolder holder, B bean, int position) {
                bindView(((V)holder.itemView), bean, position);
            }
        };
        setAdapter(loadMoreAdapter);
    }

    @Override
    public void getDecoratedBoundsWithMargins(View view, Rect outBounds) {
        if(getChildAdapterPosition(view) >= getAdapter().getItemCount() - 2) {
            outBounds.set(0, 0, 0, 0);
        } else {
            super.getDecoratedBoundsWithMargins(view, outBounds);
        }
    }

    public abstract V createView(ViewGroup parent, int viewType);

    public abstract void bindView(V view, B bean, int position);

    public List<B> getAdapterDataSet() {
        return ((LoadMoreAdapter)getAdapter()).dataSet;
    }

    public void appendData(boolean isLatest, List<B> data) {
        ((LoadMoreAdapter)getAdapter()).appendData(isLatest, data);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {

        void onLoadMore();

    }

    private static abstract class LoadMoreAdapter<T> extends Adapter<LoadMoreViewHolder> {

        static final int TYPE_ITEM = 0;
        static final int TYPE_FOOTER = 1;

        final List<T> dataSet = new ArrayList<>();
        boolean hasMoreData;

        @Override
        public final int getItemCount() {
            return dataSet.size() == 0 ? 0 : dataSet.size() + 1;
        }

        @Override
        public final int getItemViewType(int position) {
            if(position < dataSet.size()) {
                return TYPE_ITEM;
            } else {
                return TYPE_FOOTER;
            }
        }

        @Override
        public final LoadMoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LoadMoreViewHolder viewHolder = null;
            if(viewType == TYPE_ITEM) {
                return createItemViewHolder(parent, viewType);
            } else if (viewType == TYPE_FOOTER) {
                viewHolder = new LoadMoreViewHolder(new DefaultFooterView(parent, parent.getContext()));
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final LoadMoreViewHolder holder, int position) {
            if(getItemViewType(position) == TYPE_ITEM){
                bindItemViewHolder(holder, dataSet.get(position), position);
            } else if(getItemViewType(position) == TYPE_FOOTER) {
                if(hasMoreData) {
                    ((DefaultFooterView) holder.itemView).showLoadingView();
                } else {
                    ((DefaultFooterView) holder.itemView).showNoMoreDataView();
                }
            }
        }

        public void appendData(boolean isLatest, List<T> data) {
            if(data == null || data.size() == 0 ) {
                hasMoreData = false;
            } else {
                hasMoreData = true;
                Set<T> tempSet = new TreeSet<>();
                if (isLatest) {
                    dataSet.clear();
                    tempSet.addAll(data);
                } else {
                    tempSet.addAll(dataSet);
                    tempSet.addAll(data);
                    dataSet.clear();
                }
                dataSet.addAll(tempSet);
            }
            notifyDataSetChanged();
        }

        public abstract LoadMoreViewHolder createItemViewHolder(ViewGroup parent, int viewType);

        public abstract void bindItemViewHolder(LoadMoreViewHolder holder, T bean, int position);

    }

    private static class LoadMoreViewHolder extends ViewHolder {

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class DefaultFooterView extends FrameLayout {

        private MaterialCircleImageView loadingIndicatorView;
        private MaterialProgressDrawable progressDrawable;
        private TextView noMoreDataTextView;

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

            loadingIndicatorView = new MaterialCircleImageView(context, ViewUtils.getColorByAttrId(context, R.attr.themeColorViewBackground));
            FrameLayout.LayoutParams loadingIndicatorViewLayoutParams =new FrameLayout.LayoutParams(dp2px(context, 40), dp2px(context, 40));
            int loadingIndicatorViewMargin = dp2px(context, 8);
            loadingIndicatorViewLayoutParams.setMargins(loadingIndicatorViewMargin, loadingIndicatorViewMargin, loadingIndicatorViewMargin, loadingIndicatorViewMargin);
            loadingIndicatorViewLayoutParams.gravity = Gravity.CENTER;
            loadingIndicatorView.setLayoutParams(loadingIndicatorViewLayoutParams);

            progressDrawable = new MaterialProgressDrawable(context, parent);
            progressDrawable.setBackgroundColor(ViewUtils.getColorByAttrId(context, R.attr.themeColorViewBackground));
            progressDrawable.setColorSchemeColors(ViewUtils.getColorByAttrId(context, R.attr.themeColorSecondaryText));
            progressDrawable.setAlpha(255);
            progressDrawable.showArrow(true);
            loadingIndicatorView.setImageDrawable(progressDrawable);

            addView(loadingIndicatorView);

            noMoreDataTextView = new TextView(context);
            noMoreDataTextView.setText("已加载全部数据");
            noMoreDataTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            noMoreDataTextView.setTextColor(ViewUtils.getColorByAttrId(context, R.attr.themeColorSecondaryText));
            FrameLayout.LayoutParams textViewLayoutParams =new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textViewLayoutParams.setMargins(loadingIndicatorViewMargin, loadingIndicatorViewMargin, loadingIndicatorViewMargin, loadingIndicatorViewMargin);
            textViewLayoutParams.gravity = Gravity.CENTER;
            noMoreDataTextView.setLayoutParams(textViewLayoutParams);

            addView(noMoreDataTextView);
        }

        public void showLoadingView() {
            noMoreDataTextView.setVisibility(View.INVISIBLE);
            loadingIndicatorView.setVisibility(View.VISIBLE);
            progressDrawable.stop();
            progressDrawable.start();
        }

        public void showNoMoreDataView() {
            loadingIndicatorView.setVisibility(View.INVISIBLE);
            noMoreDataTextView.setVisibility(View.VISIBLE);
        }
    }
}
