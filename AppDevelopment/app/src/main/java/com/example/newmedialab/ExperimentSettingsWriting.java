package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ExperimentSettingsWriting extends AppCompatActivity {

    public Experiment exp = new Experiment("");
    public boolean editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_settings_writing);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
        editing = getIntent().getExtras().getBoolean("editing");
    }

    public void saveExperiment(View view) {
        //pointer to selected symbols
        EditText exp_symbols_EditText = (EditText) findViewById(R.id.te_experiment_select_symbols);
        EditText exp_repeats_EditText = (EditText) findViewById(R.id.te_experiment_repeats);
        EditText exp_repeats2_EditText = (EditText) findViewById(R.id.te_experiment_repeats2);
        CheckBox exp_random_CB = (CheckBox) findViewById(R.id.cb_stimuli_random);
        //get experiment symbols
        String exp_symbols = exp_symbols_EditText.getText().toString();
        //set selected symbols
        exp.setSymbols(exp_symbols);
        // set the other variables
        exp.setMaxRepeats(exp_repeats_EditText.getText().toString());
        exp.setAutoRepeats(exp_repeats2_EditText.getText().toString());
        // set random?
        if(exp_random_CB.isChecked()){
            exp.setRandom("true");
        } else {
            exp.setRandom("false");
        }

        //If editing return to my experiments
        if (editing){
            //Save results
            exp.createFile();
            Toast.makeText(this, "Saved Experiment to " + exp.getFile_name(),
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MyExperiments.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MultipleChoiceExperiment.class);
            intent = intent.putExtra("experiment", exp);
            intent = intent.putExtra("editing", false);
            startActivity(intent);
        }
    }

    public void previewSymbols(View view){

    }

}
