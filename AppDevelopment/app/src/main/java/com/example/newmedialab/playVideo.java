package com.example.newmedialab;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import org.jcodec.api.JCodecException;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.DictionaryCompressor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class playVideo extends AppCompatActivity {
    VideoView videov;
    Video v;
    ImageView image;
    Handler handler=new Handler();
    Bitmap b;



    @TargetApi(Build.VERSION_CODES.P)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void playVideo(View view) throws InterruptedException, IOException, JCodecException {

        image = (ImageView) findViewById(R.id.imview);


        MediaMetadataRetriever m = new MediaMetadataRetriever();
        String videopath ="android.resource://" + getPackageName()+ "/" + R.raw.test;
        m.setDataSource(this, Uri.parse(videopath));

        int max = (int) m.METADATA_KEY_DURATION;
        int fps = m.METADATA_KEY_CAPTURE_FRAMERATE;
        int timestep = (int)(1000.0*(float)max/(float)fps);

        Bitmap b;
        List<Bitmap> video = new ArrayList<Bitmap>();
        for (int i=0;i<max*fps;i++){

            Log.i("i",String.valueOf(i));
            b = m.getFrameAtTime(i*timestep,MediaMetadataRetriever.OPTION_CLOSEST);
            video.add(b);
            image.setImageBitmap(b);
        }



/*

        InputStream ins = getResources().openRawResource(getResources().getIdentifier("test", "raw", getPackageName()));
        String path = getFilesDir().getAbsolutePath();
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }

       // v = new Video(ins);


        Log.d("Files", "Path: " + path);
        directory = new File(path);
        files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }
*/


        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
    }


}