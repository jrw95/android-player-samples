package com.brightcove.player.samples.exoplayer.basic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

public class ContentSelection extends Activity {

    private TextView contentTypeSelector;
    private TextView videoIdSelector;

    protected static final String CONTENT_TYPE = "contentType";
    protected static final String CONTENT_TYPE_CATALOG = "Catalog Video";
    protected static final String CONTENT_TYPE_VIDEO_OBJECT = "Video Object";
    private String contentTypeString;

    protected static final String VIDEO_ID = "videoId";
    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_content_selection);

        contentTypeSelector = (TextView) findViewById(R.id.contentType);
        contentTypeSelector.setVisibility(View.VISIBLE);
        contentTypeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContentTypeDialog();
            }
        });

        videoIdSelector = (TextView) findViewById(R.id.videoId);
        videoIdSelector.setVisibility(View.INVISIBLE);
        videoIdSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoIdDialog();
            }
        });


        Button btnLoadPlayer = findViewById(R.id.buttonLoadPlayer);
        btnLoadPlayer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                loadPlayer(arg0);
            }
        });
    }

    private void showContentTypeDialog() {
        String[] contentTypeLabels = {CONTENT_TYPE_CATALOG, CONTENT_TYPE_VIDEO_OBJECT};

        PopupMenu popupMenu = new PopupMenu(ContentSelection.this, contentTypeSelector);
        for (int i = 0; i < contentTypeLabels.length; i++) {
            popupMenu.getMenu().add(i, i, i, contentTypeLabels[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            CharSequence itemTitle = item.getTitle();
            contentTypeString = itemTitle.toString();
            if (CONTENT_TYPE_CATALOG.equalsIgnoreCase(contentTypeString)) {
                videoIdSelector.setVisibility(View.VISIBLE);
                showVideoIdDialog();
            }
            else {
                videoIdSelector.setVisibility(View.INVISIBLE);
            }
            contentTypeSelector.setText("Content Type: " + contentTypeString);
            return false;
        });
        popupMenu.show();
    }

    private void showVideoIdDialog() {
        String[] videoIdLabels = {"6220854770001", "6220608951001", "6234949393001"};

        PopupMenu popupMenu = new PopupMenu(ContentSelection.this, videoIdSelector);
        for (int i = 0; i < videoIdLabels.length; i++) {
            popupMenu.getMenu().add(i, i, i, videoIdLabels[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            CharSequence itemTitle = item.getTitle();
            videoId = itemTitle.toString();
            videoIdSelector.setText("Catalog Video ID: " + videoId);
            return false;
        });
        popupMenu.show();
    }

    public void loadPlayer(View view) {
        Intent playerIntent = new Intent(getApplicationContext(), MainActivity.class);

        playerIntent.putExtra(CONTENT_TYPE, contentTypeString);
        playerIntent.putExtra(VIDEO_ID, videoId);
        startActivity(playerIntent);
    }

}
