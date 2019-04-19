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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewStimuliVelFuncAnalyses extends AppCompatActivity {

    VideoView videoView;
    String video_path;
    String video_name;
    String video_name_analysed;
    Boolean got_video_images = true;
    Video videoloaded;
    List<Double> vel_pro = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stimuli_vel_func_analyses);

        Intent i = getIntent();
        video_path = (String) i.getSerializableExtra("videoPath");
        video_name = (String) i.getSerializableExtra("videoName");
        video_name_analysed = ((String) i.getSerializableExtra("videoName"))+"_analysed";
        videoloaded = new Video(this, video_name_analysed);
        videoloaded.setVideoPath(video_path);
        videoView = (VideoView)findViewById(R.id.NewStimuli_videoViewNewStimuli);

    }

    public void next(View view){
        Intent i = new Intent(this, NewStimuliAddArticialStimuli.class);
        i = i.putExtra("videoName", video_name);
        i = i.putExtra("velocityProfile", (Serializable) vel_pro);
        startActivity(i);
    }

    public void cancel(View view){
        videoloaded.clearTempFolders();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void LoadVelFuncFromFolder(View view){

    }

    public void loadVideoImages(View view){
        got_video_images = true;
        try {
            videoloaded.getImages();
            Toast.makeText(this, "Video images loaded", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Video images could not be loaded", Toast.LENGTH_SHORT).show();
        }
    }

    public void getVelocityProfile(View view){
        if(got_video_images){
            String imageDir = "KineTest/Resources/Temp/temp_images";
            vel_pro = videoloaded.getVelocityProfile(imageDir);
            GraphView graph = (GraphView) findViewById(R.id.NewStimuli_graph_view_new_stimuli);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

            double xd_max = 0.0;
            for (int i = 0; i < vel_pro.size(); i++) {
                if (vel_pro.get(i) > xd_max) {
                    xd_max = vel_pro.get(i);
                }
                series.appendData(new DataPoint(i, vel_pro.get(i)), true, vel_pro.size());
            }

            Log.d("getVelocityProfile", "Max vel = " + Double.toString(xd_max));
            graph.removeAllSeries();
            graph.addSeries(series);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(vel_pro.size());
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(xd_max);
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setXAxisBoundsManual(true);
        }else{
            Toast.makeText(this, "Video images not loaded yet", Toast.LENGTH_SHORT).show();
        }
    }


    public void playAnalysed(View view){
        String loadedStimuliPath = (Environment.getExternalStorageDirectory()+"/KineTest/Resources/Temp/temp_loaded_video/");
        File myDir = new File(loadedStimuliPath);
        if (myDir.isDirectory()) {
            String[] children = myDir.list();
            Boolean video_found = false;
            for(String child: children){
                if(child.equals(video_name_analysed+".mp4")){
                    Log.d("AddStimuli", "playAnalysed: path = "+loadedStimuliPath+child);
                    Uri outputVideo =Uri.parse(loadedStimuliPath+child);
                    videoView.setVideoURI(outputVideo);
                    videoView.start();
                    video_found = true;
                }
            }
            if(!video_found){
                Toast.makeText(this, "Video not analysed yet", Toast.LENGTH_SHORT).show();
            }

        }else{
            Log.d("AddStimuli", "playAnalysed: no video analysed");
            Toast.makeText(this, "Video not analysed yet", Toast.LENGTH_SHORT).show();
        }
    }


}
