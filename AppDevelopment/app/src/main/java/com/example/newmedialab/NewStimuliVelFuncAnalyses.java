package com.example.newmedialab;

import androidx.appcompat.app.AppCompatActivity;

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
import java.io.IOException;
import java.util.List;

public class NewStimuliVelFuncAnalyses extends AppCompatActivity {

    VideoView videoView;
    String video_path;
    String video_name;
    Video videoloaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stimuli_vel_func_analyses);

        Intent i = getIntent();
        video_path = (String) i.getSerializableExtra("videoPath");
        video_name = (String) i.getSerializableExtra("videoName");
        videoloaded = new Video(this, video_name);
        videoloaded.setVideoPath(video_path);


    }


    public void getVelocityProfile(View view){

        try {
            videoloaded.getImages();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imageDir = "KineTest/Resources/Temp/temp_images";
        List<Double> vel_pro = videoloaded.getVelocityProfile(imageDir);
        GraphView graph = (GraphView) findViewById(R.id.NewStimuli_graph_view_new_stimuli);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

        double xd_max = 0.0;
        for(int i = 0; i < vel_pro.size(); i++){
            if(vel_pro.get(i) > xd_max){
                xd_max = vel_pro.get(i);
            }
            series.appendData(new DataPoint(i, vel_pro.get(i)) , true, vel_pro.size());
        }

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
        String loadedStimuliPath = (Environment.getExternalStorageDirectory()+"/KineTest//");
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
