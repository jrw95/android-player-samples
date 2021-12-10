package com.brightcove.player.samples.freewheelwidevinemodular.basic;

import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.brightcove.freewheel.controller.FreeWheelController;
import com.brightcove.freewheel.event.FreeWheelEventType;
import com.brightcove.player.Constants;
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.HttpRequestConfig;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import java.util.List;

import tv.freewheel.ad.interfaces.IAdContext;
import tv.freewheel.ad.interfaces.IConstants;
import tv.freewheel.ad.interfaces.ISlot;
import tv.freewheel.ad.request.config.AdRequestConfiguration;
import tv.freewheel.ad.request.config.NonTemporalSlotConfiguration;
import tv.freewheel.ad.request.config.TemporalSlotConfiguration;
import tv.freewheel.ad.request.config.VideoAssetConfiguration;

/**
 * This app illustrates how to use the FreeWheel and Widevine plugins
 * together with the Brightcove Player for Android.
 *
 * @author Billy Hnath
 * @author Sergio Martinez
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;
    private ExoPlayerVideoDisplayComponent videoDisplayComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveExoPlayerVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.freewheel_activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        setupFreeWheel();

        Catalog catalog = new Catalog.Builder(brightcoveVideoView.getEventEmitter(), getString(R.string.account))
                .setPolicy(getString(R.string.policy))
                .build();

        // Set the HttpRequestConfig with the Ad Config Id configured in
        // your https://studio.brightcove.com account.
        HttpRequestConfig httpRequestConfig = new HttpRequestConfig.Builder()
//                .setBrightcoveAuthorizationToken("ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJwa2lkIjogImVhMGZhNDVlLTA5NjItNDk5My05YWNjLWRjYTJkZWEyZTVjZCIsCiAgImFjY2lkIjogIjM1Mzc3ODI1NjUwMDEiLAogICJ1aWQiOiAiYW5kcm9pZC1zZGstdGVzdGluZyIsCiAgInByaWQiOiAiNTU2OWM2YjEtN2I0ZS00MWMyLWI3YzktODE3OGMwYTYwYWRiIiwKICAiY2xpbWl0IjogMiwKICAiaWF0IjogMTYzNzE2MjA5MywKICAiZXhwIjogMTYzOTc1NDA5MywKICAibmJmIjogMTYzNzE2MjA5Mwp9.CLoiBBcAoczuKtrl1fJsqFx2EdbBuaiuroUTUiCngP395c19h1pZLgb0HTWkb9i25N1a2xCTigDbR5baQ9lvvE-e91RcxEt8v4z3nB-gyZz8lAhZkyt3DY0g8-jg6GXqcacZ5mTARBQg4CoMF4zteMOx4YK5NizPEiPXoRoj0F0Hb2dVWtOK7kb-M-VAaNV_cyH3Bszwto0ykSX5b-sLIuy-Mip4XBM3kVRkarWKOQzvqHgLq25CwF90vBMhkFKK9eINIjC90W1L3Nsl6e8r434Lk_HsDALRlFX5QY4Dad_YaPrlqSMFK9Jib6O-datfgSnUgoLsTzrmEU_Au9Bb-g")
                .build();

        catalog.findVideoByID(getString(R.string.videoId), httpRequestConfig, new VideoListener() {
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }

            @Override
            public void onError(String s) {
                Log.e(TAG, "Could not load video: " + s);
            }
        });

//        eventEmitter.on(EventType.SET_SOURCE, event -> {
//            try {
//                // Get an instance of the MediaDrm from the device
//                MediaDrm mediaDrm = new MediaDrm(Constants.WIDEVINE_UUID);
//
//                // Get the values for hdcpLevel and maxHdcpLevel
//                String connectedHdcpLevel = mediaDrm.getPropertyString("hdcpLevel");
//                String maxHdcpLevel = mediaDrm.getPropertyString("maxHdcpLevel");
//
//                Log.v(TAG, "HDCP Level: " + connectedHdcpLevel + " Max HDCP Level: " + maxHdcpLevel);
//
//                // If either level reads "Unprotected"
//                if ("Unprotected".equals(connectedHdcpLevel) || "Unprotected".equals(maxHdcpLevel)) {
//                    Log.v(TAG, "Restricting rendition selection to SD");
//                    // Get the ExoPlayerVideoDisplayComponent
//                    videoDisplayComponent = (ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();
//                    // Create a new DefaultTrackSelector.ParamsBuilder object, and call setMaxVideoSizeSd()
//                    DefaultTrackSelector.ParametersBuilder builder = new DefaultTrackSelector.ParametersBuilder(this);
//                    builder.setMaxVideoSizeSd();
//                    // Create a new DefaultTrackSelector object, and set the Parameters object created above
//                    DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector(this);
//                    defaultTrackSelector.setParameters(builder.build());
//                    // Set this DefaultTrackSelector object on the ExoPlayerVideoDisplayComponent
//                    videoDisplayComponent.setTrackSelector(defaultTrackSelector);
//                    Log.v(TAG, "Should have restricted rendition selection to SD");
//                }
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

    private void setupFreeWheel() {

        //change this to new FrameLayout based constructor.
        FreeWheelController freeWheelController = new FreeWheelController(this, brightcoveVideoView, eventEmitter);
        //configure your own IAdManager or supply connection information
        freeWheelController.setAdURL("https://demo.v.fwmrm.net/");
        freeWheelController.setAdNetworkId(90750);
        freeWheelController.setProfile("3pqa_android");

        /*
         * Choose one of these to determine the ad policy (basically server or client).
         * - 3pqa_section - uses FW server rules - always returns a preroll and a postroll.  It should return whatever midroll slots you request though.
         * - 3pqa_section_nocbp - returns the slots that you request.
         */
        //freeWheelController.setSiteSectionId("3pqa_section");
        freeWheelController.setSiteSectionId("3pqa_section_nocbp");

        eventEmitter.on(FreeWheelEventType.SHOW_DISPLAY_ADS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                @SuppressWarnings("unchecked")
                List<ISlot> slots = (List<ISlot>) event.properties.get(FreeWheelController.AD_SLOTS_KEY);
                ViewGroup adView = findViewById(R.id.ad_frame);

                // Clean out any previous display ads
                for (int i = 0; i < adView.getChildCount(); i++) {
                    adView.removeViewAt(i);
                }

                for (ISlot slot : slots) {
                    adView.addView(slot.getBase());
                    slot.play();
                }
            }
        });

        eventEmitter.on(FreeWheelEventType.WILL_SUBMIT_AD_REQUEST, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Video video = (Video) event.properties.get(Event.VIDEO);
                IAdContext adContext = (IAdContext) event.properties.get(FreeWheelController.AD_CONTEXT_KEY);
                IConstants adConstants = adContext.getConstants();
                AdRequestConfiguration adRequestConfiguration =
                        (AdRequestConfiguration) event.properties.get(FreeWheelController.AD_REQUEST_CONFIGURATION_KEY);

                // This overrides what the plugin does by default for setVideoAsset() which is to pass in currentVideo.getId().
                VideoAssetConfiguration fwVideoAssetConfiguration = new VideoAssetConfiguration(
                        "3pqa_video",
                        IConstants.IdType.CUSTOM,
                        //FW uses their duration as seconds; Android is in milliseconds
                        video.getDuration()/1000,
                        IConstants.VideoAssetDurationType.EXACT,
                        IConstants.VideoAssetAutoPlayType.ATTENDED);
                adRequestConfiguration.setVideoAssetConfiguration(fwVideoAssetConfiguration);

                NonTemporalSlotConfiguration companionSlot = new NonTemporalSlotConfiguration("300x250slot", null, 300, 250);
                companionSlot.setCompanionAcceptance(true);
                adRequestConfiguration.addSlotConfiguration(companionSlot);

                // Add preroll
                Log.v(TAG, "Adding temporal slot for prerolls");
                TemporalSlotConfiguration prerollSlot = new TemporalSlotConfiguration("larry", adConstants.ADUNIT_PREROLL(), 0);
                adRequestConfiguration.addSlotConfiguration(prerollSlot);

                // Add midroll
                Log.v(TAG, "Adding temporal slot for midrolls");

                int midrollCount = 1;
                int segmentLength = (video.getDuration() / 1000) / (midrollCount + 1);

                TemporalSlotConfiguration midrollSlot;
                for (int i = 0; i < midrollCount; i++) {
                    midrollSlot = new TemporalSlotConfiguration("moe" + i, adConstants.ADUNIT_MIDROLL(), segmentLength * (i + 1));
                    adRequestConfiguration.addSlotConfiguration(midrollSlot);
                }

                // Add postroll
                Log.v(TAG, "Adding temporal slot for postrolls");
                TemporalSlotConfiguration postrollSlot = new TemporalSlotConfiguration("curly", adConstants.ADUNIT_POSTROLL(), video.getDuration() / 1000);
                adRequestConfiguration.addSlotConfiguration(postrollSlot);

                // Add overlay
                Log.v(TAG, "Adding temporal slot for overlays");
                TemporalSlotConfiguration overlaySlot = new TemporalSlotConfiguration("shemp", adConstants.ADUNIT_OVERLAY(), 8);
                adRequestConfiguration.addSlotConfiguration(overlaySlot);
            }
        });
        freeWheelController.enable();
    }
}
