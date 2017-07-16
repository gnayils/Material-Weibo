package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.gnayils.obiew.R;
import com.gnayils.obiew.weibo.bean.Video;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gnayils on 14/05/2017.
 */

public class PlayerActivity extends BaseActivity implements ExoPlayer.EventListener {

    public static final String TAG = PlayerActivity.class.getSimpleName();

    public static final String ARGS_KEY_VIDEO = "ARGS_KEY_VIDEO";

    public static final String STATE_CURRENT_POSITION = "STATE_CURRENT_POSITION";

    @Bind(R.id.player_view)
    protected SimpleExoPlayerView simpleExoPlayerView;
    @Bind(R.id.image_view_cover)
    protected ImageView coverImageView;
    @Bind(R.id.progress_bar)
    protected ProgressBar progressBar;


    private Video video;
    private long resumePosition = 0;
    private SimpleExoPlayer player;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        video = (Video) getIntent().getSerializableExtra(ARGS_KEY_VIDEO);

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        player.addListener(this);
        simpleExoPlayerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, getResources().getString(R.string.app_name), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(video.video_src), dataSourceFactory, extractorsFactory, null, null);
        player.prepare(videoSource);

        Glide.with(this).load(video.cover_img).dontAnimate().into(coverImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pausePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
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

    private void resumePlayer() {
        player.seekTo(resumePosition);
        player.setPlayWhenReady(true);
    }

    private void pausePlayer() {
        player.setPlayWhenReady(false);
        resumePosition = player.getCurrentPosition();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if(playbackState == ExoPlayer.STATE_BUFFERING) {
            progressBar.setVisibility(View.VISIBLE);
        } else if(playbackState == ExoPlayer.STATE_ENDED) {
        } else if(playbackState == ExoPlayer.STATE_IDLE) {
        } else if(playbackState == ExoPlayer.STATE_READY) {
            coverImageView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    public static void start(Context context, Video video) {
        if (video != null && video.video_src != null && !video.video_src.isEmpty()) {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra(ARGS_KEY_VIDEO, video);
            context.startActivity(intent);
        }
    }
}
