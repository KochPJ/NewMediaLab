package com.example.newmedialab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public void openMyExperiments(View view) {
        Intent startShapes = new Intent(MainActivity.this, MyExperiments.class);
        startActivity(startShapes);
    }

    public void createNewExperiment(View view) {
        Intent startShapes = new Intent(MainActivity.this, NewExperiments.class);
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
