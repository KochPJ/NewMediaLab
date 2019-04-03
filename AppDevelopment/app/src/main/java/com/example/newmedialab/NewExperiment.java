package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class NewExperiment extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_experiment);
    }

    public void next(View view) {
        //pointer to the text edit
        EditText exp_name_EditText = (EditText) findViewById(R.id.te_experiment_name);
        //get experiment name
        String exp_name = exp_name_EditText.getText().toString();
        // create new experiment
        Experiment exp = new Experiment(exp_name);

        Intent intent = new Intent(this, ExperimentType.class);
        intent = intent.putExtra("experiment", exp);
        startActivity(intent);
    }

}
