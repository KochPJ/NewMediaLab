package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TestMultipleChoice extends AppCompatActivity {

    public Experiment exp = new Experiment("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_multiple_choice);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");

        //Set progressbar with correct value
        ProgressBar pb = findViewById(R.id.progressBar3);
        int maxSymbols = 0;
        for (char ch : exp.getSymbols().toCharArray()){
            if(ch != ',' && ch != ' '){
                maxSymbols++;
            }
        }
        pb.setMax(maxSymbols);
        pb.setProgress(maxSymbols - exp.getRemainingSymbols().size());

        //Set messages
        TextView expName = (TextView) findViewById(R.id.textView11);
        expName.setText(exp.getName());
        TextView expName2 = (TextView) findViewById(R.id.textView12);
        expName2.setText(exp.getTask_msg());

    }

    public void nextQuestion(View view) {
        CheckBox cb = findViewById((R.id.checkBox_dontKnow));

        if(exp.finishedShowingStimuli()){
            // Save changes to experiment
            exp.createFile();
            Toast.makeText(TestMultipleChoice.this, "Finished test, saved results",
                    Toast.LENGTH_LONG).show();
            // Return to MyExperiments
            Intent intent = new Intent(this, MyExperiments.class);
            intent = intent.putExtra("experiment", exp);
            startActivity(intent);
        } else {
            // Show next stimuli
            Intent intent2 = new Intent(this, TestMultipleChoice.class);
            intent2 = intent2.putExtra("experiment", exp);
            startActivity(intent2);
        }

    }
}
