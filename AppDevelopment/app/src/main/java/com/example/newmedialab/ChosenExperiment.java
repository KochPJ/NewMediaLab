package com.example.newmedialab;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ChosenExperiment extends AppCompatActivity {

    public Experiment exp = new Experiment("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_experiment);
        Intent i = getIntent();
        exp = (Experiment) i.getSerializableExtra("experiment");
        String name = exp.getName();
        String pb = exp.getProgressbar();
        String max_repeats = exp.getMaxRepeats();
        String auto_repeats = exp.getAutoRepeats();
        String random = exp.getRandom();
        String qnum = exp.getQnum();
        String noneOption = Boolean.toString(exp.getNoneOption());
        TextView textView = (TextView) findViewById(R.id.textView_myExperiment2);
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

    public void navigation(View view) {
        String FILE_NAME = exp.getFile_name();
        File folder = Environment.getExternalStorageDirectory();
        String file_dir = folder.getPath() + "/KineTest/Experiments/Experiment_files/" + FILE_NAME;
        Log.d("FileName", file_dir);
        File myFile = new File(file_dir);
        switch (view.getId()) {
            case R.id.results: // Go to results view
                Intent intent1 = new Intent(this, Results.class);
                intent1 = intent1.putExtra("experiment", exp);
                startActivity(intent1);
                break;
            case R.id.start_experiment: // Go to start experiment view
                RadioGroup rg = findViewById(R.id.RadioGroup);
                if(rg.getCheckedRadioButtonId() == R.id.radioButton4){ //MC test
                    startMyExperiment(exp);
                } else {
                    Intent intent4a = new Intent(ChosenExperiment.this, ParticipantInfo.class);
                    intent4a = intent4a.putExtra("experiment", exp);
                    intent4a = intent4a.putExtra("writing", true);
                    startActivity(intent4a);
                }
                break;
            case R.id.delete_experiment: // Delete selected experiment
                if (myFile.exists()) {
                    deleteMyExperiment();
                } else {
                    Toast.makeText(this, FILE_NAME + " does not exist",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.view_stimuli: // Go to view stimuli view
                Intent intent2 = new Intent(this, ViewStimuli.class);
                intent2 = intent2.putExtra("experiment", exp);
                startActivity(intent2);
                break;
            //case R.id.edit_experiment: // Go to MC experiment view
            //    Intent intent7 = new Intent(this, MultipleChoiceExperiment.class);
            //    intent7 = intent7.putExtra("experiment", exp);
            //    startActivity(intent7);
            //    break;
            //case R.id.edit_experiment2: // Go to writing experiment view
            //    Intent intent8 = new Intent(this, WritingExperiment.class);
            //    intent8 = intent8.putExtra("experiment", exp);
            //    intent8 = intent8.putExtra("editing", true);
            //    startActivity(intent8);
            //    break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
    }

    public void deleteMyExperiment (){
        AlertDialog.Builder dialog = new AlertDialog.Builder(ChosenExperiment.this);
        dialog.setTitle("Delete");
        dialog.setMessage("Are you sure you want to delete this experiment?");
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(ChosenExperiment.this, "Experiment not removed", Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exp.deleteExperiment();
                Intent intent5 = new Intent(ChosenExperiment.this, MyExperiments.class);
                startActivity(intent5);
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    public void startMyExperiment (Experiment exp){
        final Experiment exp_final = exp;
        AlertDialog.Builder dialog = new AlertDialog.Builder(ChosenExperiment.this);
        dialog.setTitle("Start Experiment:");
        dialog.setMessage("Please Select the Test Phase");
        dialog.setNegativeButton("Pre Test", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent4a = new Intent(ChosenExperiment.this, ParticipantInfo.class);
                intent4a = intent4a.putExtra("experiment", exp_final);
                intent4a = intent4a.putExtra("writing", false);
                startActivity(intent4a);
            }
        })
                .setPositiveButton("Post Test", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (exp_final.getIDs() == null || exp_final.getIDs().size() == 0) {
                            Toast.makeText(ChosenExperiment.this, "Please run a pre test first",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            //Start post test
                            Intent intent4b = new Intent(ChosenExperiment.this, ParticipantSelection.class);
                            intent4b = intent4b.putExtra("experiment", exp_final);
                            startActivity(intent4b);
                        }
                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }
}
