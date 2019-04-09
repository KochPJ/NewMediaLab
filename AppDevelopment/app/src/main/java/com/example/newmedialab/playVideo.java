package com.example.newmedialab;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import org.jcodec.api.JCodecException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


public class playVideo extends AppCompatActivity {
    VideoView videov;
    Video v;
    ImageView image;
    Handler handler=new Handler();
    Bitmap b;
    String video_name = "test";


    @TargetApi(Build.VERSION_CODES.P)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void buildVideo(View view) throws InterruptedException, IOException, JCodecException {

        image = (ImageView) findViewById(R.id.imview);



        MediaMetadataRetriever m = new MediaMetadataRetriever();
        String videopath ="android.resource://" + getPackageName()+ "/" + R.raw.test;
        m.setDataSource(this, Uri.parse(videopath));

        int max = (int) m.METADATA_KEY_DURATION;
        int fps = m.METADATA_KEY_CAPTURE_FRAMERATE;
        int timestep = (int)(1000000.0*(1.0/(float)fps));

        //Create dir if not exists
        File root = new File(Environment.getExternalStorageDirectory(), "KineTest/videos");
        if (!root.exists()) root.mkdirs();
        File root2 = new File(Environment.getExternalStorageDirectory(), "KineTest/results");
        if (!root2.exists()) root2.mkdirs();

        Bitmap b;
        List<Bitmap> video = new ArrayList<Bitmap>();
        for (int i=0;i<max*fps;i++){
            //Add zero padding to filename
            String n = Integer.toString(i);
            char[] c = n.toCharArray();
            String number = "";
            for(int j = 0; j < 4-c.length; j++){
                number+="0";
            }
            for(int j = 0; j< c.length; j++){
                number+=c[j];
            }

            Log.i("i",String.valueOf(i));
            b = m.getFrameAtTime(i*timestep,MediaMetadataRetriever.OPTION_CLOSEST);

            FileOutputStream fileOutputStream = new FileOutputStream((Environment.getExternalStorageDirectory()+"/KineTest/videos/"+video_name+number+".png"));
            b.compress(Bitmap.CompressFormat.PNG, 1, fileOutputStream);
        }

        String path = (Environment.getExternalStorageDirectory()+"/KineTest/videos/");
        String resultpath = (Environment.getExternalStorageDirectory()+"/KineTest/results/");
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            //String[] cmd = {"-i", path+video_name+"%04d.png", resultpath+"test.mp4"};

            String[] cmd = {"-r", "25" ,"-f","image2", "-i",path+video_name+"%04d.png","-vcodec","libx264","-crf","25","-pix_fmt","yuv420p", resultpath+"test5.mp4"};

            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("ffmpeg", "starting");
                }

                @Override
                public void onProgress(String message) {
                    Log.d("ffmpeg", "in progress");
                    Log.d("message", message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d("error", message);
                    Log.d("ffmpeg", "failed");
                }

                @Override
                public void onSuccess(String message) {
                    Log.d("ffmpeg", "success");
                    Log.d("message", message);
                }

                @Override
                public void onFinish() {
                    String path2 = Environment.getExternalStorageDirectory()+"/KineTest/results";
                    Log.d("Files", "Path: " + path2);
                    File directory = new File(path2);
                    File[] files = directory.listFiles();
                    Log.d("Files", "Size: "+ files.length);
                    for (int i = 0; i < files.length; i++)
                    {
                        Log.d("Files", "FileName:" + files[i].getName());
                    }
                    Toast.makeText(getApplicationContext(), "saved the video", Toast.LENGTH_LONG).show();

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }

        Log.d("msg", "exited loop");
        }

    public void playVideo(View view){
        videov = (VideoView)findViewById(R.id.videoView);
        String Path = (Environment.getExternalStorageDirectory()+"/KineTest/results/")+"test5.mp4";
        Log.d("play Video", "Path = "+Path);

        Uri uri = Uri.parse(Environment.getExternalStorageDirectory()+"/KineTest/results/test5.mp4");
        videov.setVideoURI(uri);
        videov.start();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

    }


}