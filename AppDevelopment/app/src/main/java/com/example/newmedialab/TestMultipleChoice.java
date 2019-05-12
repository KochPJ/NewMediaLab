package com.example.newmedialab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class TestMultipleChoice extends AppCompatActivity {

    public Experiment exp = new Experiment("");
    public String[] stimuli;
    public ImageView im1, im2, im3, im4, im5, im6;
    public TextView tv1, tv2, tv3, tv4, tv5, tv6;
    public ArrayList<String> falseSymbols = new ArrayList<String>();
    public boolean pretest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_multiple_choice);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
        pretest = (boolean) i.getSerializableExtra("pretest");

        //Get the stimuli
        stimuli = exp.getNextStimuli();

        //Set progressbar with correct value
        ProgressBar pb = findViewById(R.id.progressBar3);
        if(Boolean.parseBoolean(exp.getProgressbar())){
            int maxSymbols = exp.getStimuli().size();
            pb.setMax(maxSymbols+1);
            pb.setProgress(maxSymbols - exp.getRemainingStimuli().size());
        } else {
            pb.setAlpha(0);
        }

        //Set messages
        TextView expName = (TextView) findViewById(R.id.textView11);
        expName.setText(exp.getName());
        TextView expName2 = (TextView) findViewById(R.id.textView12);
        expName2.setText(exp.getTask_msg_mct());

        // Find imageviews
        im1 = findViewById(R.id.imageView);
        im2 = findViewById(R.id.imageView2);
        im3 = findViewById(R.id.imageView3);
        im4 = findViewById(R.id.imageView4);
        im5 = findViewById(R.id.imageView5);
        im6 = findViewById(R.id.imageView6);
        ImageView[] imageViews = {im1, im2, im3, im4, im5, im6};

        // Find textviews
        tv1 = findViewById(R.id.textView15);
        tv2 = findViewById(R.id.textView16);
        tv3 = findViewById(R.id.textView17);
        tv4 = findViewById(R.id.textView18);
        tv5 = findViewById(R.id.textView19);
        tv6 = findViewById(R.id.textView20);

        List<String> spinnerArray =  new ArrayList<String>();
        // Randomly sample from correct group of false stimuli
        falseSymbols = exp.getFalseSymbol(Integer.parseInt(exp.qnum)-1);

        if(exp.getCurrentID()%2 == 0){ // Control group
            // Add true stimuli
            falseSymbols.add(Environment.getExternalStorageDirectory()+"/"+exp.getCurrentSymbol()[2]);
        } else { // Experimental group
            // Add true stimuli
            falseSymbols.add(Environment.getExternalStorageDirectory()+"/"+exp.getCurrentSymbol()[3]);
        }

        Collections.shuffle(falseSymbols);
        for(int j=0; j<falseSymbols.size(); j++){
            File imgFile = new  File(falseSymbols.get(j));
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageViews[j].setImageBitmap(myBitmap);
            }
        }

        if(exp.getNoneOption()){
            falseSymbols.add("None of the above");
        }

        // Load thumbnails & remove the required number of imageViews and textViews
        if(Integer.parseInt(exp.getQnum()) <= 2){
            im3.setAlpha(0);
            im4.setAlpha(0);
            im5.setAlpha(0);
            im6.setAlpha(0);
            tv3.setAlpha(0);
            tv4.setAlpha(0);
            tv5.setAlpha(0);
            tv6.setAlpha(0);
        } if(Integer.parseInt(exp.getQnum()) == 3){
            im4.setAlpha(0);
            im5.setAlpha(0);
            im6.setAlpha(0);
            tv4.setAlpha(0);
            tv5.setAlpha(0);
            tv6.setAlpha(0);
        } if(Integer.parseInt(exp.getQnum()) == 4){
            im5.setAlpha(0);
            im6.setAlpha(0);
            tv5.setAlpha(0);
            tv6.setAlpha(0);
        } if(Integer.parseInt(exp.getQnum()) == 5){
            im6.setAlpha(0);
            tv6.setAlpha(0);
        }

        // Fill spinner
        for(int j=0; j<Integer.parseInt(exp.qnum); j++){
            spinnerArray.add(Integer.toString(j+1));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner2);
        sItems.setAdapter(adapter);
    }

    public void nextQuestion(View view) {
        // Get answer
        Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        String[] currentSymbol = exp.getCurrentSymbol();

        // Create post experiment text file for the answers
        int id_num = exp.getCurrentID();
        String full_id = exp.getID(id_num);

        //First create the folder if it doesn't exist
        String subdir = "";
        if(pretest){
            subdir = "/KineTest/Experiments/" + exp.name + "/" + full_id +"/multiple_choice_test/pre_test";
        } else {
            subdir = "/KineTest/Experiments/" + exp.name + "/" + full_id +"/multiple_choice_test/post_test";
        }
        File root = new File(Environment.getExternalStorageDirectory() + subdir);
        String FILE_NAME = (exp.name +"_"+full_id+"_results.txt");
        if (!root.exists()){
            root.mkdirs();
            //Then create the file and write header
            File gpxfile = new File(root, FILE_NAME);
            FileWriter writer = null;
            try {
                writer = new FileWriter(gpxfile);
                writer.append("Experiment: "+ exp.name +"\n").append("Correct Answer \t Chosen Position \t Correct Position \t Options \n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Path to file
        String path = "";
        if(pretest){
            path = "/KineTest/Experiments/" + exp.name + "/" + full_id +"/multiple_choice_test/pre_test/"+ exp.name +"_"+full_id+"_results.txt";
        } else {
            path = "/KineTest/Experiments/" + exp.name + "/" + full_id +"/multiple_choice_test/post_test/"+ exp.name +"_"+full_id+"_results.txt";
        }
        File fullpath = new File(Environment.getExternalStorageDirectory() + path);

        //Append answers to file once it exists
        try {
            FileOutputStream fOut = new FileOutputStream(fullpath, true);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            if(exp.getCurrentID()%2 == 0) { // Control group
                osw.write(Boolean.toString(spinner.getSelectedItemPosition() == falseSymbols.indexOf(Environment.getExternalStorageDirectory()+"/"+exp.getCurrentSymbol()[2]))
                        +" \t "+ spinner.getSelectedItemPosition() + "\t" + falseSymbols.indexOf(Environment.getExternalStorageDirectory()+"/"+exp.getCurrentSymbol()[2]) + "\t");
            } else {
                osw.write(Boolean.toString(spinner.getSelectedItemPosition() == falseSymbols.indexOf(Environment.getExternalStorageDirectory()+"/"+exp.getCurrentSymbol()[3]))
                        +" \t "+ spinner.getSelectedItemPosition() + "\t"+ falseSymbols.indexOf(Environment.getExternalStorageDirectory()+"/"+exp.getCurrentSymbol()[3]) + "\t");
            }
            for(int j=0; j<Integer.parseInt(exp.qnum); j++){
                osw.write(falseSymbols.get(j) + ",");
            }
            osw.write("\n");
            osw.flush();
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if this was the last stimuli
        if (exp.finishedShowingStimuli()) {
            // Save changes to experiment
            exp.createFile();
            Toast.makeText(TestMultipleChoice.this, "Finished test, saved results \n" + exp.getFinal_msg_mct(),
                    Toast.LENGTH_LONG).show();
            // Return to MyExperiments
            Intent intent = new Intent(this, MyExperiments.class);
            intent = intent.putExtra("experiment", exp);
            startActivity(intent);
        } else {
            // Show next stimuli
            Intent intent2 = new Intent(this, TestMultipleChoice.class);
            intent2 = intent2.putExtra("experiment", exp);
            intent2 = intent2.putExtra("pretest", pretest);
            startActivity(intent2);
        }
    }
}