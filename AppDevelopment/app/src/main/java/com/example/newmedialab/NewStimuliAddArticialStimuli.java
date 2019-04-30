package com.example.newmedialab;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NewStimuliAddArticialStimuli extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int SELECT_VIDEO = 2;
    ArrayList<Uri> userSelectedVideoUriList;
    Integer lastAdd = -1;
    Video videoloaded;
    VideoView videoView;
    String video_name;
    String language;
    String type;
    List<Double> vel_pro = new ArrayList<Double>();
    private ProgressDialog progress;
    Boolean artificialStimuli_acquired = false;
    Boolean ProgressDone;
    Context context;

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
        setContentView(R.layout.activity_new_stimuli_add_articial_stimuli);
        Intent i = getIntent();
        video_name = (String) i.getSerializableExtra("videoName");
        language = (String) i.getSerializableExtra("language");
        type = (String) i.getSerializableExtra("type");
        vel_pro = (ArrayList<Double>) i.getSerializableExtra("velocityProfile");
        videoloaded = new Video(this, video_name);
        videoView = (VideoView)findViewById(R.id.newStimuli_artificialStimuli_videoView);
        context = this;
    }

    public void save(View view){
        if(videoloaded.got_video_images) {
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

            //save artificial video
            copy_to_dir = Environment.getExternalStorageDirectory() + "/KineTest/Resources/Languages/" + language + "/" + type + "/videos_artificial";
            try {
                InputStream inputStream = getContentResolver().openInputStream(this.userSelectedVideoUriList.get(this.userSelectedVideoUriList.size() - 1));
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

            //save last video image
            myDir = new File(Environment.getExternalStorageDirectory() + "/KineTest/Resources/temp/temp_images");
            if (myDir.isDirectory()) {
                String[] children = myDir.list();
                int last_image_index = children.length-1;
                copy_to_dir = Environment.getExternalStorageDirectory() + "/KineTest/Resources/Languages/"
                        + language + "/" + type + "/videos_original_lastImage";
                try {
                    InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(
                            Environment.getExternalStorageDirectory() + "/KineTest/Resources/temp/temp_images/" + children[last_image_index])));
                    videoloaded.copyVideoTo(inputStream, copy_to_dir, video_name+".png");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //save artificial video Images
            myDir = new File(Environment.getExternalStorageDirectory() + "/KineTest/Resources/temp/temp_artificial_images");
            if (myDir.isDirectory()) {
                String[] children = myDir.list();
                copy_to_dir = Environment.getExternalStorageDirectory() + "/KineTest/Resources/Languages/"
                        + language + "/" + type + "/video_artificial_images/" + video_name;
                for (int i = 0; i < children.length; i++) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(
                                Environment.getExternalStorageDirectory() + "/KineTest/Resources/temp/temp_artificial_images/" + children[i])));
                        videoloaded.copyVideoTo(inputStream, copy_to_dir, children[i]);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            //save last artificial video image
            myDir = new File(Environment.getExternalStorageDirectory() + "/KineTest/Resources/temp/temp_artificial_images");
            if (myDir.isDirectory()) {
                String[] children = myDir.list();
                int last_image_index = children.length-1;
                copy_to_dir = Environment.getExternalStorageDirectory() + "/KineTest/Resources/Languages/"
                        + language + "/" + type + "/videos_artificial_lastImage";
                try {
                    InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(
                            Environment.getExternalStorageDirectory() + "/KineTest/Resources/temp/temp_artificial_images/" + children[last_image_index])));
                    videoloaded.copyVideoTo(inputStream, copy_to_dir, video_name+".png");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            //save velocity profile
            videoloaded.setVeloctiyProfile(vel_pro);
            videoloaded.saveVelocityProfile("KineTest/Resources/Languages/" + language + "/" + type + "/velocityprofiles");


            //clear temp folder
            videoloaded.clearTempFolders();

            //make a toast
            Toast.makeText(this, "New Stimuli saved", Toast.LENGTH_SHORT).show();
            //return to main view
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }else{
            Toast.makeText(this, "video imaged not loaded yet", Toast.LENGTH_SHORT).show();
        }

    }

    public void cancel(View view){
        videoloaded.clearTempFolders();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void filmVideo(View view) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
    }


    public void loadVideoImages(View view){

        if(artificialStimuli_acquired) {
            //make new tread and run get images here
            new Thread(new Runnable() {
                public void run() {
                    try {
                        videoloaded.getImages("temp_artificial_images");
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

            final Thread t = new Thread() {
                @Override
                public void run() {
                    while (videoloaded.get_images_progress < videoloaded.n_frames) {
                        try {
                            //small 1s sleep, so we dont need to update to much. takes time to get the images
                            sleep(1000);
                            progress.setProgress((int) videoloaded.get_images_progress);
                            Log.d("NewStiVelFunc", "loadVideoImages: get image progress = " + videoloaded.get_images_progress);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    h.sendEmptyMessage(0);
                    progress.cancel();


                }
            };
            t.start();
        }else{
            Toast.makeText(this, "video not loaded yet", Toast.LENGTH_SHORT).show();
        }

    }

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

                //save artificial video to temp folder
                String copy_to_dir = Environment.getExternalStorageDirectory() + "/KineTest/Resources/temp/temp_loaded_video";
                try {
                    InputStream inputStream = getContentResolver().openInputStream(this.userSelectedVideoUriList.get(this.userSelectedVideoUriList.size() - 1));
                    videoloaded.copyVideoTo(inputStream, copy_to_dir, video_name + "_artificial.mp4");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                videoloaded.setVideoPath(copy_to_dir+"/"+video_name+"_artificial.mp4");

                //String filepath = urip.getUriRealPath(getApplicationContext(), fileUri);
                //userSelectedVideoDirectoryList.add(filepath);
                this.lastAdd = 1;
                Toast.makeText(this, "Video loaded", Toast.LENGTH_SHORT).show();
                artificialStimuli_acquired = true;
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
}
