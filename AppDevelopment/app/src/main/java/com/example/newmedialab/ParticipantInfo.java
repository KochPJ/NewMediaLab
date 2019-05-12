package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
       String id = etID.getText().toString();
       exp.addID(id);
       exp.setCurrentID(exp.getIDs().size()-1);
       Intent intent = new Intent(this, ShowStimuli.class);
       intent = intent.putExtra("experiment", exp);
       intent = intent.putExtra("writing", writing);
       startActivity(intent);
    }
}
