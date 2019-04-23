package com.example.newmedialab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
    Boolean artificialStimuli_acquired = false;

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
    }

    public void save(View view){
        if(artificialStimuli_acquired) {
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
                for (int i = 0; i < children.length; i++) {
                    copy_to_dir = Environment.getExternalStorageDirectory() + "/KineTest/Resources/Languages/" + language + "/" + type + "/video_images/" + video_name;
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
            Toast.makeText(this, "video not loaded yet", Toast.LENGTH_SHORT).show();
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
