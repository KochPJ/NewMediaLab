package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ShowStimuli extends AppCompatActivity {

    public Experiment exp = new Experiment("");
    private int remaining_repeats = 1;
    private int auto_repeats = 1;
    TextView repeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stimuli);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");

        TextView expName = (TextView) findViewById(R.id.tv_exp_name);
        expName.setText(exp.getName());

        remaining_repeats = Integer.parseInt(exp.getMaxRepeats());
        auto_repeats = Integer.parseInt(exp.getAutoRepeats());

        //Sanity checks
        if(remaining_repeats < 1){remaining_repeats = 1;}
        if(auto_repeats < 1){auto_repeats = 1;}

        repeats = (TextView) findViewById(R.id.tv_remaining_repeats);
        repeats.setText("Remaining Replays: "+remaining_repeats);
    }

    public void replayStimuli(View view) {
        String stimuli_name = exp.getNextStimuli();
        if(this.remaining_repeats > 0){
            //TODO: load correct video based on stimuli_name
            for(int i = 0; auto_repeats > i; i++){
                //play video for the correct amount of user specified loops

            }
            remaining_repeats -= 1;
            repeats.setText("Remaining Replays: "+remaining_repeats);
        } else {
            Toast.makeText(this, "No Replays Remaining", Toast.LENGTH_LONG);
        }

    }

    public void nextStimuli(View view) {
        Intent intent = new Intent(this, TestWriting.class);
        intent = intent.putExtra("experiment", exp);
        startActivity(intent);
    }
}
