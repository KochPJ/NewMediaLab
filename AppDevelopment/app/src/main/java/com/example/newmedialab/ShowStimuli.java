package com.example.newmedialab;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class ShowStimuli extends AppCompatActivity {

    public Experiment exp = new Experiment("");
    private int remaining_repeats, starting_repeats = 1;
    private int auto_repeats = 1;
    TextView repeats;
    public String[] stimuli;
    public Boolean writing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stimuli);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
        writing = (Boolean) i.getSerializableExtra("writing");

        TextView expName = (TextView) findViewById(R.id.tv_exp_name);
        expName.setText(exp.getName());

        starting_repeats = Integer.parseInt(exp.getMaxRepeats());
        remaining_repeats = Integer.parseInt(exp.getMaxRepeats());
        auto_repeats = Integer.parseInt(exp.getAutoRepeats());

        //Sanity checks
        if(remaining_repeats < 1){remaining_repeats = 1;}
        if(auto_repeats < 1){auto_repeats = 1;}

        repeats = (TextView) findViewById(R.id.tv_remaining_repeats);
        repeats.setText("Remaining Replays: "+remaining_repeats);

        stimuli = exp.getNextStimuli();
        //Set progressbar with correct value
        ProgressBar pb = findViewById(R.id.progressBar);
        if(Boolean.parseBoolean(exp.getProgressbar())){
            int maxSymbols = exp.getStimuli().size();
            pb.setMax(maxSymbols+1);
            pb.setProgress(maxSymbols - exp.getRemainingStimuli().size());
        } else {
            pb.setAlpha(0);
        }
        //Move vv to 1st frame
        VideoView vv = (VideoView) findViewById( R.id.videoView2 );
        if(!vv.isPlaying()){
            if(exp.getCurrentID()%2 == 0){
                //Subject is part of the control group and gets the artificial stimuli
                String path = stimuli[0];
                Uri u = Uri.parse(Environment.getExternalStorageDirectory()+"/"+path);
                vv.setVideoURI(u);
                vv.seekTo( 1 );
            } else {
                //Subject is part of the experimental group and gets the kinestetic stimuli
                String path = stimuli[1];
                Uri u = Uri.parse(Environment.getExternalStorageDirectory()+"/"+path);
                vv.setVideoURI(u);
                vv.seekTo( 1 );
            }
        }

    }

    public void autoReplayStimuli(final int auto_repeats, final VideoView vv) {
        final VideoView fvv = vv;
        if(auto_repeats>0){
            fvv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    // restart on completion
                    fvv.start();
                    autoReplayStimuli(auto_repeats-1, vv);
                }
            });
        } else {
            fvv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    // restart on completion
                    fvv.stopPlayback();
                }
            });
        }
    }

    public void replayStimuli(View view) {
        VideoView vv = findViewById(R.id.videoView2);
        //MediaController m = new MediaController(this);
        //vv.setMediaController(m);
        if(!vv.isPlaying()){
            if(this.remaining_repeats > 0){
                //for(int i = 0; auto_repeats > i; i++){
                    //play video for the correct amount of user specified loops
                if (exp.getCurrentID() % 2 == 0) {
                    //Subject is part of the control group and gets the artificial stimuli
                    String path = stimuli[0];
                    Uri u = Uri.parse(Environment.getExternalStorageDirectory() + "/" + path);
                    vv.setVideoURI(u);
                    vv.start();
                } else {
                    //Subject is part of the experimental group and gets the kinestetic stimuli
                    String path = stimuli[1];
                    Uri u = Uri.parse(Environment.getExternalStorageDirectory() + "/" + path);
                    vv.setVideoURI(u);
                    vv.start();
                }
                autoReplayStimuli(this.auto_repeats-1, vv);
                remaining_repeats -= 1;
                repeats.setText("Remaining Replays: "+remaining_repeats);
            } else {
                Toast mToastToShow = Toast.makeText(this, "No Replays Remaining", Toast.LENGTH_LONG);
                mToastToShow.show();
            }
        } else {
            Toast mToastToShow = Toast.makeText(this, "Currently Playing Video", Toast.LENGTH_SHORT);
            mToastToShow.show();
        }
    }

    public void nextStimuli(View view) {
        if(this.remaining_repeats == this.starting_repeats){
            Toast mToastToShow = Toast.makeText(this, "Please play the video once before continuing", Toast.LENGTH_LONG);
            mToastToShow.show();
        } else {
            if(writing){
                Intent intent = new Intent(this, TestWriting.class);
                intent = intent.putExtra("experiment", exp);
                startActivity(intent);
            } else {
                if(exp.getRemainingStimuli().size() == 0){
                    Intent intent = new Intent(this, TestMultipleChoice.class);
                    intent = intent.putExtra("experiment", exp);
                    intent = intent.putExtra("pretest", true);
                    startActivity(intent);
                } else{
                    Intent intent = new Intent(this, ShowStimuli.class);
                    intent = intent.putExtra("experiment", exp);
                    intent = intent.putExtra("writing", false);
                    startActivity(intent);
                }
            }
        }
    }
}