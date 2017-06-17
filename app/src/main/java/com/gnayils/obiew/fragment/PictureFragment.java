package com.gnayils.obiew.fragment;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.model.stream.StreamUriLoader;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.gnayils.obiew.R;
import com.gnayils.obiew.util.Popup;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.weibo.bean.PicUrls;

import java.io.File;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gnayils on 04/03/2017.
 */

public class PictureFragment extends Fragment {

    public static final String ARGS_KEY_PICTURE_URL = "ARGS_KEY_PICTURE_URL";

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.image_view)
    ImageView imageView;
    @Bind(R.id.subsampling_scale_image_view)
    SubsamplingScaleImageView subsamplingScaleImageView;

    private PicUrls picUrls;

    private Size screenSize;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        picUrls = (PicUrls) getArguments().getSerializable(ARGS_KEY_PICTURE_URL);
        screenSize = ViewUtils.getScreenSize(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (picUrls.isGif()) {
            Glide.with(this).load(picUrls.large()).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<String, GifDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                    onImageLoadingFinished(false);
                    return false;
                }

                @Override
                public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    onImageLoadingFinished(true);
                    return false;
                }
            }).into(imageView);
        } else {
            Glide.with(this)
                    .using(new StreamUriLoader(getContext()), InputStream.class)
                    .from(Uri.class).as(File.class)
                    .sourceEncoder(new StreamEncoder())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .load(Uri.parse(picUrls.large()))
                    .into(new SimpleTarget<File>() {

                        @Override
                        public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                            subsamplingScaleImageView.setImage(ImageSource.uri(resource.getAbsolutePath()));
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(resource.getAbsolutePath(), options);
                            subsamplingScaleImageView.setScaleAndCenter(screenSize.getWidth() / 1.0f / options.outWidth, new PointF(0, screenSize.getWidth() / 2));
                            onImageLoadingFinished(true);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            onImageLoadingFinished(false);
                        }
                    });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        subsamplingScaleImageView.recycle();
    }

    private void onImageLoadingFinished(boolean isLoadingSuccessful) {
        progressBar.setVisibility(View.INVISIBLE);
        if (!picUrls.isGif()) {
            imageView.setVisibility(View.INVISIBLE);
        }
        if (!isLoadingSuccessful) {
            Popup.toast("加载图片失败");
        }
    }

    public static Fragment newInstance(PicUrls picUrls) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_KEY_PICTURE_URL, picUrls);
        fragment.setArguments(args);
        return fragment;
    }

}
