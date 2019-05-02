package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ViewStimuli extends AppCompatActivity {

    public Experiment exp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stimuli);

        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
    }

    //TODO: finish activity

}
