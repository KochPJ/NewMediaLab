package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class ExperimentSettingsWriting extends AppCompatActivity {

    public Experiment exp;
    public boolean editing = false;
    String convertedVideoSavingPath;

    Spinner symbolesSpinner;
    private String [] symboles;
    String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_settings_writing);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
        editing = getIntent().getExtras().getBoolean("editing");



        convertedVideoSavingPath = Environment.getExternalStorageDirectory() + "/KineTest/Experiments/"+exp.name+"/videos_transformed";
        File folder = new File(convertedVideoSavingPath);
        if(!folder.exists()) folder.mkdirs();

        updateSpinner();


    }



    private void updateSpinner(){
        symboles = new String[exp.stimuli.size()];
        ArrayList<String[]> stimuli = exp.stimuli;
        for(int i = 0; i < exp.stimuli.size(); i++){
            String[] stim = exp.stimuli.get(i);
            symboles[i] = stim[1];
        }


        symbolesSpinner = (Spinner) findViewById(R.id.expSettings_symboles_spinner);
        ArrayAdapter<String> spinnerArrayAdapterSymbol = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        this.symboles); //selected item will look like a spinner set from XML
        spinnerArrayAdapterSymbol.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        symbolesSpinner.setAdapter(spinnerArrayAdapterSymbol);
        symbolesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                symbol = symboles[position];
            }
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });


    }

    public void cancel(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void deleteSymbol(View view){

    }

    public void saveExperiment(View view) {
        //pointer to selected symbols
        EditText exp_repeats_EditText = (EditText) findViewById(R.id.te_experiment_repeats);
        EditText exp_repeats2_EditText = (EditText) findViewById(R.id.te_experiment_repeats2);
        CheckBox exp_random_CB = (CheckBox) findViewById(R.id.cb_stimuli_random);

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
        Intent intent = new Intent(this, AddStimuli.class);
        intent = intent.putExtra("experiment", exp);
        intent = intent.putExtra("editing", editing);
        startActivity(intent);
    }

}
