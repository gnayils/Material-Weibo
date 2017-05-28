package com.gnayils.obiew.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.model.stream.StreamUriLoader;
import com.bumptech.glide.load.resource.SimpleResource;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.gnayils.obiew.R;
import com.gnayils.obiew.glide.BitmapSizeDecoder;
import com.gnayils.obiew.glide.LoggingListener;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.view.TouchImageView;
import com.gnayils.obiew.weibo.bean.PicUrls;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocketImpl;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gnayils on 04/03/2017.
 */

public class PictureFragment extends Fragment {

    public static final String ARGS_KEY_PICTURE_URL = "ARGS_KEY_PICTURE_URL";

    @Bind(R.id.progress_bar)
    protected ProgressBar progressBar;

    @Bind(R.id.frame_layout)
    protected FrameLayout frameLayout;
    private FrameLayout.LayoutParams childLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    private PicUrls picUrls;

    private Size screenSize;

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
        screenSize = ViewUtils.getScreenSize(getContext());

        if(picUrls.isGif()) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(childLayoutParams);
            frameLayout.addView(imageView);
            Glide.with(this).load(picUrls.large()).asGif().listener(new RequestListener<String, GifDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    progressBar.setVisibility(View.INVISIBLE);
                    return false;
                }
            }).into(imageView);
        } else {
            Glide.with(this)
                    .using(new StreamUriLoader(getContext()), InputStream.class)
                    .from(Uri.class).as(BitmapFactory.Options.class)
                    .sourceEncoder(new StreamEncoder())
                    .cacheDecoder(new BitmapSizeDecoder())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new LoggingListener<Uri, BitmapFactory.Options>())
                    .load(Uri.parse(picUrls.large()))
                    .into(new SimpleTarget<BitmapFactory.Options>() {
                        @Override
                        public void onResourceReady(BitmapFactory.Options resource, GlideAnimation glideAnimation) {
                            PictureFragment.this.onResourceReady(resource, glideAnimation);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            PictureFragment.this.onLoadFailed(e, errorDrawable);
                        }
                    });
        }
        return view;
    }

    private void onResourceReady(BitmapFactory.Options resource, GlideAnimation glideAnimation) {
        View view = null;
        if (resource.outWidth > screenSize.getWidth() || resource.outHeight > screenSize.getHeight()) {
            final View finalView = view = new SubsamplingScaleImageView(getContext());
            view.setLayoutParams(childLayoutParams);
            Glide.with(this)
                    .using(new StreamUriLoader(getContext()), InputStream.class)
                    .from(Uri.class).as(BitmapFactory.Options.class)
                    .sourceEncoder(new StreamEncoder())
                    .cacheDecoder(new ResourceDecoder<File, BitmapFactory.Options>() {
                        @Override
                        public Resource<BitmapFactory.Options> decode(final File source, int width, int height) throws IOException {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((SubsamplingScaleImageView)finalView).setImage(ImageSource.uri(source.getAbsolutePath()));
                                }
                            });
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(source.getAbsolutePath(), options);
                            return new SimpleResource<>(options);
                        }

                        @Override
                        public String getId() {
                            return getClass().getName();
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new LoggingListener<Uri, BitmapFactory.Options>())
                    .load(Uri.parse(picUrls.large()))
                    .into(new SimpleTarget<BitmapFactory.Options>() {
                        @Override
                        public void onResourceReady(BitmapFactory.Options resource, GlideAnimation glideAnimation) {
                            progressBar.setVisibility(View.INVISIBLE);
                            ((SubsamplingScaleImageView)finalView).setScaleAndCenter(screenSize.getWidth() / 1.0f / resource.outWidth, new PointF(0, screenSize.getWidth() / 2));
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        }
                    });

        } else {
            final View finalView = view = new TouchImageView(getContext());
            view.setLayoutParams(childLayoutParams);
            Glide.with(this).load(picUrls.large()).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (finalView instanceof TouchImageView) {
                        ((TouchImageView) finalView).setImageBitmap(resource);
                    }
                }
            });
        }
        if(view != null) {
            frameLayout.addView(view);
        }
    }

    private void onLoadFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static Fragment newInstance(PicUrls picUrls) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_KEY_PICTURE_URL, picUrls);
        fragment.setArguments(args);
        return fragment;
    }

}
