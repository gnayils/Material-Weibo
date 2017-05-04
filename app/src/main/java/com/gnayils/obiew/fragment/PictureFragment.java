package com.gnayils.obiew.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.gnayils.obiew.R;
import com.gnayils.obiew.bmpldr.BitmapLoader;
import com.gnayils.obiew.bmpldr.BitmapLoadListener;
import com.gnayils.obiew.weibo.bean.PicUrls;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gnayils on 04/03/2017.
 */

public class PictureFragment extends Fragment implements BitmapLoadListener {

    public static final String ARGS_KEY_PICTURE_URL = "ARGS_KEY_PICTURE_URL";

    @Bind(R.id.progress_bar)
    protected ProgressBar progressBar;

    @Bind(R.id.image_view)
    protected ImageView imageView;

    private PicUrls picUrls;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        picUrls = (PicUrls) getArguments().getSerializable(ARGS_KEY_PICTURE_URL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BitmapLoader.getInstance().loadBitmap(picUrls.largeThumbnailPic(), imageView, this);
    }

    public static Fragment newInstance(PicUrls picUrls) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_KEY_PICTURE_URL, picUrls);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPreLoad() {

    }

    @Override
    public void onProgressUpdate(Integer... values) {

    }

    @Override
    public void onPostLoad(Bitmap bitmap) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onCancelled() {

    }
}
