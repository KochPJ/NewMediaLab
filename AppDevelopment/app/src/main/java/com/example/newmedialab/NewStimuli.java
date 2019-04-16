package com.example.newmedialab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class NewStimuli extends AppCompatActivity {
    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int SELECT_VIDEO = 2;
    ArrayList<Uri> userSelectedVideoUriList;
    ArrayList<Uri> userFilmedVideoUriList;
    ArrayList<String> userSelectedVideoDirectoryList;
    ArrayList<String> userFilmedVideoDirectoryList;
    String video_name = "video_1";
    VideoView videoView;
    Integer lastAdd = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stimuli);
        videoView = (VideoView)findViewById(R.id.vv_new_stimuli);
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void FinishAdding(View view) {
        //TODO: set correct in/out dirs, add name
        // Get video name
        EditText et_video_name = (EditText) findViewById(R.id.et_video_name);
        this.video_name = et_video_name.getText().toString();

        if (lastAdd != -1) {
            Video videoloaded = new Video(this, video_name);
            String copy_to_dir = Environment.getExternalStorageDirectory()+ "/KineTest/Resources/temp/temp_loaded_video";
            try {
                InputStream inputStream = getContentResolver().openInputStream(this.userSelectedVideoUriList.get(this.userSelectedVideoUriList.size() - 1));
                videoloaded.copyVideoTo(inputStream, copy_to_dir, video_name+".mp4");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent i = new Intent(this, NewStimuliVelFuncAnalyses.class);
            i = i.putExtra("videoPath", copy_to_dir+"/"+video_name+".mp4");
            i = i.putExtra("videoName", video_name);
            startActivity(i);

        }else{
            Log.d("NewStimuli", "playStimuli: no video");
            Toast.makeText(this, "video not loaded yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void filmVideo(View view) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
    }
    /* Invoke android os system file browser to select images. */
    public void loadVideo(View view)
    {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_VIDEO);
    }

    /* When the action Intent.ACTION_GET_CONTENT invoked app return, this method will be executed. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==SELECT_VIDEO) {
            if(resultCode==RESULT_OK)
            {
                if(userSelectedVideoUriList == null) {userSelectedVideoUriList = new ArrayList<Uri>(); }

                Uri fileUri = data.getData();
                userSelectedVideoUriList.add(fileUri);
                Log.d("NewStimuli: ", "video uri: "+fileUri.toString());
                //String filepath = urip.getUriRealPath(getApplicationContext(), fileUri);
                //userSelectedVideoDirectoryList.add(filepath);
                this.lastAdd = 1;
            }
        }
    }

    public void playVideo(View view){
        if(this.lastAdd == 1){
            Uri outputVideo = this.userSelectedVideoUriList.get(this.userSelectedVideoUriList.size() - 1);
            videoView.setVideoURI(outputVideo);
            videoView.start();
        } else{
            Log.d("NewStimuli", "playStimuli: no video");
            Toast.makeText(this, "video not loaded yet", Toast.LENGTH_SHORT).show();
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
}