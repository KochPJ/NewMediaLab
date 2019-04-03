package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ExperimentType extends AppCompatActivity {

    public Experiment exp = new Experiment("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_type);
    }

    public void expChoice(View view) {
        switch (view.getId()) {
            case R.id.writing_exp: // Go to writing view
                Intent intent = new Intent(this, WritingExperiment.class);
                intent = intent.putExtra("experiment", exp);
                startActivity(intent);
                break;
            case R.id.multiple_choice_exp: // Go to multiple choice view
                Intent intent2 = new Intent(this, MultipleChoiceExperiment.class);
                intent2 = intent2.putExtra("experiment", exp);
                startActivity(intent2);
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
    }

}
