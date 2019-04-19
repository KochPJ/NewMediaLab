package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.jcodec.containers.mp4.boxes.Edit;

import androidx.appcompat.app.AppCompatActivity;

public class ParticipantInfo extends AppCompatActivity {

    public Experiment exp = new Experiment("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_info);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
    }

    public void addID(View view) {
       EditText etID = (EditText) findViewById(R.id.et_participantID);
       String ID = etID.getText().toString();
       Intent intent = new Intent(this, ShowStimuli.class);
       intent = intent.putExtra("experiment", exp);
       startActivity(intent);
    }
}
