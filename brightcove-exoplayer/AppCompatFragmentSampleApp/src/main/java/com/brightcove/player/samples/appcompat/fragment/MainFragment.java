package com.brightcove.player.samples.appcompat.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brightcove.player.appcompat.BrightcovePlayerFragment;
import com.brightcove.player.concurrency.ConcurrencyClient;
import com.brightcove.player.edge.BrightcoveTokenAuthorizer;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.HttpRequestConfig;
import com.brightcove.player.view.BaseVideoView;

import java.util.HashMap;
import java.util.Map;

public class MainFragment extends BrightcovePlayerFragment {

    public static final String TAG = MainFragment.class.getSimpleName();
    private long playheadPositionToStop;

    // EPA Clear
    private final String clearJwt1 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICI0ODAwMjY2ODQ5MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTEiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUwMTIzLAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTgxMjMKfQ.iPfqFkJXw1XPs8wdIIUX1MZjpDgDukt1aSsb9j159zu-Bn3K7FPAGR6ERA91jya4dRKXrTbqk5YKnlepZlz8rT7ysRGsUpgHo_KRFhmYs5DrWWl8ie50yERHKnna7NZneYUjzQ9i-_vJrf9FS67qnLGL2CgtmC3WZrKPERJ5x13DQFHMoI9wQDEBbuA9hj6urEUTGwpxmMJvd-2NaCsCkA5AUgnvYgBLkAwwDNHuoZeX_f6sfv45_SoTamaNDYPN9rBWV-usn_MQDvDTSrjnH9R9pKFVFdWoIFRDJXUdMLmUam5E4MAd01iSh2lZ6v6QMs2x04aSob4v2MJsaXcB9A";
    private final String clearJwt2 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICI0ODAwMjY2ODQ5MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTIiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUwMjE1LAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTgyMTUKfQ.OoQJ91yPDQTsNjB8YKd7wJR-3yTLX9KY4OjERqojW7r2-By4vfO8yzggP2UFqri4SWKGbMBRWms8VVxX6gctWm_9Vt6fgZ2YIMJxBh_DwwN3QepxGWebPsRcbwmc6YlVFhWzChx58679KwfS6jZTdFoZ0ORXDRmAaFrsZZZRc5Ye17VzF8koTMolNMiFKZhA0GzsYfWrbrCXSHMtExqOkdPHntGxOmOIBEBf3pZgEPykSbve3qfB8u5U73PCkOux1z7F25K_jfXdyHHCjFY_w177uiOOo1ySCAlcK2CkMWIkNSsB-MAiNq9_brwVNWz8WpgwgHCm9PRnoDnojr2dWg";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_main, container, false);
        baseVideoView = (BaseVideoView) result.findViewById(R.id.brightcove_video_view);
        String account = getString(R.string.account);
        EventEmitter eventEmitter = baseVideoView.getEventEmitter();

        super.onCreateView(inflater, container, savedInstanceState);

        baseVideoView.setStreamConcurrencyEnabled(true);
        baseVideoView.setStreamConcurrencySessionsListener(sessionsList ->
                Log.v(TAG, "Stream Concurrency. Get Active Sessions: " + sessionsList.toString()));

        Catalog catalog = new Catalog.Builder(eventEmitter, account)
                .build();

        // Set the HttpRequestConfig with the Ad Config Id configured in
        // your https://studio.brightcove.com account.
        HttpRequestConfig httpRequestConfig = new HttpRequestConfig.Builder()
                .setBrightcoveAuthorizationToken(clearJwt1)
                .build();

        catalog.findVideoByReferenceID(getString(R.string.videoRefId), httpRequestConfig, new VideoListener() {
            @Override
            public void onVideo(Video video) {
                Log.v(TAG, "onVideo: video = " + video);
                baseVideoView.add(video);
            }
        });

        eventEmitter.once(EventType.DID_SET_VIDEO, event -> {
            Video video = (Video) event.getProperties().get(Event.VIDEO);
            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put(ConcurrencyClient.HEARTBEAT_VIDEO_HEADER_KEY, video.getId());
            requestHeaders.put(ConcurrencyClient.HEARTBEAT_ACCOUNTID_HEADER_KEY, account);
            requestHeaders.put(BrightcoveTokenAuthorizer.BRIGHTCOVE_AUTHORIZATION_HEADER_KEY, clearJwt1);
            baseVideoView.setStreamConcurrencyRequestHeaders(requestHeaders);
        });

//        eventEmitter.on(EventType.PROGRESS, event -> {
//            playheadPositionToStop = event.getIntegerProperty(Event.PLAYHEAD_POSITION);
//            if (playheadPositionToStop > 125000L) {
//                eventEmitter.emit(EventType.STOP);
//            }
//        });

        return result;
    }
}
