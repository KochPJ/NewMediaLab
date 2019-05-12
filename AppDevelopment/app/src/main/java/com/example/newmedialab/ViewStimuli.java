package com.example.newmedialab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.VideoView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class ViewStimuli extends AppCompatActivity {

    public Experiment exp;
    public Button play_button;
    public ImageView seen, notseen;
    public VideoView vv;
    public Spinner seenspin, notseenspin, videospinner;
    public Uri sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stimuli);

        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");

        seen = findViewById(R.id.imageView7);
        notseen = findViewById(R.id.imageView8);
        vv = findViewById(R.id.videoView3);
        play_button = findViewById(R.id.play_button);

        ArrayList<String> videos = new ArrayList<String>();
        ArrayList<String> seenImages = new ArrayList<String>();
        ArrayList<String> notseenImages = exp.getFalseStimuli();
        for (String[] row : exp.getStimuli()) {
            videos.add(row[0]);
            videos.add(row[1]);
            seenImages.add(row[2]);
            seenImages.add(row[3]);
        }

        // Fill spinner dynamically
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, seenImages);

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        seenspin = findViewById(R.id.spinner_seen);
        seenspin.setAdapter(adapter);

        seenspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Uri ssi = Uri.parse((String) seenspin.getSelectedItem());
                seen.setImageURI(ssi);
            }
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        // Fill spinner dynamically
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, notseenImages);

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        notseenspin = findViewById(R.id.spinner_notseen);
        notseenspin.setAdapter(adapter2);

        notseenspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Uri nsi = Uri.parse((String) notseenspin.getSelectedItem());
                seen.setImageURI(nsi);
            }
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        // Fill spinner dynamically
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, videos);

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        videospinner = findViewById(R.id.spinner_video);
        videospinner.setAdapter(adapter3);

        videospinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                sv = Uri.parse((String) videospinner.getSelectedItem());
            }
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    public void playStimuli(View view) {
        if (!vv.isPlaying()) {
            if (exp.getCurrentID() % 2 == 0) {
                //Subject is part of the control group and gets the artificial stimuli
                vv.setVideoURI(sv);
                vv.start();
            }
        }
    }
}
