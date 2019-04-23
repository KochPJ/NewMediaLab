package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ExperimentSettingsMultipleChoice extends AppCompatActivity {

    public Experiment exp = new Experiment("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_settings_multiplechoice);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
        //TODO: ensure that for the false symbols only those that were not shown are available
    }

    public void saveExperiment2(View view) {
        //pointer to selected symbols
        EditText exp_symbols_EditText = (EditText) findViewById(R.id.te_experiment_select_symbols2);
        //get experiment symbols
        String exp_symbols = exp_symbols_EditText.getText().toString();
        //set selected symbols
        exp.setFalseSymbols(exp_symbols);

        // Save and return to my experiments
        exp.createFile();
        Intent intent = new Intent(this, MyExperiments.class);
        intent = intent.putExtra("experiment", exp);
        startActivity(intent);

    }

    public void previewFalseSymbols(View view){

    }

}