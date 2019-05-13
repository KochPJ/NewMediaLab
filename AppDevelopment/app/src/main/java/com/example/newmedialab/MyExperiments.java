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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_experiments);

        /// Read experiment files
        File dir = new File(Environment.getExternalStorageDirectory(), "KineTest/Experiments/Experiment_files/");
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
                    while (sc.hasNextLine()) {
                        String line = (sc.nextLine());
                        if (c == 0) {
                            this.names[i] = line;
                            exp = new Experiment(this.names[i]);
                        } else if (c == 1) {
                            exp.setProgressbar(line);
                        } else if (c == 2) {
                            exp.setMaxRepeats(line);
                        } else if (c == 3) {
                            exp.setAutoRepeats(line);
                        } else if (c == 4) {
                            exp.setQnum(line);
                        } else if (c == 5) {
                            exp.setRandom(line);
                        } else if (c == 6) {
                            exp.setTask_msg_wrt(line);
                        } else if (c == 7) {
                            exp.setFinal_msg_wrt(line);
                        } else if (c == 8) {
                            exp.setTask_msg_mct(line);
                        } else if (c == 9) {
                            exp.setFinal_msg_mct(line);
                        } else if (c == 10){
                            exp.setNoneOption(Boolean.parseBoolean(line));
                        } else if (c == 11) { //Read paths
                            String[] strParts = line.split(",");
                            for (String str : strParts) {
                                exp.addSymbol(str, "", "", "");
                            }
                        } else if (c == 12) {
                            String[] strParts = line.split(",");
                            int index = 0;
                            for (String str : strParts) {
                                exp.directAddSymbol(str, index, 1);
                                index += 1;
                            }
                        } else if (c == 13) {
                            String[] strParts = line.split(",");
                            int index = 0;
                            for (String str : strParts) {
                                exp.directAddSymbol(str, index, 2);
                                index += 1;
                            }
                        } else if (c == 14) {
                            String[] strParts = line.split(",");
                            int index = 0;
                            for (String str : strParts) {
                                exp.directAddSymbol(str, index, 3);
                                index += 1;
                            }
                        } else if (c == 15) {
                            String[] strParts = line.split(",");
                            for (String str : strParts) {
                                exp.addFalseSymbol(str);
                            }
                        } else if (c == 16) { //Get ID's
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
                    String noneOption = Boolean.toString(exp.getNoneOption());
                    TextView textView = (TextView) findViewById(R.id.textView_myExperiment);
                    String text = "Name: " + name + "\n"
                            + "Number of Stimuli: " + exp.getStimuli().size()+ "\n"
                            + "Number of False Stimuli: " + exp.getFalseStimuli().size()+ "\n"
                            + "Allowed Repeats of Video: " + max_repeats + "\n"
                            + "Automatic Repeats of Video: " + auto_repeats + "\n"
                            + "Progressbar: " + pb + "\n"
                            + "Random Stimuli Presentation: " + random + "\n"
                            + "Number of Multiple Choice Options: " + qnum + "\n"
                            + "None of the Above Option: " + noneOption + "\n"
                            + "Number of Subjects: " + exp.getIDs().size();
                    textView.setText(text); //set text for text view
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

    public void selectExperiment(View view) {
        if (experiment_list == null || experiment_list.length == 0) {
            Toast.makeText(MyExperiments.this, "You have not created or selected an experiment from the list",
                    Toast.LENGTH_LONG).show();
        } else {
            Spinner spinner = (Spinner) findViewById(R.id.experiment_names_spinner);
            int pos = spinner.getSelectedItemPosition();
            Experiment exp = experiment_list[pos];
            String FILE_NAME = exp.getFile_name();
            File folder = Environment.getExternalStorageDirectory();
            String file_dir = folder.getPath() + "/KineTest/Experiments/Experiment_files/" + FILE_NAME;
            Log.d("FileName", file_dir);
            Intent intent = new Intent(this, ChosenExperiment.class);
            intent = intent.putExtra("experiment", exp);
            startActivity(intent);
        }
    }

    public void createNewExperiment(View view) {
        Intent activity = new Intent(MyExperiments.this, WritingExperiment.class);
        activity = activity.putExtra("editing", false);
        startActivity(activity);
    }
}
