package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WritingExperiment extends AppCompatActivity {

    public boolean editing = false;
    public Experiment exp = new Experiment("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_experiment);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
        editing = getIntent().getExtras().getBoolean("editing");
        if(exp.getName() != ""){
            EditText etExpName = findViewById(R.id.te_experiment_name);
            etExpName.setText(exp.getName());
            CheckBox cb = (CheckBox)findViewById(R.id.progress_bar);
            cb.setChecked(Boolean.parseBoolean(exp.getProgressbar()));
        }
    }

    public void navigation(View view) {
        //TODO: save question parameters in experiment
        CheckBox cb = (CheckBox)findViewById(R.id.progress_bar);
        boolean progress = cb.isChecked();
        switch (view.getId()) {
            case R.id.save_button: // Go to function view with edit param
                //pointer to the text edit
                EditText exp_name_EditText = (EditText) findViewById(R.id.te_experiment_name);
                //get experiment name
                String exp_name = exp_name_EditText.getText().toString();

                //If editing return to my experiments
                if (editing) {
                    //Set values
                    exp.setName(exp_name);
                    exp.setProgressbar(String.valueOf(progress));
                    exp.setExperimentType("writing");

                    //Save results
                    exp.createFile();
                    Toast.makeText(this, "Saved Experiment to " + exp.getFile_name(),
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, MyExperiments.class);
                    startActivity(intent);
                } else {
                    exp = new Experiment(exp_name);
                    exp.setProgressbar(String.valueOf(progress));
                    exp.setExperimentType("writing");
                    Intent intent = new Intent(this, ExperimentFunction.class);
                    intent = intent.putExtra("experiment", exp);
                    intent = intent.putExtra("editing", editing);
                    startActivity(intent);
                }
                break;
            case R.id.preview_button: // Go to preview view
                //TODO: create preview view
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
    }

}
