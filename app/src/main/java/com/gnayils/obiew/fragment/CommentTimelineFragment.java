package com.gnayils.obiew.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gnayils.obiew.R;
import com.gnayils.obiew.view.CommentTimelineView;
import com.gnayils.obiew.weibo.bean.CommentTimeline;

/**
 * Created by Gnayils on 11/03/2017.
 */

public class CommentTimelineFragment extends Fragment {

    private CommentTimelineView commentTimelineView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        commentTimelineView = (CommentTimelineView) inflater.inflate(R.layout.fragment_comment_timeline, container, false);
        return commentTimelineView;
    }

    public void show(CommentTimeline commentTimeline) {
        commentTimelineView.show(commentTimeline);
    }

    public static CommentTimelineFragment newInstance() {
        CommentTimelineFragment fragment = new CommentTimelineFragment();
        return fragment;
    }
}
