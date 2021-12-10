package com.brightcove.player.samples.pictureinpicture;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.brightcove.player.concurrency.ConcurrencyClient;
import com.brightcove.player.edge.BrightcoveTokenAuthorizer;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.HttpRequestConfig;
import com.brightcove.player.pictureinpicture.PictureInPictureManager;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BrightcovePlayer {

    private static final String TAG = MainActivity.class.getSimpleName();
    // EPA Clear JWTs
    private final String epaclearJwt1 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICI0ODAwMjY2ODQ5MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTEiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUwMTIzLAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTgxMjMKfQ.iPfqFkJXw1XPs8wdIIUX1MZjpDgDukt1aSsb9j159zu-Bn3K7FPAGR6ERA91jya4dRKXrTbqk5YKnlepZlz8rT7ysRGsUpgHo_KRFhmYs5DrWWl8ie50yERHKnna7NZneYUjzQ9i-_vJrf9FS67qnLGL2CgtmC3WZrKPERJ5x13DQFHMoI9wQDEBbuA9hj6urEUTGwpxmMJvd-2NaCsCkA5AUgnvYgBLkAwwDNHuoZeX_f6sfv45_SoTamaNDYPN9rBWV-usn_MQDvDTSrjnH9R9pKFVFdWoIFRDJXUdMLmUam5E4MAd01iSh2lZ6v6QMs2x04aSob4v2MJsaXcB9A";
    private final String epaclearJwt2 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICI0ODAwMjY2ODQ5MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTIiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUwMjE1LAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTgyMTUKfQ.OoQJ91yPDQTsNjB8YKd7wJR-3yTLX9KY4OjERqojW7r2-By4vfO8yzggP2UFqri4SWKGbMBRWms8VVxX6gctWm_9Vt6fgZ2YIMJxBh_DwwN3QepxGWebPsRcbwmc6YlVFhWzChx58679KwfS6jZTdFoZ0ORXDRmAaFrsZZZRc5Ye17VzF8koTMolNMiFKZhA0GzsYfWrbrCXSHMtExqOkdPHntGxOmOIBEBf3pZgEPykSbve3qfB8u5U73PCkOux1z7F25K_jfXdyHHCjFY_w177uiOOo1ySCAlcK2CkMWIkNSsB-MAiNq9_brwVNWz8WpgwgHCm9PRnoDnojr2dWg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);
        brightcoveVideoView.setStreamConcurrencyEnabled(true);
        brightcoveVideoView.setStreamConcurrencySessionsListener(sessionsList ->
                Log.v(TAG, "Stream Concurrency. Get Active Sessions: " + sessionsList.toString()));

        String account = getString(R.string.account);

        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();

//        Catalog catalog = new Catalog.Builder(eventEmitter, account)
//                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
//                .setPolicy(getString(R.string.policy))
//                .build();

        Catalog catalog = new Catalog.Builder(eventEmitter, account)
//                .setPolicy(getString(R.string.policy))
                .build();
//                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
//                .setPolicy(getString(R.string.policy))
//                .build();

        // Set the HttpRequestConfig with the Ad Config Id configured in
        // your https://studio.brightcove.com account.
        HttpRequestConfig httpRequestConfig = new HttpRequestConfig.Builder()
                // thumbnail scrubbing
//                .addQueryParameter(HttpRequestConfig.KEY_DELIVERY_RULE_CONFIG_ID, "0ae267f1-cafd-4e6e-acc5-e5cc0ccf568c")
//                .addQueryParameter(HttpRequestConfig.KEY_AD_CONFIG_ID, "cbe98010-de7f-4d84-b9d6-208172f2e5fc")
                .setBrightcoveAuthorizationToken(epaclearJwt2)
                .build();

        catalog.findVideoByReferenceID(getString(R.string.videoRefId), httpRequestConfig, new VideoListener() {
            @Override
            public void onVideo(Video video) {
                Log.v(TAG, "onVideo: video = " + video);
                brightcoveVideoView.add(video);
//                brightcoveVideoView.start();
            }
        });

        eventEmitter.once(EventType.DID_SET_VIDEO, event -> {
            Video video = (Video) event.getProperties().get(Event.VIDEO);
            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put(ConcurrencyClient.HEARTBEAT_VIDEO_HEADER_KEY, video.getId());
            requestHeaders.put(ConcurrencyClient.HEARTBEAT_ACCOUNTID_HEADER_KEY, account);
            requestHeaders.put(BrightcoveTokenAuthorizer.BRIGHTCOVE_AUTHORIZATION_HEADER_KEY, epaclearJwt2);
            brightcoveVideoView.setStreamConcurrencyRequestHeaders(requestHeaders);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        SettingsModel settingsModel = new SettingsModel(this);
        //Configure Picture in Picture
        PictureInPictureManager manager = PictureInPictureManager.getInstance();
        manager.setClosedCaptionsEnabled(settingsModel.isPictureInPictureClosedCaptionsEnabled())
                .setOnUserLeaveEnabled(settingsModel.isPictureInPictureOnUserLeaveEnabled())
                .setClosedCaptionsReductionScaleFactor(settingsModel.getPictureInPictureCCScaleFactor())
                .setAspectRatio(settingsModel.getPictureInPictureAspectRatio());
    }

    public void onClickConfigurePictureInPicture(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this,
                    "Picture-in-Picture is currently available only on Android Oreo or Higher",
                    Toast.LENGTH_LONG).show();
        }
    }
}

