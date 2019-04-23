package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//TODO: implement progressbar

public class ShowStimuli extends AppCompatActivity {

    public Experiment exp = new Experiment("");
    private int remaining_repeats = 1;
    private int auto_repeats = 1;
    TextView repeats;
    public String stimuli_name;

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

        stimuli_name = exp.getNextStimuli();
        //Set progressbar with correct value
        ProgressBar pb = findViewById(R.id.progressBar);
        int maxSymbols = 0;
        for (char ch : exp.getSymbols().toCharArray()){
            if(ch != ',' && ch != ' '){
                maxSymbols++;
            }
        }
        pb.setMax(maxSymbols);
        pb.setProgress(maxSymbols - exp.getRemainingSymbols().size());

    }

    public void replayStimuli(View view) {

        if(this.remaining_repeats > 0){
            //TODO: load correct video based on stimuli_name
            for(int i = 0; auto_repeats > i; i++){
                //play video for the correct amount of user specified loops

            }
            remaining_repeats -= 1;
            repeats.setText("Remaining Replays: "+remaining_repeats);
        } else {
            Toast mToastToShow = Toast.makeText(this, "No Replays Remaining", Toast.LENGTH_LONG);
            mToastToShow.show();
        }

    }

    public void nextStimuli(View view) {
        Intent intent = new Intent(this, TestWriting.class);
        intent = intent.putExtra("experiment", exp);
        startActivity(intent);
    }
}
