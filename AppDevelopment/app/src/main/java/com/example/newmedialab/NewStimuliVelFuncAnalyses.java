package com.example.newmedialab;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class NewStimuliVelFuncAnalyses extends AppCompatActivity {

    int PICK_IMAGE = 1;
    VideoView videoView;
    String video_path;
    String language;
    String type;
    String video_name;
    String video_name_analysed;
    Boolean ProgressDone;
    Boolean velocityProfile_acquired = false;
    Video videoloaded;
    Context context;
    List<Double> vel_pro = new ArrayList<Double>();
    private ProgressDialog progress;
    Spinner ImportVelProSpinner;
    String[] ImportVelProList;
    String ImportVelPro;
    Uri selectedTestImage;
    ImageView TestImageView;

    Handler h = new Handler() {
        public void handleMessage(Message msg){
            if(msg.what == 0){
                Toast.makeText(context, "Video images loaded", Toast.LENGTH_SHORT).show();
            }
            if(msg.what == 1){
                Toast.makeText(context, "Video images not loaded yet", Toast.LENGTH_SHORT).show();
            }
            if(msg.what == 2){

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stimuli_vel_func_analyses);

        Intent i = getIntent();
        video_path = (String) i.getSerializableExtra("videoPath");
        video_name = (String) i.getSerializableExtra("videoName");
        language = (String) i.getSerializableExtra("language");
        type = (String) i.getSerializableExtra("type");
        video_name_analysed = ((String) i.getSerializableExtra("videoName"))+"_analysed";
        videoloaded = new Video(this, video_name_analysed);
        videoloaded.setVideoPath(video_path);
        videoView = (VideoView)findViewById(R.id.NewStimuli_videoViewNewStimuli);
        context = this;
        TestImageView = (ImageView) findViewById(R.id.NewStimuli_testImage_imageView);

        File myDir = new File(Environment.getExternalStorageDirectory() + "/KineTest/ImportVelocityProfile");
        if(!myDir.exists()) myDir.mkdirs();
        if (myDir.isDirectory()) {
            ImportVelProList = myDir.list();
        }

        ImportVelProSpinner = (Spinner) findViewById(R.id.newStimuli_vel_func_importVelPro_spinner);
        ArrayAdapter<String> spinnerArrayAdapterLanguage = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        this.ImportVelProList); //selected item will look like a spinner set from XML
        spinnerArrayAdapterLanguage.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        ImportVelProSpinner.setAdapter(spinnerArrayAdapterLanguage);
        ImportVelProSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ImportVelPro = ImportVelProList[position];
            }
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

    }

    public void SelectTestImage(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE);
    }



    public void next(View view){
        if(velocityProfile_acquired && videoloaded.got_video_images) {
            Toast.makeText(this, "saving...", Toast.LENGTH_SHORT).show();
            //save original video
            String copy_to_dir = Environment.getExternalStorageDirectory() + "/KineTest/Resources/Languages/" + language + "/" + type + "/videos_kinematic";
            try {
                InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(
                        Environment.getExternalStorageDirectory() + "/KineTest/Resources/temp/temp_loaded_video/" + video_name + ".mp4")));
                videoloaded.copyVideoTo(inputStream, copy_to_dir, video_name + ".mp4");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            //save video Images
            File myDir = new File(Environment.getExternalStorageDirectory() + "/KineTest/Resources/temp/temp_images");
            if (myDir.isDirectory()) {
                String[] children = myDir.list();
                copy_to_dir = Environment.getExternalStorageDirectory() + "/KineTest/Resources/Languages/"
                        + language + "/" + type + "/video_images/" + video_name;
                for (int i = 0; i < children.length; i++) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(
                                Environment.getExternalStorageDirectory() + "/KineTest/Resources/temp/temp_images/" + children[i])));
                        videoloaded.copyVideoTo(inputStream, copy_to_dir, children[i]);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            //save test image
            copy_to_dir = Environment.getExternalStorageDirectory() + "/KineTest/Resources/Languages/"
                    + language + "/" + type + "/testImages";
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedTestImage);
                videoloaded.copyVideoTo(inputStream, copy_to_dir, video_name+".png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            //save velocity profile
            videoloaded.videoName = video_name;
            videoloaded.setVeloctiyProfile(vel_pro);
            videoloaded.saveVelocityProfile("KineTest/Resources/Languages/" + language + "/" + type + "/velocityprofiles");


            //clear temp folder
            videoloaded.clearTempFolders();

            //make a toast
            Toast.makeText(this, "New Stimuli saved", Toast.LENGTH_SHORT).show();
            //return to main view
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);


            /*
            Intent i = new Intent(this, NewStimuliAddArticialStimuli.class);
            i = i.putExtra("videoName", video_name);
            i = i.putExtra("velocityProfile", (Serializable) vel_pro);
            i = i.putExtra("type", type);
            i = i.putExtra("language", language);
            startActivity(i);*/

        }else{

            Toast.makeText(this, "Velocity Profile not acquired yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancel(View view){
        videoloaded.clearTempFolders();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void LoadVelFuncFromFolder(View view){

        vel_pro = videoloaded.loadVelocityProfile("KineTest/ImportVelocityProfile/"+ImportVelPro);

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



        velocityProfile_acquired = true;
    }

    public void loadVideoImages(View view){

        //make new tread and run get images here
        new Thread(new Runnable() {
            public void run() {
                try {
                    videoloaded.getImages("temp_images");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        progress = new ProgressDialog(this);
        progress.setMessage("Get images");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.setCancelable(false);
        progress.setMax(videoloaded.n_frames);

        progress.show();
        ProgressDone = false;

        final Thread t = new Thread(){
            @Override
            public void run(){
                while(videoloaded.get_images_progress < videoloaded.n_frames){
                    try {
                        //small 1s sleep, so we dont need to update to much. takes time to get the images
                        sleep(1000);
                        progress.setProgress((int) videoloaded.get_images_progress);
                        Log.d("NewStiVelFunc", "loadVideoImages: get image progress = "+videoloaded.get_images_progress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                h.sendEmptyMessage(0);
                progress.cancel();


            }
        };
        t.start();

    }

    public void getVelocityProfile(View view){

        if(videoloaded.got_video_images){
            videoloaded.setVideo_created(false); // the bool to false, needed for the progress bar update
            //make new tread and run get velocity function
            new Thread(new Runnable() {
                public void run() {
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
                }
            }).start();


            progress = new ProgressDialog(this);
            progress.setMessage("Analyse images");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setIndeterminate(false);
            progress.setProgress(0);
            progress.setCancelable(false);
            progress.setMax(videoloaded.n_frames);

            progress.show();
            ProgressDone = false;

            final Thread t = new Thread(){
                @Override
                public void run(){
                    while(videoloaded.get_velPro_progress < videoloaded.n_frames){
                        try {
                            //small 200ms sleep, so we dont need to update to much. takes time to get the images
                            sleep(200);
                            progress.setProgress((int) videoloaded.get_velPro_progress);
                            Log.d("NewStiVelFunc", "loadVideoImages: get vel pro progress = "+videoloaded.get_velPro_progress);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //progress.setMessage("Create Video");
                    //check if the video was created
                    while(!videoloaded.video_created){
                        try {
                            //small 500ms sleep, so we dont need to update to much. takes time to get the images
                            sleep(500);
                            Log.d("NewStiVelFunc", "loadVideoImages: waiting for video to be created");

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    progress.cancel(); //end progress dialog
                    velocityProfile_acquired = true;
                }
            };
            t.start();
        }else{
            h.sendEmptyMessage(1);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if ((requestCode == PICK_IMAGE)
                    && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image
                if(data.getData()!=null){
                    selectedTestImage = data.getData();
                    TestImageView.setImageURI(selectedTestImage);


                }
            } else {
                Toast.makeText(this, "You haven't picked an image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong: "+e, Toast.LENGTH_LONG)
                    .show();
        }
    }
}
