package com.example.newmedialab;

import android.content.DialogInterface;
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MyExperiments extends AppCompatActivity {

    private String[] names;
    private Experiment[] experiment_list;

    public void addNewStimuli(View view) {
        Intent startShapes = new Intent(MyExperiments.this, AddStimuli.class);
        startActivity(startShapes);
    }

    public void navigation(View view) {
        if(experiment_list == null || experiment_list.length == 0){
            Toast.makeText(MyExperiments.this, "You have not created or selected an experiment from the list",
                    Toast.LENGTH_LONG).show();
        } else {
            Spinner spinner = (Spinner) findViewById(R.id.experiment_names_spinner);
            int pos = spinner.getSelectedItemPosition();
            Experiment exp = experiment_list[pos];
            String FILE_NAME = exp.getFile_name();
            File folder = Environment.getExternalStorageDirectory();
            String file_dir = folder.getPath() + "/KineTest/Experiments/"+FILE_NAME;
            Log.d("FileName", file_dir);
            File myFile = new File(file_dir);
            switch (view.getId()) {
                case R.id.results: // Go to results view
                    // TODO: add export experimental setup
                    Intent intent3 = new Intent(this, Results.class);
                    intent3 = intent3.putExtra("experiment", exp);
                    startActivity(intent3);
                    break;
                case R.id.start_experiment: // Go to start experiment view
                    //Intent intent4 = new Intent(this, StartExperimentMain.class);
                    //intent4 = intent4.putExtra("experiment", exp);
                    //startActivity(intent4);
                    startMyExperiment(exp);
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
                case R.id.edit_experiment: // Go to MC experiment view
                    Intent intent7 = new Intent(this, MultipleChoiceExperiment.class);
                    intent7 = intent7.putExtra("experiment", exp);
                    startActivity(intent7);
                    break;
                case R.id.edit_experiment2: // Go to writing experiment view
                    Intent intent8 = new Intent(this, WritingExperiment.class);
                    intent8 = intent8.putExtra("experiment", exp);
                    intent8 = intent8.putExtra("editing", true);
                    startActivity(intent8);
                    break;
                default:
                    throw new RuntimeException("Unknown button ID");
            }
        }
    }

    public void startMyExperiment(Experiment exp) {
        final Experiment exp_final = exp;
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyExperiments.this);
        dialog.setTitle("Start Experiment:");
        dialog.setMessage("Please Select the Test Phase");
        dialog.setNegativeButton("Pre Test", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent4a = new Intent(MyExperiments.this, ParticipantInfo.class);
                intent4a = intent4a.putExtra("experiment", exp_final);
                startActivity(intent4a);
            }
        })
        .setPositiveButton("Post Test", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(exp_final.getIDs() == null || exp_final.getIDs().size() == 0){
                    Toast.makeText(MyExperiments.this, "Please run a pre test first",
                            Toast.LENGTH_LONG).show();
                } else {
                    // Create post experiment text file for the answers
                    String currentSymbol = exp_final.getCurrentSymbol();
                    int id_num = exp_final.getCurrentID();
                    String full_id = exp_final.getID(id_num);

                    //First create the folder if it doesn't exist
                    String path = "/KineTest/Experiments/" + exp_final.name + "/" + full_id +"/post_test";
                    File root = new File(Environment.getExternalStorageDirectory() + path);
                    String FILE_NAME = (exp_final.name+ ".txt");
                    if (!root.exists()) root.mkdirs();

                    //Then create the file and write header
                    File gpxfile = new File(root, FILE_NAME);
                    FileWriter writer = null;
                    try {
                        writer = new FileWriter(gpxfile);
                        writer.append("Experiment: "+ exp_final.name +"\n").append("Correct Answer \t Chosen Answer \t Options \n");
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Start post test
                    Intent intent4b = new Intent(MyExperiments.this, ParticipantSelection.class);
                    intent4b = intent4b.putExtra("experiment", exp_final);
                    startActivity(intent4b);
                }
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();
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
                            exp.setProgressbar(line);
                        }else if(c == 2) {
                            exp.setMaxRepeats(line);
                        }else if (c == 3){
                            exp.setAutoRepeats(line);
                        }else if(c == 4){
                            exp.setSymbols(line);
                        }else if(c == 5){
                            exp.setQnum(line);
                        }else if(c == 6){
                            exp.setFalseSymbols(line);
                        }else if(c == 7){
                            exp.setRandom(line);
                        }else if(c == 8){
                            exp.setTask_msg_wrt(line);
                        }else if(c == 9){
                            exp.setFinal_msg_wrt(line);
                        }else if(c == 10){
                            exp.setTask_msg_mct(line);
                        }else if(c == 11){
                            exp.setFinal_msg_mct(line);
                        }else if(c == 12){
                            String[] strParts = line.split(";");
                            for (String str : strParts) {
                                exp.addID(str);
                            }
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
                    String pb = exp.getProgressbar();
                    String max_repeats = exp.getMaxRepeats();
                    String auto_repeats = exp.getAutoRepeats();
                    String random = exp.getRandom();
                    String qnum = exp.getQnum();
                    TextView textView = (TextView) findViewById(R.id.textView_myExperiment);
                    textView.setText("Name: "+name+"\n"+"Progressbar: "+pb +"\n"+"Allowed Repeats: "+max_repeats+"\n"+"Automatic Repeats: "+auto_repeats+"\n"+"Random Stimuli Presentation: "+random+"\n"+"Number of multiple choice options: "+qnum); //set text for text view
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
