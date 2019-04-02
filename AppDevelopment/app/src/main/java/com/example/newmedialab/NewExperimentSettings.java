package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewExperimentSettings extends AppCompatActivity {

    public Experiment exp = new Experiment("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_experiment_settings);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");

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

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
