package com.example.newmedialab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import java.io.Serializable;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

public class NewExperiments extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_experiments);
    }

    public void createNewExperiment(View view) {
        //pointer to the text edit
        EditText exp_name_EditText = (EditText) findViewById(R.id.te_experiment_name);
        EditText exp_repeats_EditText = (EditText) findViewById(R.id.te_experiment_repeats);
        EditText exp_function_EditText = (EditText) findViewById(R.id.te_experiment_function);
        //get experiment name
        String exp_name = exp_name_EditText.getText().toString();
        // create new experiment
        Experiment exp = new Experiment(exp_name);
        // set the other variables
        exp.setFunction(exp_function_EditText.getText().toString());
        exp.setRepeats(exp_repeats_EditText.getText().toString());

        Log.d("get repeats : ", exp.getRepeats());


        Intent intent = new Intent(this, NewExperimentSelectSymboles.class);
        Intent i = intent.putExtra("experiment", exp);
        startActivity(i);

        //exp.createFile();

    }

}
