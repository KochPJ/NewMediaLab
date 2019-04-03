package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import androidx.appcompat.app.AppCompatActivity;

public class MyExperiments extends AppCompatActivity {

    private String[] names;
    private Experiment[] experiment_list;

    public void startExperiment(View view) {
        Intent startShapes = new Intent(MyExperiments.this, playVideo.class);
        startActivity(startShapes);
    }

    public void navigation(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.experiment_names_spinner);
        int pos = spinner.getSelectedItemPosition();
        Experiment exp = experiment_list[pos];
        String FILE_NAME = exp.getFile_name();
        File folder = Environment.getExternalStorageDirectory();
        String file_dir = folder.getPath() + "/KineTest/Experiments/"+FILE_NAME;
        Log.d("FileName", file_dir);
        File myFile = new File(file_dir);
        switch (view.getId()) {
            case R.id.edit_experiment: // Go to edit experiment view
                Intent intent = new Intent(this, NewExperimentFunction.class);
                intent = intent.putExtra("experiment", exp);
                startActivity(intent);
                break;
            case R.id.edit_stimuli: // Go to edit stimuli view
                //Intent intent = new Intent(this, editStimuli.class);
                //intent = intent.putExtra("experiment", exp);
                //startActivity(intent);
                break;
            case R.id.results: // Go to results view
                //Intent intent = new Intent(this, results.class);
                //intent = intent.putExtra("experiment", exp);
                //startActivity(intent);
                break;
            case R.id.start_experiment: // Go to start experiment view
                //Intent intent = new Intent(this, startExperiment.class);
                //intent = intent.putExtra("experiment", exp);
                //startActivity(intent);
                break;
            case R.id.delete_experiment: // Delete selected experiment
                if(myFile.exists()) {
                    myFile.delete();
                    Toast.makeText(this, "Deleted Experiment " +  FILE_NAME,
                        Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this,  FILE_NAME + " does not exist",
                        Toast.LENGTH_LONG).show();
                }
                Intent startShapes = new Intent(MyExperiments.this, MyExperiments.class);
                startActivity(startShapes);
                break;
            case R.id.add_stimuli: // Go to add stimuli view
                //Intent intent = new Intent(this, addStimuli.class);
                //intent = intent.putExtra("experiment", exp);
                //startActivity(intent);
                break;
            case R.id.view_stimuli: // Go to view stimuli view
                //Intent intent = new Intent(this, viewStimuli.class);
                //intent = intent.putExtra("experiment", exp);
                //startActivity(intent);
                break;
             default:
                throw new RuntimeException("Unknow button ID");
        }
    }

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
            this.experiment_list = new Experiment[directoryListing.length];
            for (File child : directoryListing) {
                // Do something with child
                Scanner sc = null;
                try {
                    sc = new Scanner(child);
                    int c = 0;
                    Experiment exp = new Experiment("");
                    while(sc.hasNextLine()){
                        String line = (sc.nextLine());
                        if(c == 0){
                            this.names[i] = line;
                            exp = new Experiment(this.names[i]);
                        }else if(c == 1){
                            exp.setRepeats(line);
                        }else if(c == 2){
                            exp.setSymbols(line);
                        }else if(c == 3){
                            exp.setFunction(line);
                        }
                        this.experiment_list[i] = exp;
                        c++;
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
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    Experiment exp = experiment_list[position];
                    String name = exp.getName();
                    String repeats = exp.getRepeats();
                    String symbols = exp.getSymbols();
                    String function = exp.getFunction();
                    TextView textView = (TextView) findViewById(R.id.textView_myExperiment);
                    textView.setText("Name: "+name +"\n"+"Allowed Repeats: "+repeats+"\n"+"Symbols: "+symbols+"\n"+"Function: "+function); //set text for text view
                }
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
        }
    }



}
