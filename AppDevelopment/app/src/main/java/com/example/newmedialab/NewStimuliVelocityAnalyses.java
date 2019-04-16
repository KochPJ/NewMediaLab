package com.example.newmedialab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class NewStimuliVelocityAnalyses extends AppCompatActivity {

    VideoView videoView;
    Video video_object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("here", "Here");
        super.onCreate(savedInstanceState);
        Log.d("here", "Here1");
        setContentView(R.layout.activity_new_stimuli_velocity_analyses);
        Log.d("here", "Here2");
        videoView = (VideoView)findViewById(R.id.NewStimuli_videoViewNewStimuli);
        Log.d("here", "Here3");
        Intent i = getIntent();
        Log.d("here", "Here4");
        this.video_object = (Video)i.getSerializableExtra("video_object");
        Log.d("here", "Here5");
    }

    public void getVelocityProfile(View view){

        String imageDir = "KineTest/Resources/Temp/temp_images";
        List<Double> vel_pro = video_object.getVelocityProfile(imageDir);
        GraphView graph = (GraphView) findViewById(R.id.NewStimuli_graph_view_new_stimuli);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

        double xd_max = 0.0;
        for(int i = 0; i < vel_pro.size(); i++){
            if(vel_pro.get(i) > xd_max){
                xd_max = vel_pro.get(i);
            }
            series.appendData(new DataPoint(i, vel_pro.get(i)) , true, vel_pro.size());
        }

        video_object.saveVelocityProfile();

        Log.d("getVelocityProfile", "Max vel = "+Double.toString(xd_max));
        graph.removeAllSeries();
        graph.addSeries(series);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(vel_pro.size());
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(xd_max);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
    }


    public void playAnalysed(View view){
        String loadedStimuliPath = (Environment.getExternalStorageDirectory()+"/KineTest/CurrentAnalysedVideo/");
        File myDir = new File(loadedStimuliPath);
        if (myDir.isDirectory()) {
            String[] children = myDir.list();
            Log.d("AddStimuli", "playAnalysed: path = "+loadedStimuliPath+children[0]);
            Uri outputVideo =Uri.parse(loadedStimuliPath+children[0]);
            videoView.setVideoURI(outputVideo);
            videoView.start();
        }else{
            Log.d("AddStimuli", "playAnalysed: no video analysed");
            Toast.makeText(this, "Video not analysed yet", Toast.LENGTH_SHORT).show();
        }
    }

}
