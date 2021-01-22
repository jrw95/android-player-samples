package com.brightcove.player.samples.ima.adrules;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.mediacontroller.BrightcoveSeekBar;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.HttpRequestConfig;
import com.brightcove.player.view.BaseVideoView;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * This app illustrates how to use the Google IMA plugin with Ad Rules (aka VMAP)
 * with the Brightcove Player for Android.
 *
 * Note: Video cue points are not used with IMA Ad Rules. The AdCuePoints referenced
 * in the setupAdMarkers method below are Google IMA objects.
 *
 * @author Paul Matthew Reilly (original code)
 * @author Paul Michael Reilly (added explanatory comments)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private GoogleIMAComponent googleIMAComponent;
    private final String CORRELATOR_KEY = "correlatorKey";
    private long currentContentPlayheadPosition = -1;
    private long previousContentPlayheadPosition = -1;
    private ExoPlayer contentPlayer;

    private String accountId;
    private String policyKey;
    private String videoId;
    private String jwt;

    // Needed for HLSe account testing
    private final String otherJwt = "";
    private final String hlseJwt = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJwa2lkIjogIjRlMDRmMzliLTdiZGMtNGFkZi1hMzA4LWUzOTQ2MDQyZjcwOSIsCiAgInByaWQiOiAiIiwKICAiYWNjaWQiOiAiNTUwNzc3ODc2MzAwMSIsCiAgInVpZCI6ICJhbmRyb2lkLXNkayIsCiAgImlhdCI6IDE2MDkyNzIyMDYsCiAgImV4cCI6IDE2MTE4NjQyMDYsCiAgIm5iZiI6IDE2MDkyNzIyMDYKfQ.OEp1P3MZVU79MNjTXsqnQUIsjzuNrxNkUT06ODGPw42JW4F-I01ScsdSZ8kMO11sM94-PVogl62iQU5L8ciX5GF3xN5E3Xsc3gOeC6QOj5tiNy4QSm37VMjDmvfr-a6s8vFtZeAYavenuJcDhQu8aaoo693cCTSJ7E-D3qvJ7E4z8x-xvCqDwcjAM64v7iIyyc9UOEve8QLflAC-KMcsRMy6kb98PD0z15ZsKQ3Y9aWowPKxDzhRmFpwHvV_qUE3U-_0BkTYSUowXjTc8vnEGUod6trDyvfSD7jlZ74rh1X06VwQ2t_ZZ5dxuNjr9rrh_PsbhYYJKGGw4l813ZXzlQ";

    // 28 Dec test ad tag URL 1.1.1 - with timestamp replacement for the correlator
    private String adRulesURL = "https://pubads.g.doubleclick.net/gampad/ads?sz=300x250|320x180|400x300|480x270|640x360|640x480|854x480|960x540|1280x720|1920x1080|1920x1280&iu=/210325652/AD_Test/KOCOWABCAPP001&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&url=https://www.kocowa.com/auto/channel/13614149&description_url=https://www.kocowa.com/auto/channel/13614149&correlator=" + CORRELATOR_KEY + "&ad_rule=1&cmsid=2532769&vid=6171350716001";

    // Plato ad tag - 3x Skippable preroll, 3x Skippable midroll, 3x Skippable postroll
//    private String adRulesURL = "http://appsci.vidmark.local:9090/formats/IMA3/combined/pre-mid-post-multiple-skippable-ads.handlebars";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveExoPlayerVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.ima_activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);

        // *** This method call is optional *** //
        setupAdMarkers(brightcoveVideoView);

        super.onCreate(savedInstanceState);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        // Use a procedural abstraction to setup the Google IMA SDK via the plugin.
        setupGoogleIMA();

        String contentType = getIntent().getStringExtra(TestOptionsActivity.CONTENT_TYPE);
        boolean useHlsIfAvailable = getIntent().getBooleanExtra(TestOptionsActivity.USE_HLS_IF_AVAILABLE, false);

        switch (contentType) {
            case "Clear DASH":
                accountId = getString(R.string.boltClearAccount);
                policyKey = getString(R.string.boltClearPolicyKey);
                videoId = getString(R.string.boltClearVideoId);
                jwt = otherJwt;
                break;
            case "DRM DASH":
                accountId = getString(R.string.boltDrmAccount);
                policyKey = getString(R.string.boltDrmPolicyKey);
                videoId = getString(R.string.boltDrmVideoId);
                jwt = otherJwt;
                break;
            case "HLSe":
                accountId = getString(R.string.boltHlseAccount);
                policyKey = getString(R.string.boltHlsePolicyKey);
                videoId = getString(R.string.boltHlseVideoId);
                jwt = hlseJwt;
                break;
            default:
                break;
        }

        HttpRequestConfig.Builder requestConfigBuilder = new HttpRequestConfig.Builder();
        if (!TextUtils.isEmpty(jwt)) {
            requestConfigBuilder.setBrightcoveAuthorizationToken(jwt);
        }

        Catalog catalog = new Catalog.Builder(eventEmitter, accountId)
                .setPolicy(policyKey)
                .build();

        catalog.findVideoByID(videoId, requestConfigBuilder.build(), new VideoListener() {
            public void onVideo(Video video) {
                if (useHlsIfAvailable) {
                    sourceSelectionFilter(DeliveryType.HLS, video);
                }
                brightcoveVideoView.add(video);

                // Auto play: the GoogleIMAComponent will postpone
                // playback until the Ad Rules are loaded.
//                brightcoveVideoView.start();
            }

            public void onError(@NonNull List<CatalogError> errors) {
                Log.e(TAG, errors.toString());
            }
        });

        eventEmitter.on(EventType.AD_BREAK_COMPLETED, event -> {
            Video video = (Video) event.getProperty(Event.VIDEO);
            int playheadPosition = event.getIntegerProperty(Event.PLAYHEAD_POSITION);
            Log.v(TAG, "didSeekTo playheadPosition: " + playheadPosition);
            // If we seek to within a second of the end of the video, or back to 0
            if (playheadPosition == 0 || (playheadPosition > (video.getDuration() - 1000))) {
                // Seek a few ms into the video, to allow playback to start
                brightcoveVideoView.seekTo(50);
            }
        });

        eventEmitter.on(EventType.DID_SEEK_TO, event -> {
            Video video = (Video) event.getProperty(Event.VIDEO);
            int playheadPosition = event.getIntegerProperty(Event.PLAYHEAD_POSITION);
            Log.v(TAG, "didSeekTo playheadPosition: " + playheadPosition);
            // If we seek to within a second of the end of the video, or back to 0
            if (playheadPosition == 0 || (playheadPosition > (video.getDuration() - 1000))) {
                // Seek a few ms into the video, to allow playback to start
                brightcoveVideoView.seekTo(50);
            }
        });
    }

    /**
     * Setup the Brightcove IMA Plugin.
     */
    private void setupGoogleIMA() {
        // Establish the Google IMA SDK factory instance.
        final ImaSdkFactory sdkFactory = ImaSdkFactory.getInstance();

        // Enable logging up ad start.
        eventEmitter.on(EventType.AD_STARTED, event -> Log.v(TAG, event.getProperties().toString()));

        // Enable Logging upon ad completion.
        eventEmitter.on(EventType.AD_COMPLETED, event -> Log.v(TAG, event.getProperties().toString()));

        // Enable logging any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD, event -> Log.v(TAG, event.getProperties().toString()));

        // Set up a listener for initializing AdsRequests. The Google
        // IMA plugin emits an ad request event as a result of
        // initializeAdsRequests() being called.
        eventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO, event -> {
            // Build an ads request object and point it to the ad
            // display container created above.
            // Create a container object for the ads to be presented.
            AdsRequest adsRequest = sdkFactory.createAdsRequest();
            long appTimeStamp = System.currentTimeMillis();
            String adRulesURLWithCorrelatorTimestamp = adRulesURL.replace(CORRELATOR_KEY, String.valueOf(appTimeStamp));
            adsRequest.setAdTagUrl(adRulesURLWithCorrelatorTimestamp);

            ArrayList<AdsRequest> adsRequests = new ArrayList<>(1);
            adsRequests.add(adsRequest);

            // Respond to the event with the new ad requests.
            event.properties.put(GoogleIMAComponent.ADS_REQUESTS, adsRequests);
            eventEmitter.respond(event);

        });

//        AdsRenderingSettings adsRenderingSettings =
//                sdkFactory.createAdsRenderingSettings();
//        List<String> arrayList = new ArrayList<>();
//        arrayList.add("video/mp4"); //MP4
//        arrayList.add("video/webm"); //WEBM
//        adsRenderingSettings.setMimeTypes(arrayList);

        googleIMAComponent = new GoogleIMAComponent.Builder(brightcoveVideoView, eventEmitter)
                .setUseAdRules(true)
//                .setAdsRenderingSettings(adsRenderingSettings)
                .setDebugMode(true)
                .build();
    }

    /*
      This methods show how to the the Google IMA AdsManager, get the cue points and add the markers
      to the Brightcove Seek Bar.
     */
    private void setupAdMarkers(BaseVideoView videoView) {
        final BrightcoveMediaController mediaController = new BrightcoveMediaController(brightcoveVideoView);

        // Add "Ad Markers" where the Ads Manager says ads will appear.
        mediaController.addListener(GoogleIMAEventType.ADS_MANAGER_LOADED, event -> {
            AdsManager manager = (AdsManager) event.properties.get("adsManager");
            if (manager != null) {
                List<Float> cuepoints = manager.getAdCuePoints();
                for (int i = 0; i < cuepoints.size(); i++) {
                    Float cuepoint = cuepoints.get(i);
                    BrightcoveSeekBar brightcoveSeekBar = mediaController.getBrightcoveSeekBar();
                    // If cuepoint is negative it means it is a POST ROLL.
                    int markerTime = cuepoint < 0 ? brightcoveSeekBar.getMax() : (int) (cuepoint * DateUtils.SECOND_IN_MILLIS);
                    mediaController.getBrightcoveSeekBar().addMarker(markerTime);

                }
            }
        });
        videoView.setMediaController(mediaController);

    }

    private void sourceSelectionFilter(@NonNull DeliveryType deliveryTypeFilter, @NonNull Video video) {
        if (video.getSourceCollections().containsKey(deliveryTypeFilter)) {
            // Only remove the other delivery types if the desired type is in our Source collections
            for (DeliveryType deliveryType : DeliveryType.values()) {
                if (!deliveryType.equals(deliveryTypeFilter)) {
                    video.getSourceCollections().remove(deliveryType);
                }
            }
        } else {
            Log.v(TAG, "The specified source type was not found: " + deliveryTypeFilter.name());
        }
    }

}
