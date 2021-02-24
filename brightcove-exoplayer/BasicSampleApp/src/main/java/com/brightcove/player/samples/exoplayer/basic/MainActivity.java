package com.brightcove.player.samples.exoplayer.basic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.drm.WidevineMediaDrmCallback;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.android.exoplayer2.PlaybackParameters;

/**
 * This app illustrates how to use the ExoPlayer with the Brightcove
 * Native Player SDK for Android.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();
    private TextView playbackSpeed;
    private EventEmitter eventEmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String contentType = intent.getStringExtra(ContentSelection.CONTENT_TYPE);
        String videoId = intent.getStringExtra(ContentSelection.VIDEO_ID);
        if (!TextUtils.isEmpty(contentType)) {
            if (ContentSelection.CONTENT_TYPE_VIDEO_OBJECT.equalsIgnoreCase(contentType)) {
                loadVideoFromVideoObject();
            }
            else {
                loadVideoFromCatalog(videoId);
            }
        }

        playbackSpeed = (TextView) findViewById(R.id.speed);
        playbackSpeed.setVisibility(View.VISIBLE);
        playbackSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlayerSpeedDialog();
            }
        });

    }

    private void showPlayerSpeedDialog() {
        String[] playerSpeedArrayLabels = {"1.0x", "0.8x", "1.2x", "1.5x", "1.8x", "2.0x"};

        PopupMenu popupMenu = new PopupMenu(MainActivity.this, playbackSpeed);
        for (int i = 0; i < playerSpeedArrayLabels.length; i++) {
            popupMenu.getMenu().add(i, i, i, playerSpeedArrayLabels[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            CharSequence itemTitle = item.getTitle();
            float playbackSpeed = Float.parseFloat(itemTitle.subSequence(0, 3).toString());
            changePlayerSpeed(playbackSpeed, itemTitle.subSequence(0, 3).toString());
            return false;
        });
        popupMenu.show();
    }

    private void changePlayerSpeed(float speed, String speedLabel) {
        // Set playback speed
        ((ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay()).getExoPlayer().setPlaybackParameters(new PlaybackParameters(speed, 1.0f));
        // Set playback speed label
        playbackSpeed.setText("Speed: " + speedLabel + "x");
    }

    private void loadVideoFromCatalog(String videoId) {
        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.account)).setPolicy(getString(R.string.policy)).build();

        catalog.findVideoByID(videoId, new VideoListener() {

            // Add the video found to the queue with add().
            // Start playback of the video with start().
            @Override
            public void onVideo(Video video) {
                Log.v(TAG, "onVideo: video = " + video);
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }
        });
    }

    private void loadVideoFromVideoObject() {
        // To load a Video object created from the Google demo video content and license URLs, uncomment this code
        Video video = Video.createVideo("https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd", DeliveryType.DASH);
        video.getProperties().put(WidevineMediaDrmCallback.DEFAULT_URL, "https://proxy.uat.widevine.com/proxy?video_id=d286538032258a1c&provider=widevine_test");
        brightcoveVideoView.add(video);
        brightcoveVideoView.start();
    }
}
