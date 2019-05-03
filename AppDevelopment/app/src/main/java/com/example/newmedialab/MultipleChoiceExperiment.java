package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MultipleChoiceExperiment extends AppCompatActivity {

    public Experiment exp = new Experiment("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_experiment);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
    }

    public void cancel(View view){
        exp.deleteExperiment();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void continueToSettings2(View view) {
        //pointer to the text edits
        EditText exp_task_EditText = (EditText) findViewById(R.id.task_description_message2);
        EditText exp_final_EditText = (EditText) findViewById(R.id.participation_message2);
        EditText exp_qnum_EditText = (EditText) findViewById(R.id.te_number_of_questions);
        //get values
        String exp_task_msg = exp_task_EditText.getText().toString();
        String exp_final_msg = exp_final_EditText.getText().toString();
        String exp_qnum = exp_qnum_EditText.getText().toString();

        exp.setTask_msg_mct(exp_task_msg);
        exp.setFinal_msg_mct(exp_final_msg);
        exp.setQnum(exp_qnum);
        Intent intent = new Intent(this, ExperimentSettingsMultipleChoice.class);
        intent = intent.putExtra("experiment", exp);
        startActivity(intent);
    }

}
