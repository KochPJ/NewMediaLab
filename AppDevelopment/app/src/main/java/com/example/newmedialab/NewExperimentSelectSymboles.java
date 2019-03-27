package com.example.newmedialab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewExperimentSelectSymboles extends AppCompatActivity {

    public Experiment exp = new Experiment("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_experiment_select_symboles);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");

    }

    public void saveExperiment(View view) {
        //pointer to selected symboles
        EditText exp_symboles_EditText = (EditText) findViewById(R.id.te_experiment_select_symboles);
        //get experiment symboles
        String exp_symboles = exp_symboles_EditText.getText().toString();
        //set selected symboles
        exp.setSymboles(exp_symboles);
        exp.createFile();

        Toast.makeText(this, "Saved Experiment to " +  exp.getFile_name(),
                Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
