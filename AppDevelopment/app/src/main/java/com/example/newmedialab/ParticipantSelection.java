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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_selection);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
    }

    public void startMultipleChoiceTest(View view){
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        exp.IDs); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
            }
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        int pos = spinner.getSelectedItemPosition();
        exp.setCurrentID(pos);

        Intent intent = new Intent(this, TestMultipleChoice.class);
        intent = intent.putExtra("experiment", exp);
        startActivity(intent);
    }

}
