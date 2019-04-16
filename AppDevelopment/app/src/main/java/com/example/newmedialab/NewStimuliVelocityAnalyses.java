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

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class NewStimuliVelocityAnalyses extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int SELECT_VIDEO = 2;
    VideoView videoView;
    VideoView videoView2;
    Uri currentVideo = Uri.parse(Environment.getExternalStorageDirectory()+"/KineTest/CurrentVideo/loadedVideo.mp4");
    Video videoloaded = new Video(this, "loadedVideo");
    Video video_object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stimuli);
        videoView = (VideoView)findViewById(R.id.videoViewNewStimuli);
        videoView2 = (VideoView)findViewById(R.id.videoViewAnalysedStimuli);
        Intent i = getIntent();
        this.video_object = (Video)i.getSerializableExtra("video_object");

        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    public void getVelocityProfile(View view){

        String imageDir = "KineTest/CurrentVideoImages";
        List<Double> vel_pro = videoloaded.getVelocityProfile(imageDir);
        GraphView graph = (GraphView) findViewById(R.id.graph_view_new_stimuli);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

        double xd_max = 0.0;
        for(int i = 0; i < vel_pro.size(); i++){
            if(vel_pro.get(i) > xd_max){
                xd_max = vel_pro.get(i);
            }
            series.appendData(new DataPoint(i, vel_pro.get(i)) , true, vel_pro.size());
        }

        videoloaded.saveVelocityProfile();

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


    public void getVelocityProfileConverted(View view){

        String imageDir = "KineTest/CurrentConvertedVideoImages";
        List<Double> vel_pro = videoloaded.getVelocityProfile(imageDir);
        GraphView graph = (GraphView) findViewById(R.id.graph_view_new_stimuli);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

        double xd_max = 0.0;
        for(int i = 0; i < vel_pro.size(); i++){
            if(vel_pro.get(i) > xd_max){
                xd_max = vel_pro.get(i);
            }
            series.appendData(new DataPoint(i, vel_pro.get(i)) , true, vel_pro.size());
        }

        //videoloaded.saveVelocityProfile();

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
            videoView2.setVideoURI(outputVideo);
            videoView2.start();
        }else{
            Log.d("AddStimuli", "playAnalysed: no video analysed");
            Toast.makeText(this, "Video not analysed yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void playStimuli(View view){
        String loadedStimuliPath = (Environment.getExternalStorageDirectory()+"/KineTest/CurrentVideo/");
        File myDir = new File(loadedStimuliPath);
        if (myDir.isDirectory()) {
            String[] children = myDir.list();
            Log.d("AddStimuli", "playStimuli: path = "+loadedStimuliPath+children[0]);
            Uri outputVideo =Uri.parse(loadedStimuliPath+children[0]);
            videoView.setVideoURI(outputVideo);
            videoView.start();
        }else{
            Log.d("AddStimuli", "playStimuli: no video");
            Toast.makeText(this, "video not loaded yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void playConverted(View view){
        String loadedStimuliPath = (Environment.getExternalStorageDirectory()+"/KineTest/CurrentConvertedVideo/");
        File myDir = new File(loadedStimuliPath);
        if (myDir.isDirectory()) {
            String[] children = myDir.list();
            Log.d("AddStimuli", "playConverted: path = "+loadedStimuliPath+children[0]);
            Uri outputVideo =Uri.parse(loadedStimuliPath+children[0]);
            videoView.setVideoURI(outputVideo);
            videoView.start();
        }else{
            Log.d("AddStimuli", "playConverted: no video");
            Toast.makeText(this, "video not converted yet", Toast.LENGTH_SHORT).show();
        }
    }


    public void convertStimuli(View view){

        List<Double> vel_pro = videoloaded.loadVelocityProfile();
        VelocityFunction velfunc = new VelocityFunction("1-log(x)");
        videoloaded.convertVideo("KineTest/CurrentVideoImages", "KineTest/CurrentConvertedVideoImages", "KineTest/CurrentConvertedVideo", vel_pro, velfunc,"linear", 1);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    Mat imageMat=new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


}
