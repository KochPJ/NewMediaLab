package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

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
        if(this.exp != null){
            EditText etExpName = findViewById(R.id.te_experiment_name);
            etExpName.setText(exp.getName());
            CheckBox cb = (CheckBox)findViewById(R.id.progress_bar);
            cb.setChecked(Boolean.parseBoolean(exp.getProgressbar()));
        }
    }

    public void cancel(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void continueToSettings(View view) {
        CheckBox cb = (CheckBox)findViewById(R.id.progress_bar);
        boolean progress = cb.isChecked();
        //pointer to the text edits
        EditText exp_name_EditText = (EditText) findViewById(R.id.te_experiment_name);
        EditText exp_task_EditText = (EditText) findViewById(R.id.task_description_message);
        EditText exp_final_EditText = (EditText) findViewById(R.id.participation_message);
        //get experiment name
        String exp_name = exp_name_EditText.getText().toString();
        String exp_task_msg = exp_task_EditText.getText().toString();
        String exp_final_msg = exp_final_EditText.getText().toString();

        exp = new Experiment(exp_name);
        exp.setProgressbar(String.valueOf(progress));
        exp.setTask_msg_wrt(exp_task_msg);
        exp.setFinal_msg_wrt(exp_final_msg);
        Intent intent = new Intent(this, ExperimentSettingsWriting.class);
        intent = intent.putExtra("experiment", exp);
        intent = intent.putExtra("editing", editing);
        startActivity(intent);
    }

}
