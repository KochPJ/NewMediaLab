package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

public class WritingExperiment extends AppCompatActivity {

    public Experiment exp = new Experiment("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_experiment);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
    }

    public void navigation(View view) {
        //TODO: save parameters in experiment
        CheckBox cb = (CheckBox)findViewById(R.id.progress_bar);
        boolean progress = cb.isChecked();
        switch (view.getId()) {
            case R.id.save_button: // Go to function view with edit param
                Intent intent = new Intent(this, ExperimentFunction.class);
                intent = intent.putExtra("experiment", exp);
                intent = intent.putExtra("progress", progress);
                startActivity(intent);
                break;
            case R.id.preview_button: // Go to preview view
                //TODO: create preview view
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
    }

}
