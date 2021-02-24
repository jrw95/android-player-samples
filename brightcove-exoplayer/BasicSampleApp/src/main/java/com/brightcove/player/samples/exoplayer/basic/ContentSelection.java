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

    protected static final String CONTENT_TYPE = "contentType";
    protected static final String CONTENT_TYPE_CATALOG = "Catalog Video";
    protected static final String CONTENT_TYPE_VIDEO_OBJECT = "Video Object";
    private String contentTypeString;

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
            contentTypeSelector.setText("Content Type: " + itemTitle);
            return false;
        });
        popupMenu.show();
    }

    public void loadPlayer(View view) {
        Intent playerIntent = new Intent(getApplicationContext(), MainActivity.class);

        playerIntent.putExtra(CONTENT_TYPE, contentTypeString);
        startActivity(playerIntent);
    }

}
