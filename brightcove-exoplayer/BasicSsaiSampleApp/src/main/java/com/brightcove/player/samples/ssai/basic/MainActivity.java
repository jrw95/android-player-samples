package com.brightcove.player.samples.ssai.basic;

import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.brightcove.player.Constants;
import com.brightcove.player.concurrency.ConcurrencyClient;
import com.brightcove.player.concurrency.ConcurrencySession;
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.drm.WidevineMediaDrmCallback;
import com.brightcove.player.edge.BrightcoveTokenAuthorizer;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.HttpRequestConfig;
import com.brightcove.player.render.TrackSelectorHelper;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.ssai.SSAIComponent;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BrightcovePlayer {
    // Private class constants
    private final String TAG = this.getClass().getSimpleName();
    private long playheadPositionOnComplete;

    @SuppressWarnings("FieldCanBeLocal")
    // EPA Clear AdConfig
    private final String EPA_CLEAR_AD_CONFIG = "c583484c-2344-466d-a63f-d9934e42b677";
    // EPA DRM AdConfig
    private final String EPA_DRM_AD_CONFIG = "7ab87662-469b-49ea-a5e6-29881e6d2270";
    // GSC Non-EPA AdConfig
    private final String GSC_DRM_AD_CONFIG = "cbe98010-de7f-4d84-b9d6-208172f2e5fc";
    // EPA HLSE AdConfig
    private final String EPA_HLSe_AD_CONFIG = "2519409e-2065-4375-aadf-cab73dd29cdf";

    // EPA Clear JWTs
    private final String epaclearJwt1 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICI0ODAwMjY2ODQ5MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTEiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUwMTIzLAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTgxMjMKfQ.iPfqFkJXw1XPs8wdIIUX1MZjpDgDukt1aSsb9j159zu-Bn3K7FPAGR6ERA91jya4dRKXrTbqk5YKnlepZlz8rT7ysRGsUpgHo_KRFhmYs5DrWWl8ie50yERHKnna7NZneYUjzQ9i-_vJrf9FS67qnLGL2CgtmC3WZrKPERJ5x13DQFHMoI9wQDEBbuA9hj6urEUTGwpxmMJvd-2NaCsCkA5AUgnvYgBLkAwwDNHuoZeX_f6sfv45_SoTamaNDYPN9rBWV-usn_MQDvDTSrjnH9R9pKFVFdWoIFRDJXUdMLmUam5E4MAd01iSh2lZ6v6QMs2x04aSob4v2MJsaXcB9A";
    private final String epaclearJwt2 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICI0ODAwMjY2ODQ5MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTIiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUwMjE1LAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTgyMTUKfQ.OoQJ91yPDQTsNjB8YKd7wJR-3yTLX9KY4OjERqojW7r2-By4vfO8yzggP2UFqri4SWKGbMBRWms8VVxX6gctWm_9Vt6fgZ2YIMJxBh_DwwN3QepxGWebPsRcbwmc6YlVFhWzChx58679KwfS6jZTdFoZ0ORXDRmAaFrsZZZRc5Ye17VzF8koTMolNMiFKZhA0GzsYfWrbrCXSHMtExqOkdPHntGxOmOIBEBf3pZgEPykSbve3qfB8u5U73PCkOux1z7F25K_jfXdyHHCjFY_w177uiOOo1ySCAlcK2CkMWIkNSsB-MAiNq9_brwVNWz8WpgwgHCm9PRnoDnojr2dWg";

    // EPA DRM JWTs
    private final String epadrmJwt1 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICIzNTM3NzgyNTY1MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTEiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUxNTUzLAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTk1NTMKfQ.ckrvanyiC1Q3u9uVCP6X4vDKOslzQqqstc2tx8qiX_nP44i1BXciB7S5mAl4oQ4488M2sqeWCmULQL_O-zC9jTvp-8cl_8GjbTh54O4hhzNp2EHf7Iy0yaiCIg_eq0Y1_W4LrVBnB_8sCEp_AAue1H0f5YwtRhn3wD3JaNyrpoWX6-KkBgXqdNDM8K7onn5w6cy550OspJSHhtTZB0NIwHVPeCSP2_f4lQ9mbcmyLdbwGbLYc4UvbsgbZncbzJK_OiGIFmTvAFqoAV7MjrTbokGBsOoSe3_6uQfMR1GIgNuB6feOH7y9ZvAaju2PY6h8KdFk6NDKDx2pSFfpuAwOhA";
    private final String epadrmJwt2 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICIzNTM3NzgyNTY1MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTIiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUxNjEzLAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTk2MTMKfQ.YGCB_xMg3t9KPYmTC-_iXA11euhu-ZW3OifTot1iw-8e8vP5DbpJN_h-8_bvHUd5Y2GrKP9Ica6Z4mPVb7crf0X5pC14mf1UwB8EQhrhvwrbelhFG1W2E1PZFgzf0C5766vFQjR3GhYBDIwNIX3X8frI1Nal5Vks-cOC5SVBzGdGtwRQk9eJdmSGg-CblEO6GD04Kz8NxxJMYMuff4k8u-zxfG4HSVEsibwWJuseBKMYUv1JVUTJrEaLPQns1-eWJLwSlSsXKxJYmHjZS08VdjE6AY9b7Hk-4A80Z1HhNV61mA19gImPO0cOBJ0aoCzvDIDYCBEzGlRsNzCIx6ASKg";

    // Non-EPA JWTs
    private final String drmJwt1 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICIzNjM2MzM0MTY0MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTEiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUwNzUxLAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTg3NTEKfQ.wF7KuVHppTMewwFkfnbEGgao8CRM5rcL40wbcT6zSF9SiqGuRHyGCBnMfR1ButOjug9-vpdp8A738GTs5MDjmOOXDTYGCnTrgDofFN9hLTSpvkLiUF7l9Q36srlnyN_4iZN6JHDFOk7EDdntgLyu7ll5t_5NpAXl5EDHV3CAMDLAV5zOLdlXr4E3LlFdV4F-06ZzJmJxn4PYxbnY9dbvQNTsZp0XKWnzv1xSBNYvhpJIHgwPpvG2enDvuPZA_-ZF7tSsVQPYh5dox-blfQeC6j6Gj_r7aUzLV_bi-RroLV5jYsvliDnXOWVjB9BdNL-Oc2LxIS_a_WduoMLcEV8uxw";
    private final String drmJwt2 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICIzNjM2MzM0MTY0MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTIiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUxMDA4LAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTkwMDgKfQ.oZYFr2ScuuC74Xj9cIr8RVEtcBO_tufqsLqhaQJV8B9eLuc93y0cQypYWUNRueYiuRwvCQ1vSILCyozEx30ROhPZ05oLrUAT7DBcfbuBBrYE0oJ5BISd0yGw6LFX8UtHKAzKnacBLry0i5DQedvpz17bvQ3gFvTyqpxT_3Bt0YOsBuKWsq03Xxy8WSQ8bzSAsO76FH38nlwykvEQ5s_yUgTRG0oHpq8KPAjy0n1Qj9EIi_ImgNJ_8q2c9H2gPKFZDTDBRW3Tz_9sY6X0ykV0Gn1BL-p21tr2UKuruNlUNa5-pjc29L2NcqfYsqbNz8Hj0Rthu7agEIEtZWo8mb10PA";
    private final String drmJwt3 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICIzNjM2MzM0MTY0MDAxIiwKICAidWlkIjogImFuZHJvaWRfc2RrX3Rlc3RpbmdfdXNhIiwKICAiZXhwIjogMTYzOTI1Mzk4MSwKICAiY2JlaCI6ICJCTE9DS19ORVciLAogICJpYXQiOiAxNjM2NjYxOTgxCn0.MUgWX_IXDB9OA_9QPIle21WEtMq0_wVEKAl8lEgyu9p3hqNDYid6yRRINZmFQDPZKpyZ01uX2m_ELSwiaelNQDcXsQ3_pd3ftvCm2Wc4BA7-_65tV_w50rpkdD7TB0NfCpRWWXIVJYrQ4aUSYp2pW_m3glRKh-EXj0JsnYMfVABIlgX7kGA1xFNP5gv9uh4en0VOActmq-icPRcawnu40m1YZOuISAAvC_pBls5_pW3sJsHrfVDeonrJOBWTjXuUIxoqiO6G3hhkSF6_kyKw7Ltg0rsr0KJejhzY5NhcDSJzBkDDFehwZMkSFimjBx7zdo2JR4ByAhEC0Xz6JySzCQ";

    // EPA HLSe JWTs
    private final String epahlseJwt1 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICI1NTA3Nzc4NzYzMDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTEiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MzI4MTQ3LAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY3MzYxNDcKfQ.LcY9Ickl8cz2mUFMk1bFdqtKNsW2XoEVP6SevagSiyfgGTfglx77pTasRObRn-dvJaBC8A2ODGPmWm40O6UiebTVy9yHIdY8f-k74eQRbpFDIXXp56ngcGBLwOYgnYyEhFuhFy3lfFU2TVv1A6QDk21MKbFtJQNuAZxZO180B_xxHtssM3yP0JwGBTU9xc-sWf4PDmAz06Uaf1XfinuxPQDRVLTo4_4BKR96GxElFqqgbpTuwrbW7mr8vFlA-7WxsTsdJQT-3Pai-Nv6nTwa78lPqsegdF267ky3jEC6yI9QZZC87TgipLYBsIfN4EVJWNH7hP4iqXgq46vZ0lWdbg";
    private final String epahlseJwt2 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICI1NTA3Nzc4NzYzMDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTIiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MzI4MTg2LAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY3MzYxODYKfQ.ts2nzxpExHYQU06BIvhOEQn0BO76tILN_ibgJwlkgBuRcCdWFBJKKy_KTW_THKOPgii4XNg6tJ7hnh-HHt3x3FZcm1F5Cbh-kq1CFlhpiZAHaO3gbl_TU7mXFreLo8VLyj_zh-XYgB2lYSWcGpD2_8EVwjCPzmiOjdvyFs6AYcTAtwyqwCHZ6A25DV-eNbOZCqx503fFpGufRAn-YD2MxxGxHMaijwzwV_8buo0AtjiRXjj0DA_vfRPi-l_PIaUwDtTZzxLcZQLVBGIazEoVzsmp_XVdVC5vImPaD8oj2g5mBgO2kU0NeMNMycXZ1hQULm3d4_ZjTQCztI4142janQ";

    // BC-54907 Playback Token
    private final String AD_CONFIG_ID_QUERY_PARAM_VALUE = "live.EtrcsmmJquVOR_WJU6qKA92JbmJ9SprV9WnOWYkjapqJH9xpO5fhsAvpzwOMf-uHRJ1hE_9oDr0HuEP7jiVLFUEyKriWRACgVbxDpMFWhrdx-o_7MaXWFFCWFFfWeohObd3nY-zlJqN7SOAJeA";

    private SSAIComponent plugin;
    private ExoPlayerVideoDisplayComponent videoDisplayComponent;
    private EventEmitter eventEmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass.  This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.ssai_activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);

        brightcoveVideoView.setStreamConcurrencyEnabled(true);
        brightcoveVideoView.setStreamConcurrencySessionsListener(sessionsList ->
                Log.v(TAG, "Stream Concurrency. Get Active Sessions: " + sessionsList.toString()));

        videoDisplayComponent = (ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();
        super.onCreate(savedInstanceState);

        final EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();

        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
//                .setPolicy(getString(R.string.policy))
                .build();

        // Setup the error event handler for the SSAI plugin.
        registerErrorEventHandler();
        plugin = new SSAIComponent(this, brightcoveVideoView);
        View view = findViewById(R.id.ad_frame);
        if (view instanceof ViewGroup) {
            // Set the companion ad container,
            plugin.addCompanionContainer((ViewGroup) view);
        }

        // Set the HttpRequestConfig with the Ad Config Id configured in
        // your https://studio.brightcove.com account.
        HttpRequestConfig httpRequestConfig = new HttpRequestConfig.Builder()
                .setBrightcoveAuthorizationToken(epaclearJwt1)
//                .addQueryParameter(HttpRequestConfig.KEY_DELIVERY_RULE_CONFIG_ID, "de40204e-3cb7-49e6-8d4a-7ea3b673dac4")
                .addQueryParameter(HttpRequestConfig.KEY_AD_CONFIG_ID, EPA_CLEAR_AD_CONFIG)
                .build();

        catalog.findVideoByReferenceID(getString(R.string.video_id), httpRequestConfig, new VideoListener() {
            @Override
            public void onVideo(Video video) {
                // The Video Sources will have a VMAP url which will be processed by the SSAI plugin,
                // If there is not a VMAP url, or if there are any requesting or parsing error,
                // an EventType.ERROR event will be emitted.
                plugin.processVideo(video);
            }
        });

        eventEmitter.on(EventType.DID_SET_VIDEO, event -> {
            Video video = (Video) event.getProperties().get(Event.VIDEO);
            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put(ConcurrencyClient.HEARTBEAT_VIDEO_HEADER_KEY, video.getId());
            requestHeaders.put(ConcurrencyClient.HEARTBEAT_ACCOUNTID_HEADER_KEY, getString(R.string.sdk_demo_account));
            requestHeaders.put(BrightcoveTokenAuthorizer.BRIGHTCOVE_AUTHORIZATION_HEADER_KEY, epaclearJwt1);
            brightcoveVideoView.setStreamConcurrencyRequestHeaders(requestHeaders);
        });

        eventEmitter.on(EventType.GSC_MAX_CONCURRENCY_REACHED, event -> {
            Log.v(TAG, "Max Concurrency Reached: " + event.getProperties().get(Event.GSC_MAX_CONCURRENCY_REACHED_MESSAGE));
        });

        eventEmitter.on(EventType.GSC_ERROR, event -> {
            Log.v(TAG, "Concurrency Error: " + event.getProperties().toString());
        });


//        eventEmitter.on(EventType.SET_SOURCE, event -> {
//            try {
//                // Get an instance of the MediaDrm from the device
//                MediaDrm mediaDrm = new MediaDrm(Constants.WIDEVINE_UUID);
//
//                // Create a new DefaultTrackSelector.ParamsBuilder object, and call setMaxVideoSizeSd()
//                DefaultTrackSelector.ParametersBuilder builder = new DefaultTrackSelector.ParametersBuilder(this);
//                builder.setAllowMultipleAdaptiveSelections(true);
//
//                // Get the values for hdcpLevel and maxHdcpLevel
//                String connectedHdcpLevel = mediaDrm.getPropertyString("hdcpLevel");
//                String maxHdcpLevel = mediaDrm.getPropertyString("maxHdcpLevel");
//                Log.v(TAG, "HDCP Level: " + connectedHdcpLevel + " Max HDCP Level: " + maxHdcpLevel);
//
//                // If either level reads "Unprotected"
//                if ((TextUtils.isEmpty(connectedHdcpLevel) || TextUtils.isEmpty(maxHdcpLevel)) ||
//                        ("Unprotected".equals(connectedHdcpLevel) || "Unprotected".equals(maxHdcpLevel))) {
//                    Log.v(TAG, "Restricting rendition selection to SD");
//
//                    // Set the max video size to SD
//                    builder.setMaxVideoSizeSd();
//                }
//
//                DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector(this);
//                defaultTrackSelector.setParameters(builder.build());
//                // Create a new DefaultTrackSelector object, and set the Parameters object created above
//                // Set this DefaultTrackSelector object on the ExoPlayerVideoDisplayComponent
//                videoDisplayComponent.setTrackSelector(defaultTrackSelector);
//                Log.v(TAG, "Should have restricted rendition selection to SD");
//            }
//            catch (Exception exception) {
//                if (exception instanceof UnsupportedSchemeException) {
//                    Log.e(TAG, "UnsupportedSchemeException: " + exception.getLocalizedMessage());
//                }
//                else {
//                    Log.e(TAG, "An unexpected error occurred: " + exception.getLocalizedMessage());
//                }
//            }
//        });

        eventEmitter.on(EventType.COMPLETED, event -> {
            playheadPositionOnComplete = event.getIntegerProperty(Event.PLAYHEAD_POSITION);
            Log.v(TAG, "playheadPositionOnComplete: " + playheadPositionOnComplete);
        });

        eventEmitter.on(EventType.PLAY, event -> {
            long playheadPositionOnPlay = event.getIntegerProperty(Event.PLAYHEAD_POSITION);
            Log.v(TAG, "playheadPositionOnPlay: " + playheadPositionOnPlay + " playheadPositionOnComplete: " + playheadPositionOnComplete);
            if (playheadPositionOnPlay >= playheadPositionOnComplete) {
                brightcoveVideoView.seekTo(0);
            }
        });

        eventEmitter.on(EventType.ANALYTICS_VIDEO_ENGAGEMENT, event -> {
            TrackSelectorHelper trackSelectorHelper = videoDisplayComponent.getTrackSelectorHelper();
            Log.v(TAG, "Available Formats: " + trackSelectorHelper.getAvailableFormatList(ExoPlayerVideoDisplayComponent.TYPE_VIDEO));
        });
    }

    private void registerErrorEventHandler() {
        // Handle the case where the ad data URL has not been supplied to the plugin.
        eventEmitter = brightcoveVideoView.getEventEmitter();
        eventEmitter.on(EventType.ERROR, event -> Log.e(TAG, event.getType()));

    }
}
