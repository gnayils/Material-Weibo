package com.gnayils.obiew.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gnayils.obiew.R;
import com.gnayils.obiew.view.RepostTimelineView;
import com.gnayils.obiew.weibo.bean.CommentTimeline;
import com.gnayils.obiew.weibo.bean.RepostTimeline;
import com.gnayils.obiew.weibo.bean.Status;

/**
 * Created by Gnayils on 11/03/2017.
 */

public class RepostTimelineFragment extends Fragment{

    private RepostTimelineView repostTimelineView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        repostTimelineView = (RepostTimelineView) inflater.inflate(R.layout.fragment_repost_timeline, container, false);
        return repostTimelineView;
    }

    public void show(RepostTimeline repostTimeline) {
        repostTimelineView.show(repostTimeline);
    }

    public static RepostTimelineFragment newInstance() {
        RepostTimelineFragment fragment = new RepostTimelineFragment();
        return fragment;
    }
}
