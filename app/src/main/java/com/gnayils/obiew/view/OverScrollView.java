package com.gnayils.obiew.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.SlidingDrawer;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.ViewHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 8/13/2016.
 */
public class OverScrollView extends LinearLayout {

    private int touchSlop;

    private float lastMotionY;

    private boolean isBeingDragged = false;

    private Scroller scroller;

    private int headerId;
    private View headerView;
    private int contentId;
    private View contentView;

    public OverScrollView(Context context) {
        this(context, null);
    }

    public OverScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OverScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(LinearLayout.VERTICAL);
        scroller = new Scroller(getContext());
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OverScrollView, defStyleAttr, defStyleRes);
            headerId = typedArray.getResourceId(R.styleable.OverScrollView_header, 0);
            if (headerId == 0) {
                throw new IllegalArgumentException("The handle attribute is required and must refer to a valid child.");
            }
            contentId = typedArray.getResourceId(R.styleable.OverScrollView_content, 0);
            if (contentId == 0) {
                throw new IllegalArgumentException("The content attribute is required and must refer to a valid child.");
            }
            if (headerId == contentId) {
                throw new IllegalArgumentException("The content and handle attributes must refer to different children.");
            }
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        headerView = findViewById(headerId);
        if (headerView == null) {
            throw new IllegalArgumentException("The header attribute is must refer to an existing child.");
        }

        contentView = findViewById(contentId);
        if (contentView == null) {
            throw new IllegalArgumentException("The content attribute is must refer to an existing child.");
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(!headerView.isLaidOut() && !contentView.isLaidOut()) {
            headerView.layout(l, t, r, headerView.getMeasuredHeight());
            contentView.layout(l, headerView.getMeasuredHeight(), r, headerView.getMeasuredHeight() + contentView.getMeasuredHeight());
        } else {
            int offset = headerView.getLayoutParams().height - headerView.getHeight();
            headerView.layout(headerView.getLeft(), headerView.getTop() - offset, headerView.getRight(), headerView.getBottom());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastMotionY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(ev.getY() - lastMotionY) > touchSlop) {
                    ViewParent parent = getParent();
                    if(parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    isBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isBeingDragged = false;
                break;
        }
        return isBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isBeingDragged && Math.abs(ev.getY() - lastMotionY) > touchSlop) {
                    ViewParent parent = getParent();
                    if(parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    isBeingDragged = true;
                }
                if(isBeingDragged) {
                    int deltaY = (int) (lastMotionY - ev.getY());
                    if (getScrollY() + deltaY < 0) {
                        if (deltaY < 0) {
                            scrollYBy(deltaY *= 0.3f);
                        } else {
                            scrollYBy(deltaY);
                        }
                    }
                    lastMotionY = ev.getY();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isBeingDragged = false;
                scroller.startScroll(0, getScrollY(), 0, 0 - getScrollY());
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if(scroller.computeScrollOffset()) {
            int deltaY = scroller.getCurrY() - getScrollY();
            scrollYBy(deltaY);
        }
    }


    private void scrollYBy(int deltaY) {
        scrollBy(0, deltaY);
        ViewGroup.LayoutParams layoutParams = headerView.getLayoutParams();
        layoutParams.height += (0 - deltaY);
        headerView.setLayoutParams(layoutParams);
    }
}
