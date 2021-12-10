package com.brightcove.player.samples.exoplayer.basic;

import android.os.Bundle;
import android.util.Log;
import android.util.Rational;

import androidx.annotation.NonNull;

import com.brightcove.player.concurrency.ConcurrencyClient;
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.edge.BrightcoveTokenAuthorizer;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.HttpRequestConfig;
import com.brightcove.player.pictureinpicture.PictureInPictureManager;
import com.brightcove.player.render.TrackSelectorHelper;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * This app illustrates how to use the ExoPlayer with the Brightcove
 * Native Player SDK for Android.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();
    private ExoPlayerVideoDisplayComponent videoDisplayComponent;
    // EPA Clear
    private final String clearJwt1 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICI0ODAwMjY2ODQ5MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTEiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUwMTIzLAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTgxMjMKfQ.iPfqFkJXw1XPs8wdIIUX1MZjpDgDukt1aSsb9j159zu-Bn3K7FPAGR6ERA91jya4dRKXrTbqk5YKnlepZlz8rT7ysRGsUpgHo_KRFhmYs5DrWWl8ie50yERHKnna7NZneYUjzQ9i-_vJrf9FS67qnLGL2CgtmC3WZrKPERJ5x13DQFHMoI9wQDEBbuA9hj6urEUTGwpxmMJvd-2NaCsCkA5AUgnvYgBLkAwwDNHuoZeX_f6sfv45_SoTamaNDYPN9rBWV-usn_MQDvDTSrjnH9R9pKFVFdWoIFRDJXUdMLmUam5E4MAd01iSh2lZ6v6QMs2x04aSob4v2MJsaXcB9A";
    private final String clearJwt2 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICI0ODAwMjY2ODQ5MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTIiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUwMjE1LAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTgyMTUKfQ.OoQJ91yPDQTsNjB8YKd7wJR-3yTLX9KY4OjERqojW7r2-By4vfO8yzggP2UFqri4SWKGbMBRWms8VVxX6gctWm_9Vt6fgZ2YIMJxBh_DwwN3QepxGWebPsRcbwmc6YlVFhWzChx58679KwfS6jZTdFoZ0ORXDRmAaFrsZZZRc5Ye17VzF8koTMolNMiFKZhA0GzsYfWrbrCXSHMtExqOkdPHntGxOmOIBEBf3pZgEPykSbve3qfB8u5U73PCkOux1z7F25K_jfXdyHHCjFY_w177uiOOo1ySCAlcK2CkMWIkNSsB-MAiNq9_brwVNWz8WpgwgHCm9PRnoDnojr2dWg";
    // EPA DRM
    private final String drmJwt1 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICIzNjM2MzM0MTY0MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTEiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUwNzUxLAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTg3NTEKfQ.wF7KuVHppTMewwFkfnbEGgao8CRM5rcL40wbcT6zSF9SiqGuRHyGCBnMfR1ButOjug9-vpdp8A738GTs5MDjmOOXDTYGCnTrgDofFN9hLTSpvkLiUF7l9Q36srlnyN_4iZN6JHDFOk7EDdntgLyu7ll5t_5NpAXl5EDHV3CAMDLAV5zOLdlXr4E3LlFdV4F-06ZzJmJxn4PYxbnY9dbvQNTsZp0XKWnzv1xSBNYvhpJIHgwPpvG2enDvuPZA_-ZF7tSsVQPYh5dox-blfQeC6j6Gj_r7aUzLV_bi-RroLV5jYsvliDnXOWVjB9BdNL-Oc2LxIS_a_WduoMLcEV8uxw";
    private final String drmJwt2 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICIzNjM2MzM0MTY0MDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTIiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MjUxMDA4LAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY2NTkwMDgKfQ.oZYFr2ScuuC74Xj9cIr8RVEtcBO_tufqsLqhaQJV8B9eLuc93y0cQypYWUNRueYiuRwvCQ1vSILCyozEx30ROhPZ05oLrUAT7DBcfbuBBrYE0oJ5BISd0yGw6LFX8UtHKAzKnacBLry0i5DQedvpz17bvQ3gFvTyqpxT_3Bt0YOsBuKWsq03Xxy8WSQ8bzSAsO76FH38nlwykvEQ5s_yUgTRG0oHpq8KPAjy0n1Qj9EIi_ImgNJ_8q2c9H2gPKFZDTDBRW3Tz_9sY6X0ykV0Gn1BL-p21tr2UKuruNlUNa5-pjc29L2NcqfYsqbNz8Hj0Rthu7agEIEtZWo8mb10PA";
    // EPA HLSe
    private final String hlseJwt1 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICI1NTA3Nzc4NzYzMDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTEiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MzI4MTQ3LAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY3MzYxNDcKfQ.LcY9Ickl8cz2mUFMk1bFdqtKNsW2XoEVP6SevagSiyfgGTfglx77pTasRObRn-dvJaBC8A2ODGPmWm40O6UiebTVy9yHIdY8f-k74eQRbpFDIXXp56ngcGBLwOYgnYyEhFuhFy3lfFU2TVv1A6QDk21MKbFtJQNuAZxZO180B_xxHtssM3yP0JwGBTU9xc-sWf4PDmAz06Uaf1XfinuxPQDRVLTo4_4BKR96GxElFqqgbpTuwrbW7mr8vFlA-7WxsTsdJQT-3Pai-Nv6nTwa78lPqsegdF267ky3jEC6yI9QZZC87TgipLYBsIfN4EVJWNH7hP4iqXgq46vZ0lWdbg";
    private final String hlseJwt2 = "ewoJInR5cGUiOiAiSldUIiwKCSJhbGciOiAiUlMyNTYiCn0.ewogICJjbGltaXQiOiAxLAogICJhY2NpZCI6ICI1NTA3Nzc4NzYzMDAxIiwKICAic2lkIjogImFuZHJvaWRfbmF0aXZlX3Nka190ZXN0aW5nX3VzYTIiLAogICJ1aWQiOiAiYW5kcm9pZF9zZGtfdGVzdGluZ191c2EiLAogICJleHAiOiAxNjM5MzI4MTg2LAogICJjYmVoIjogIkJMT0NLX05FVyIsCiAgImlhdCI6IDE2MzY3MzYxODYKfQ.ts2nzxpExHYQU06BIvhOEQn0BO76tILN_ibgJwlkgBuRcCdWFBJKKy_KTW_THKOPgii4XNg6tJ7hnh-HHt3x3FZcm1F5Cbh-kq1CFlhpiZAHaO3gbl_TU7mXFreLo8VLyj_zh-XYgB2lYSWcGpD2_8EVwjCPzmiOjdvyFs6AYcTAtwyqwCHZ6A25DV-eNbOZCqx503fFpGufRAn-YD2MxxGxHMaijwzwV_8buo0AtjiRXjj0DA_vfRPi-l_PIaUwDtTZzxLcZQLVBGIazEoVzsmp_XVdVC5vImPaD8oj2g5mBgO2kU0NeMNMycXZ1hQULm3d4_ZjTQCztI4142janQ";


    @Override
    @SuppressWarnings("WrongConstant")
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);

        super.onCreate(savedInstanceState);

        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        brightcoveVideoView.setStreamConcurrencyEnabled(true);
        brightcoveVideoView.setStreamConcurrencySessionsListener(sessionsList ->
                Log.v(TAG, "Stream Concurrency. Get Active Sessions: " + sessionsList.toString()));
        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        String account = getString(R.string.account);
        videoDisplayComponent = (ExoPlayerVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();

        Catalog catalog = new Catalog.Builder(eventEmitter, account)
                .setPolicy(getString(R.string.policy))
                .build();

//        Catalog catalog = new Catalog(eventEmitter, account, getString(R.string.policy), Catalog.DEFAULT_EPA_BASE_URL);

        // Set the HttpRequestConfig with the Ad Config Id configured in
        // your https://studio.brightcove.com account.
        HttpRequestConfig httpRequestConfig = new HttpRequestConfig.Builder()
                .setBrightcoveAuthorizationToken(drmJwt2)
                .build();

        catalog.findVideoByReferenceID(getString(R.string.videoRefId), httpRequestConfig, new VideoListener() {
            @Override
            public void onVideo(Video video) {
                Log.v(TAG, "onVideo: video = " + video);
//                sourceSelectionFilter(DeliveryType.HLS, video);
                brightcoveVideoView.add(video);
//                brightcoveVideoView.start();
            }
        });

        eventEmitter.once(EventType.DID_SET_VIDEO, event -> {
            Video video = (Video) event.getProperties().get(Event.VIDEO);
            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put(ConcurrencyClient.HEARTBEAT_VIDEO_HEADER_KEY, video.getId());
            requestHeaders.put(ConcurrencyClient.HEARTBEAT_ACCOUNTID_HEADER_KEY, account);
            requestHeaders.put(BrightcoveTokenAuthorizer.BRIGHTCOVE_AUTHORIZATION_HEADER_KEY, drmJwt2);
            brightcoveVideoView.setStreamConcurrencyRequestHeaders(requestHeaders);
        });

//        eventEmitter.on(EventType.ANALYTICS_VIDEO_ENGAGEMENT, event -> {
//            TrackSelectorHelper trackSelectorHelper = videoDisplayComponent.getTrackSelectorHelper();
//            Log.v(TAG, "Available Formats: " + trackSelectorHelper.getAvailableFormatList(ExoPlayerVideoDisplayComponent.TYPE_VIDEO));
//        });

        //Configure Picture in Picture
        PictureInPictureManager manager = PictureInPictureManager.getInstance();
        manager.setClosedCaptionsEnabled(true)
                .setOnUserLeaveEnabled(true)
                .setClosedCaptionsReductionScaleFactor(1.0f);
    }

    public void sourceSelectionFilter(@NonNull DeliveryType deliveryTypeFilter, @NonNull Video video) {
        if (video.getSourceCollections().containsKey(deliveryTypeFilter)) {
            // Only remove the other delivery types if the desired type is in our Source collections
            for (DeliveryType deliveryType : DeliveryType.values()) {
                if (!deliveryType.equals(deliveryTypeFilter)) {
                    video.getSourceCollections().remove(deliveryType);
                }
            }
        } else {
            Log.v(this.getClass().getSimpleName(), "The specified source type was not found: " + deliveryTypeFilter.name());
        }
    }
}
