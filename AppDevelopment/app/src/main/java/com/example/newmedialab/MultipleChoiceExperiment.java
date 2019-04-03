package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MultipleChoiceExperiment extends AppCompatActivity {

    public Experiment exp = new Experiment("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_experiment);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
    }

    public void navigation(View view) {

    }

}
