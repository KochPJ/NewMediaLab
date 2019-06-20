package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ParticipantInfo extends AppCompatActivity {

    public Experiment exp = new Experiment("");
    public Boolean writing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_info);
        Intent i = getIntent();
        exp = (Experiment) i.getSerializableExtra("experiment");
        writing = (Boolean) i.getSerializableExtra("writing");
    }

    public void addID(View view) {
        EditText etID = (EditText) findViewById(R.id.et_participantID);
        CheckBox control = findViewById(R.id.control);
        String id = etID.getText().toString();
        if(exp.getTrueIDs().size() == 0){
            exp.addID("NaS"); //add dummy entry to ensure correct placement
        }
        if(control.isChecked()){
            if(exp.getTrueIDs().size()%2 == 0){
                exp.addID("NaS"); //add dummy entry to ensure correct placement
                exp.addID(id);
                exp.setCurrentID(exp.getTrueIDs().size()-1);
            } else {
                exp.addID(id);
                exp.setCurrentID(exp.getTrueIDs().size()-1);
            }
        } else {
            if(exp.getTrueIDs().size()%2 == 1){
                exp.addID("NaS"); //add dummy entry to ensure correct placement
                exp.addID(id);
                exp.setCurrentID(exp.getTrueIDs().size()-1);
            } else {
                exp.addID(id);
                exp.setCurrentID(exp.getTrueIDs().size()-1);
            }
        }
        Intent intent = new Intent(this, ShowStimuli.class);
        intent = intent.putExtra("experiment", exp);
        intent = intent.putExtra("writing", writing);
        startActivity(intent);
    }
}
