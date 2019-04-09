package com.example.newmedialab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public void openMyExperiments(View view) {
        Intent startShapes = new Intent(MainActivity.this, MyExperiments.class);
        startActivity(startShapes);
    }

    public void createNewExperiment(View view) {
        Intent startShapes = new Intent(MainActivity.this, ExperimentType.class);
        startShapes = startShapes.putExtra("editing", false);
        startActivity(startShapes);
    }

    public void openTestActivity(View view) {
        Intent startShapes = new Intent(MainActivity.this, playVideo.class);
        startActivity(startShapes);
    }


    public class MyActivity extends Activity {
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createDirectory();

        //Initialize FFmpeg, run once at start of application:
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {}

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }


    }

    public void createDirectory(){

        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "./KineTest/Experiments");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            // Do nothing
        } else {
            // Create directory
            new File("./KineTest/Experiments").mkdirs();
        }

    }

}
