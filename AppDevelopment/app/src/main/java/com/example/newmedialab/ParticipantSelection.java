package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class ParticipantSelection extends AppCompatActivity {

    public Experiment exp = new Experiment("");
    public Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_selection);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");

        // Fill spinner dynamically
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, exp.getIDs());

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
            }
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    public void startMultipleChoiceTest(View view){
        int pos = spinner.getSelectedItemPosition();
        exp.setCurrentID(pos);

        Intent intent = new Intent(this, TestMultipleChoice.class);
        intent = intent.putExtra("experiment", exp);
        startActivity(intent);
    }

}
