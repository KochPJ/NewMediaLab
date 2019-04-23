package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MultipleChoiceExperiment extends AppCompatActivity {

    public boolean editing = false;
    public Experiment exp = new Experiment("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_experiment);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
        editing = getIntent().getExtras().getBoolean("editing");
    }

    public void continueToSettings2(View view) {
        //pointer to the text edits
        EditText exp_task_EditText = (EditText) findViewById(R.id.task_description_message2);
        EditText exp_final_EditText = (EditText) findViewById(R.id.participation_message2);
        EditText exp_qnum_EditText = (EditText) findViewById(R.id.participation_message2);
        //get values
        String exp_task_msg = exp_task_EditText.getText().toString();
        String exp_final_msg = exp_final_EditText.getText().toString();
        String exp_qnum = exp_qnum_EditText.getText().toString();

        //If editing return to my experiments
        if (editing) {
            //Set values
            exp.setExperimentType("multiple-choice");
            exp.setMessagesMCT(exp_task_msg, exp_final_msg);
            exp.setQnum(exp_qnum);

            //Save results
            exp.createFile();
            Toast.makeText(this, "Saved Experiment to " + exp.getFile_name(),
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ExperimentSettingsMultipleChoice.class);
            startActivity(intent);
        } else {
            exp.setExperimentType("multiple-choice");
            exp.setMessagesMCT(exp_task_msg, exp_final_msg);
            exp.setQnum(exp_qnum);
            Intent intent = new Intent(this, ExperimentSettingsMultipleChoice.class);
            intent = intent.putExtra("experiment", exp);
            intent = intent.putExtra("editing", false);
            startActivity(intent);
        }
        //Reset boolean for backnavigation
        editing = false;
    }

}
