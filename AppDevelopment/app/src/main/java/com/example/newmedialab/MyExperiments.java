package com.example.newmedialab;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import androidx.appcompat.app.AppCompatActivity;

public class MyExperiments extends AppCompatActivity {

    private String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_experiments);

        /// Read experiment files
        File dir = new File(Environment.getExternalStorageDirectory(), "KineTest/Experiments");
        File[] directoryListing = dir.listFiles();

        if (directoryListing != null) {
            int i = 0;
            this.names = new String[directoryListing.length];
            for (File child : directoryListing) {
                // Do something with child
                Scanner sc = null;
                try {
                    sc = new Scanner(child);
                    if (sc.hasNextLine()) {
                        this.names[i] = (sc.nextLine());
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                i++;
            }

        Spinner spinner = (Spinner) findViewById(R.id.experiment_names_spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        this.names); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
        }
    }

}
