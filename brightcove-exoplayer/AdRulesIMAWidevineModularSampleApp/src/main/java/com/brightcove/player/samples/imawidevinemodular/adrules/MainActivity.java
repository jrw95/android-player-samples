package com.brightcove.player.samples.imawidevinemodular.adrules;

import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.Constants;
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.HttpRequestConfig;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * This app illustrates how to use "Ad Rules" with the Google IMA
 * plugin, the Widevine plugin, and the Brightcove Player for Android.
 * <p>
 * Note: Video cue points are not used with IMA Ad Rules. The AdCuePoints referenced
 * in the setupAdMarkers method below are Google IMA objects.
 *
 * @author Paul Matthew Reilly (original code)
 * @author Paul Michael Reilly (added explanatory comments)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;
    private ExoPlayerVideoDisplayComponent videoDisplayComponent;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private GoogleIMAComponent googleIMAComponent;
    private String adRulesURL = "http://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=%2F15018773%2Feverything2&ciu_szs=300x250%2C468x60%2C728x90&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=dummy&correlator=[timestamp]&cmsid=133&vid=10XWSh7W4so&ad_rule=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveExoPlayerVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);
        eventEmitter = brightcoveVideoView.getEventEmitter();
        videoDisplayComponent = (ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();

        // Use a procedural abstraction to setup the Google IMA SDK via the plugin.
        setupGoogleIMA();

        // Create the catalog object which will start and play the video.
        Catalog catalog = new Catalog.Builder(brightcoveVideoView.getEventEmitter(), getString(R.string.account))
                .setPolicy(getString(R.string.policy))
                .build();

        // Set the HttpRequestConfig with the Ad Config Id configured in
        // your https://studio.brightcove.com account.
        HttpRequestConfig httpRequestConfig = new HttpRequestConfig.Builder()
                .setBrightcoveAuthorizationToken("ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJwa2lkIjogImVhMGZhNDVlLTA5NjItNDk5My05YWNjLWRjYTJkZWEyZTVjZCIsCiAgInByaWQiOiAiNTU2OWM2YjEtN2I0ZS00MWMyLWI3YzktODE3OGMwYTYwYWRiIiwKICAiYWNjaWQiOiAiMzUzNzc4MjU2NTAwMSIsCiAgImlhdCI6IDE2MzY0OTQ2MzcsCiAgImV4cCI6IDE2MzkwODY2MzcsCiAgIm5iZiI6IDE2MzY0OTQ2MzcKfQ.ZDtNShtwma0QnEuNcuCD0ETydKpMqUYydtylNMCu1KgXGKqWJ5kfXJ3EWWN7CrZgqgD4r90J2kpAIJcT1I_eP2td7C7IHK8WVYt7K1epOCkACGV96ENJredVnzBtvp7kGzvoQD7uPf1SwWY69Hs5-zTlK4wj5GypG58FSs_Aqi2cbz-lIxeJQHwF2dbEyYPVuZO6Ka98yHyzuktHxCs2dTSShbF5NHYdapI0usZffMPZZ1JUNJ6ezi_ZGuC_e9velODW0YID_CjWpENGalm3EMhsyKAGZvHV6hSLt1nTyrUfUlD0Gdfq0XYBYNwPHdh4Rkc3SzxGPEfaDz5Lmg5VXg")
                .build();

        catalog.findVideoByID(getString(R.string.videoId), httpRequestConfig, new VideoListener() {
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);

                // Auto play: the GoogleIMAComponent will postpone
                // playback until the Ad Rules are loaded.
//                brightcoveVideoView.start();
            }

            @Override
            public void onError(@NonNull List<CatalogError> errors) {
                Log.e(TAG, "Could not load video: " + errors.toString());
            }
        });

//        eventEmitter.on(EventType.SET_SOURCE, event -> {
//            try {
//                // Get an instance of the MediaDrm from the device
//                MediaDrm mediaDrm = new MediaDrm(Constants.WIDEVINE_UUID);
//
//                // Create a new DefaultTrackSelector.ParamsBuilder object
//                DefaultTrackSelector.ParametersBuilder builder = new DefaultTrackSelector.ParametersBuilder(this);
//                // Allow selecting tracks across AdaptationSets (if multiple AdaptationSets are found in the manifest)
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
//                    // Set the max video size to SD when the MediaDrm reports the HDCP capability as "Unprotected"
//                    // or if it can't find it at all and returns null or the empty String
//                    builder.setMaxVideoSizeSd();
//                }
//
//                // Create the DefaultTrackSelector, and set the Parameters
//                DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector(this);
//                defaultTrackSelector.setParameters(builder.build());
//
//                // Set this DefaultTrackSelector object on the ExoPlayerVideoDisplayComponent
//                videoDisplayComponent.setTrackSelector(defaultTrackSelector);
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
    }

    /**
     * Setup the Brightcove IMA Plugin.
     */
    private void setupGoogleIMA() {
        // Establish the Google IMA SDK factory instance.
        final ImaSdkFactory sdkFactory = ImaSdkFactory.getInstance();

        // Enable logging up ad start.
        eventEmitter.on(EventType.AD_STARTED, event -> Log.v(TAG, event.getType()));

        // Enable logging any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD, event -> Log.v(TAG, event.getType()));

        // Enable Logging upon ad completion.
        eventEmitter.on(EventType.AD_COMPLETED, event -> Log.v(TAG, event.getType()));

        // Set up a listener for initializing AdsRequests. The Google
        // IMA plugin emits an ad request event as a result of
        // initializeAdsRequests() being called.
        eventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO, event -> {
            // Build an ads request object and point it to the ad
            // display container created above.
            AdsRequest adsRequest = sdkFactory.createAdsRequest();
            adsRequest.setAdTagUrl(adRulesURL);

            ArrayList<AdsRequest> adsRequests = new ArrayList<>(1);
            adsRequests.add(adsRequest);

            // Respond to the event with the new ad requests.
            event.properties.put(GoogleIMAComponent.ADS_REQUESTS, adsRequests);
            eventEmitter.respond(event);
        });

        // Create the Brightcove IMA Plugin and pass in the event
        // emitter so that the plugin can integrate with the SDK.
        googleIMAComponent = new GoogleIMAComponent.Builder(brightcoveVideoView, eventEmitter)
                .setUseAdRules(true)
                .build();

        Log.v(TAG, "googleIMAComponent PPID: " + googleIMAComponent.getImaSdkSettings().getPpid());

        // Calling GoogleIMAComponent.initializeAdsRequests() is no longer necessary.
    }
}
