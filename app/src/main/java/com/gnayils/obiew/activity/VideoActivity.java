package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.gnayils.obiew.R;
import com.gnayils.obiew.weibo.bean.Video;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gnayils on 14/05/2017.
 */

public class VideoActivity extends AppCompatActivity {

    public static final String TAG = VideoActivity.class.getSimpleName();

    public static final String ARGS_KEY_VIDEO = "ARGS_KEY_VIDEO";

    public static final String STATE_CURRENT_POSITION = "STATE_CURRENT_POSITION";

    @Bind(R.id.video_view)
    protected VideoView videoView;
    @Bind(R.id.image_view_cover)
    protected ImageView coverImageView;
    @Bind(R.id.progress_bar)
    protected ProgressBar progressBar;

    private MediaController mediaController;

    private Video video;

    private int currentPosition = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(STATE_CURRENT_POSITION, 0);
        }
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        video = (Video) getIntent().getSerializableExtra(ARGS_KEY_VIDEO);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(video.video_src));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                coverImageView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        });
        Glide.with(this).load(video.cover_img).dontAnimate().into(coverImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(currentPosition);
        videoView.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                     View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.suspend();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_POSITION, videoView.getCurrentPosition());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }

    public static void start(Context context, Video video) {
        if(video != null && video.video_src != null && !video.video_src.isEmpty()) {
            Intent intent = new Intent(context, VideoActivity.class);
            intent.putExtra(ARGS_KEY_VIDEO, video);
            context.startActivity(intent);
        }
    }
}
