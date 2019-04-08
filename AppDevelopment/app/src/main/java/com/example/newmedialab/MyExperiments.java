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
            case R.id.edit_function: // Go to function view with edit param
                Intent intent = new Intent(this, ExperimentFunction.class);
                intent = intent.putExtra("experiment", exp);
                intent = intent.putExtra("editing", true);
                startActivity(intent);
                break;
            case R.id.edit_stimuli: // Go to stimuli view with edit param
                Intent intent2 = new Intent(this, ExperimentSettings.class);
                intent2 = intent2.putExtra("experiment", exp);
                intent2 = intent2.putExtra("editing", true);
                startActivity(intent2);
                break;
            case R.id.results: // Go to results view
                // TODO: add export experimental setup
                Intent intent3 = new Intent(this, Results.class);
                intent3 = intent3.putExtra("experiment", exp);
                startActivity(intent3);
                break;
            case R.id.start_experiment: // Go to start experiment view
                Intent intent4 = new Intent(this, StartExperimentMain.class);
                intent4 = intent4.putExtra("experiment", exp);
                startActivity(intent4);
                break;
            case R.id.delete_experiment: // Delete selected experiment
                // TODO: check if no experiments in spinner (currently crashes)
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
                Intent intent5 = new Intent(this, AddStimuli.class);
                intent5 = intent5.putExtra("experiment", exp);
                startActivity(intent5);
                break;
            case R.id.view_stimuli: // Go to view stimuli view
                Intent intent6 = new Intent(this, ViewStimuli.class);
                intent6 = intent6.putExtra("experiment", exp);
                startActivity(intent6);
                break;
            case R.id.edit_experiment: // Go to edit experiment view
                Intent intent7 = new Intent(this, ExperimentType.class);
                intent7 = intent7.putExtra("experiment", exp);
                intent7 = intent7.putExtra("editing", true);
                startActivity(intent7);
                break;
             default:
                throw new RuntimeException("Unknown button ID");
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
                        }else if(c == 1) {
                            exp.setExperimentType(line);
                        }else if(c == 2) {
                            exp.setProgressbar(line);
                        }else if(c == 3) {
                            exp.setMaxRepeats(line);
                        }else if (c == 4){
                            exp.setAutoRepeats(line);
                        }else if(c == 5){
                            exp.setSymbols(line);
                        }else if(c == 6){
                            exp.setFunction(line);
                        } else if(c == 7){
                            exp.setSpeedModifier(line);
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
                    String type = exp.getExperimentType();
                    String pb = exp.getProgressbar();
                    String max_repeats = exp.getMaxRepeats();
                    String auto_repeats = exp.getAutoRepeats();
                    String symbols = exp.getSymbols();
                    String function = exp.getFunction();
                    String speed_modifier = exp.getSpeed_modifier();
                    TextView textView = (TextView) findViewById(R.id.textView_myExperiment);
                    textView.setText("Name: "+name +"\n"+"Experiment type: "+type +"\n"+"Progressbar: "+pb +"\n"+"Allowed Repeats: "+max_repeats+"\n"+"Automatic Repeats: "+auto_repeats+"\n"+"Symbols: "+symbols+"\n"+"Function: "+function+"\n"+"Playback speed: "+speed_modifier+"%"); //set text for text view
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
