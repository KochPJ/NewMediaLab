package com.example.newmedialab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

public class playVideo extends AppCompatActivity {
    VideoView videov;


    public void playVideo(View view) throws InterruptedException {

        Log.d("Vidoe", "playing the video");
        String videopath ="android.resource://" + getPackageName() + "/" + R.raw.test;
        Uri uri =  Uri.parse(videopath);
        videov = (VideoView) findViewById(R.id.videoView);
        videov.setVideoURI(uri);
        videov.start();

        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
    }
}
