package com.example.newmedialab;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class AddStimuli extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int SELECT_VIDEO = 2;
    VideoView videoView;
    Uri currentVideo = Uri.parse(Environment.getExternalStorageDirectory()+"/KineTest/CurrentVideo/loadedVideo.mp4");
    Video videoloaded = new Video(this, "loadedVideo", 25,4,currentVideo);

    public void getVelocityProfile(View view){
        List<Double> vel_pro = videoloaded.getVelocityProfile();
        GraphView graph = (GraphView) findViewById(R.id.graph_view_new_stimuli);
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

    public void captureVideo(View view) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    public void loadStimuli(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_VIDEO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                Uri videoPath = data.getData();
                videoloaded = new Video(this, "loadedVideo", 25, 4, videoPath);

                VelocityFunction velocityFunction= new VelocityFunction("1");

                try {
                    videoloaded.getImages(velocityFunction);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Uri outputVideo = videoloaded.createVideo();
                videoView.setVideoURI(outputVideo);
                videoView.start();
            }
        }
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stimuli);
        videoView = (VideoView)findViewById(R.id.videoViewNewStimuli);

        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }



}
