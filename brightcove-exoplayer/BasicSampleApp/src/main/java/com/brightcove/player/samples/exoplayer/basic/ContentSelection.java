package com.brightcove.player.samples.exoplayer.basic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class ContentSelection extends Activity {

    private TextView contentTypeSelector;
    private TextView videoSelector;

    protected static final String CONTENT_TYPE = "contentType";
    protected static final String CONTENT_TYPE_CATALOG = "Catalog Video";
    protected static final String CONTENT_TYPE_VIDEO_OBJECT = "Video Object";
    private String contentTypeString;

    protected static final String INTENT_KEY_VIDEO_ID = "videoId";
    protected static final String INTENT_KEY_VIDEO_OBJECT = "videoObject";

    private String videoId;
    private VideoObjectInfo videoObjectInfo;

    private final List<VideoObjectInfo> videoInfos = Arrays.asList(
            VideoObjectInfo.create("Google Demo DRM Video", "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd", "https://proxy.uat.widevine.com/proxy?video_id=d286538032258a1c&provider=widevine_test"),
            VideoObjectInfo.create("RPeck Test DRM Audio-only 48 kHz", "https://manifest.prod.boltdns.net/manifest/v1/dash/live-baseurl/bccenc/4590388345001/d89ba0e4-680a-4ede-98a2-a9eb3746cbb1/6s/manifest.mpd?fastly_token=NjA2NGU4YmJfMDBlMDk0ZmFlMzlkY2U0ZTU2YzRlYzZjMTZjNzQzOGJmNGU2MTY4MzNlMjcwYmE4YTg1ODQ5Yjk0YWRkN2RlNQ==", "https://manifest.prod.boltdns.net/license/v1/cenc/widevine/4590388345001/d89ba0e4-680a-4ede-98a2-a9eb3746cbb1/0205fd25-ed5b-45f4-a750-a9891c001e92?fastly_token=NjA2NGYzZjBfODFlM2E0YzVkM2EwZTU5ODBhZDEwYjgwZDUyNjIzY2Y5MTE0ZDcyNmUzMmIxOGM0ODI4MTkyZGNkOGExY2JkMA=="),
            VideoObjectInfo.create("RPeck Test DRM Audio-only 24 Fps & 44.1 kHz", "https://manifest.prod.boltdns.net/manifest/v1/dash/live-baseurl/bccenc/4590388345001/696bb957-af46-4a3d-8698-17414ae510a4/6s/manifest.mpd?fastly_token=NjA2NTA2YmVfNjg5MmU5MDYwZmQxMjIzZTg1NjM5MGM5M2RiM2FhNGU4ODIzMmJlZTllMjIyMTQzMDIzY2ZlNDcxYTY3NjIyOA==", "https://manifest.prod.boltdns.net/license/v1/cenc/widevine/4590388345001/696bb957-af46-4a3d-8698-17414ae510a4/1bb17fe1-1409-4249-801b-2841cd2a2f5d?fastly_token=NjA2NTA3MWVfMmNmYTNiOTg1NTliM2YxZTU5OTAxNDJjMGRmOTQ4ZGJiMjU5MmI0MTc4YzQ5NDYxMzMzNTRkZmZjNmMxOTg5Yg=="),
            VideoObjectInfo.create("RPeck Test Clear Audio-only 48 kHz", "https://manifest.prod.boltdns.net/manifest/v1/dash/live-baseurl/clear/4590388345001/d89ba0e4-680a-4ede-98a2-a9eb3746cbb1/6s/manifest.mpd?fastly_token=NjA2NGU3OGNfZWQ3ZGRkZTdhNDc0ODk5ZjBjMmE2MDIyMWRlY2U3NmJhMTYwYjdhNDE4N2ZlZTk3NDNmZDljYzUzYWM1NDljNQ=="),
            VideoObjectInfo.create("RPeck Test Clear Audio-only 24 Fps & 44.1 kHz", "https://manifest.prod.boltdns.net/manifest/v1/dash/live-baseurl/clear/4590388345001/696bb957-af46-4a3d-8698-17414ae510a4/6s/manifest.mpd?fastly_token=NjA2NTA2NzhfYzUxYjVhY2JkN2UxMGEzOGQwMjVjNDNhNDQyOTA5NDI3NmQ4NmZhZjFhNDlhMDdhMzUyYmJkYzViOWYyYTczNw==")
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_content_selection);

        contentTypeSelector = findViewById(R.id.contentType);
        contentTypeSelector.setVisibility(View.VISIBLE);
        contentTypeSelector.setOnClickListener(v -> showContentTypeDialog());

        videoSelector = findViewById(R.id.videoId);
        videoSelector.setVisibility(View.VISIBLE);

        Button btnLoadPlayer = findViewById(R.id.buttonLoadPlayer);
        btnLoadPlayer.setOnClickListener(arg0 -> loadPlayer(arg0));
    }

    private void showContentTypeDialog() {
        String[] contentTypeLabels = {CONTENT_TYPE_CATALOG, CONTENT_TYPE_VIDEO_OBJECT};

        PopupMenu contentTypePopupMenu = new PopupMenu(ContentSelection.this, contentTypeSelector);
        for (int i = 0; i < contentTypeLabels.length; i++) {
            contentTypePopupMenu.getMenu().add(i, i, i, contentTypeLabels[i]);
        }
        contentTypePopupMenu.setOnMenuItemClickListener(item -> {
            contentTypeString = item.getTitle().toString();
            if (CONTENT_TYPE_CATALOG.equalsIgnoreCase(contentTypeString)) {
                showVideoIdDialog();
            }
            else {
                showVideoObjectDialog();
            }
            contentTypeSelector.setText("Content Type: " + contentTypeString);
            return false;
        });
        contentTypePopupMenu.show();
    }

    private void showVideoIdDialog() {
        String[] videoIdLabels = {"6220854770001", "6220608951001", "6234949393001", "6236665198001"};

        PopupMenu videoIdPopupMenu = new PopupMenu(ContentSelection.this, videoSelector);
        for (int i = 0; i < videoIdLabels.length; i++) {
            videoIdPopupMenu.getMenu().add(i, i, i, videoIdLabels[i]);
        }
        videoIdPopupMenu.setOnMenuItemClickListener(item -> {
            videoId = item.getTitle().toString();
            videoSelector.setText("Catalog Video ID: " + videoId);
            return false;
        });
        videoIdPopupMenu.show();
    }

    private void showVideoObjectDialog() {
        String[] videoObjectLabels = {
                videoInfos.get(0).getName(),
                videoInfos.get(1).getName(),
                videoInfos.get(2).getName(),
                videoInfos.get(3).getName(),
                videoInfos.get(4).getName()
        };

        PopupMenu videoObjectPopupMenu = new PopupMenu(ContentSelection.this, videoSelector);
        for (int i = 0; i < videoObjectLabels.length; i++) {
            videoObjectPopupMenu.getMenu().add(i, i, i, videoObjectLabels[i]);
        }
        videoObjectPopupMenu.setOnMenuItemClickListener(item -> {
            videoObjectInfo = videoInfos.get(item.getItemId());
            videoSelector.setText("Video Object: " + videoObjectInfo.getName());
            return false;
        });
        videoObjectPopupMenu.show();
    }

    public void loadPlayer(View view) {
        Intent playerIntent = new Intent(getApplicationContext(), MainActivity.class);

        playerIntent.putExtra(CONTENT_TYPE, contentTypeString);
        playerIntent.putExtra(INTENT_KEY_VIDEO_ID, videoId);
        playerIntent.putExtra(INTENT_KEY_VIDEO_OBJECT, videoObjectInfo);
        startActivity(playerIntent);
    }

}
