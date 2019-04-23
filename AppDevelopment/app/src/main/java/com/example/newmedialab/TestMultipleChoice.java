package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class TestMultipleChoice extends AppCompatActivity {

    public Experiment exp = new Experiment("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_multiple_choice);

        setContentView(R.layout.activity_test_writing);
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
    }

}
