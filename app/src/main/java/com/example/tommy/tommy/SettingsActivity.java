package com.example.tommy.tommy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.List;

/**
 * Settings page activity.
 * Transfers to:
 * <ul>
 * <li>Home page, by clicking the back button </li>
 * </ul>
 */

public class SettingsActivity extends AppCompatActivity {
    private Intent intent;
    private List<String> voices;
    private String selectedVoice;
    private boolean isMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        intent = getIntent();
        voices = intent.getStringArrayListExtra("voices");
        selectedVoice = intent.getStringExtra("current_voice");
        isMute = intent.getBooleanExtra("is_mute", false);
        // set mute toggle button according to the specified is_mute
        ToggleButton tbMute = (ToggleButton) findViewById(R.id.tbMute);
        tbMute.setChecked(isMute);
        tbMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isMute = isChecked;
            }
        });
        // fill spinner with all available voices
        Spinner sVoices = (Spinner) findViewById(R.id.sVoices);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, voices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sVoices.setAdapter(adapter);
        // set selected voice if exists in list
        int selectedVoiceIndex = voices.indexOf(selectedVoice);
        if (selectedVoiceIndex != -1) {
            sVoices.setSelection(selectedVoiceIndex);
        }

        sVoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedVoice = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * Terminates the activity when clicking the back button.
     * Sends the selected voice and mute setup back to the home activity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                intent.putExtra("selected_voice", selectedVoice);
                intent.putExtra("is_mute", isMute);
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
