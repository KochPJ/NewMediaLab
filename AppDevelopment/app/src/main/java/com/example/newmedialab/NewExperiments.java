package com.example.newmedialab;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

public class NewExperiments extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_experiments);
    }

    public void createNewExperiment(View view) {
        EditText simpleEditText = (EditText) findViewById(R.id.exp_name_input_textbox);
        String exp_name = simpleEditText.getText().toString();

        Experiment exp = new Experiment(exp_name);

        Context context = this.getApplicationContext();
        File path = context.getFilesDir();

        exp.createFile();

    }

}
