package com.brightcove.player.samples.ima.adrules;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class TestOptionsActivity extends Activity {

    public static final String CONTENT_TYPE = "testContentType";
    public static final String USE_HLS_IF_AVAILABLE = "useHlsIfAvailable";

    String testContentType;
    boolean useHlsIfAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_options);

        configureTestContentTypeSpinner();
        configureUseHlsCheckBox();

        Button btnLoadPlayer = findViewById(R.id.buttonLoadPlayer);
        btnLoadPlayer.setOnClickListener(arg0 -> loadPlayer(arg0));

    }

    public void loadPlayer(View view) {

        Intent playerOptionsIntent = new Intent(getApplicationContext(), MainActivity.class);

        playerOptionsIntent.putExtra(CONTENT_TYPE, testContentType);
        if (testContentType.equals(getString(R.string.hlse))) {
            useHlsIfAvailable = true;
        }
        playerOptionsIntent.putExtra(USE_HLS_IF_AVAILABLE, useHlsIfAvailable);

        startActivity(playerOptionsIntent);
    }

    private void configureTestContentTypeSpinner() {
        Spinner spnrTestContentType = findViewById(R.id.spinnerTestContent);
        ArrayAdapter<CharSequence> spnrTestContentTypeAdapter = ArrayAdapter.createFromResource(this, R.array.content_types, android.R.layout.simple_spinner_item);
        spnrTestContentTypeAdapter.setDropDownViewResource(R.layout.pbt_simple_spinner_item);
        spnrTestContentType.setAdapter(spnrTestContentTypeAdapter);
        spnrTestContentType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                testContentType = (String) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                testContentType = (String) parent.getSelectedItem();
            }
        });
    }

    private void configureUseHlsCheckBox() {
        CheckBox chkBoxUseHlsIfAvailable = findViewById(R.id.checkBoxUseHlsIfAvailable);
        chkBoxUseHlsIfAvailable.setVisibility(View.VISIBLE);
        chkBoxUseHlsIfAvailable.setEnabled(true);

        chkBoxUseHlsIfAvailable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.v(TestOptionsActivity.class.getSimpleName(), "The next player will use HLS sources if they are available.");
                useHlsIfAvailable = true;
            } else {
                Log.v(TestOptionsActivity.class.getSimpleName(), "The next player will not use HLS sources if they are available.");
                useHlsIfAvailable = false;
            }
        });
    }

}
