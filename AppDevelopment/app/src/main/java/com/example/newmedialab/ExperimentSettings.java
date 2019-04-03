package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ExperimentSettings extends AppCompatActivity {

    public Experiment exp = new Experiment("");
    public boolean editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_settings);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
        editing = getIntent().getExtras().getBoolean("editing");
    }

    public void saveExperiment(View view) {
        //pointer to selected symbols
        EditText exp_symbols_EditText = (EditText) findViewById(R.id.te_experiment_select_symbols);
        EditText exp_repeats_EditText = (EditText) findViewById(R.id.te_experiment_repeats);
        //get experiment symbols
        String exp_symbols = exp_symbols_EditText.getText().toString();
        //set selected symbols
        exp.setSymbols(exp_symbols);
        // set the other variables
        exp.setRepeats(exp_repeats_EditText.getText().toString());
        exp.createFile();

        Toast.makeText(this, "Saved Experiment to " +  exp.getFile_name(),
                Toast.LENGTH_LONG).show();

        //If editing return to my experiments
        if (editing){
            Intent intent = new Intent(this, MyExperiments.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        //Reset boolean for backnavigation
        editing = false;

    }

}
