package com.example.newmedialab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
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
import androidx.core.content.ContextCompat;

public class TestMultipleChoice extends AppCompatActivity {

    public Experiment exp = new Experiment("");
    public String[] stimuli;
    public ImageView im1, im2, im3, im4, im5, im6;
    public TextView tv1, tv2, tv3, tv4, tv5, tv6;
    public ArrayList<String> falseSymbols = new ArrayList<String>();
    public ImageView[] imageViews;
    public boolean pretest = false;
    public int prevTint = 0;

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
        im1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Spinner sItems = (Spinner) findViewById(R.id.spinner2);
                setTint(prevTint,0);
                prevTint = 0;
                sItems.setSelection(0);
            }
        });
        im2 = findViewById(R.id.imageView2);
        im2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Spinner sItems = (Spinner) findViewById(R.id.spinner2);
                setTint(prevTint, 1);
                prevTint = 1;
                sItems.setSelection(1);
            }
        });
        im3 = findViewById(R.id.imageView3);
        if(Integer.parseInt(exp.getQnum()) >= 3){
            im3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Spinner sItems = (Spinner) findViewById(R.id.spinner2);
                    setTint(prevTint, 2);
                    prevTint = 2;
                    sItems.setSelection(2);
                }
            });
        }
        im4 = findViewById(R.id.imageView4);
        if(Integer.parseInt(exp.getQnum()) >= 4) {
            im4.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Spinner sItems = (Spinner) findViewById(R.id.spinner2);
                    setTint(prevTint, 3);
                    prevTint = 3;
                    sItems.setSelection(3);
                }
            });
        }
        im5 = findViewById(R.id.imageView5);
        if(Integer.parseInt(exp.getQnum()) >= 5) {
            im5.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Spinner sItems = (Spinner) findViewById(R.id.spinner2);
                    setTint(prevTint, 4);
                    prevTint = 4;
                    sItems.setSelection(4);
                }
            });
        }
        im6 = findViewById(R.id.imageView6);
        if(Integer.parseInt(exp.getQnum()) == 6) {
            im6.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Spinner sItems = (Spinner) findViewById(R.id.spinner2);
                    setTint(prevTint, 5);
                    prevTint = 5;
                    sItems.setSelection(5);
                }
            });
        }
        this.imageViews = new ImageView[]{im1, im2, im3, im4, im5, im6};

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
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, 100, 100);

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
            spinnerArray.add("Symbol "+Integer.toString(j+1));
        }
        // Add none option
        if(exp.getNoneOption()){
            spinnerArray.add("None of the above");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner2);
        sItems.setAdapter(adapter);
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setTint(prevTint,position);
                prevTint = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        //Start spinner with none-of-the-above if that option was selected
        if(exp.getNoneOption()){
            sItems.setSelection(spinnerArray.size()-1);
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public void setTint(int prevTint, int newTint){
        ImageView[] images = new ImageView[]{im1, im2, im3, im4, im5, im6};
        if(prevTint < 6){ images[prevTint].clearColorFilter(); }
        if(newTint < 6){ images[newTint].setColorFilter(ContextCompat.getColor(TestMultipleChoice.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY); }
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
            // Force resetting of bitmaps to prevent OOM error
            for(ImageView im : this.imageViews){
                im.setImageBitmap(null);
            }
            // Return to MyExperiments
            Intent intent = new Intent(this, MyExperiments.class);
            intent = intent.putExtra("experiment", exp);
            startActivity(intent);
        } else {
            // Force resetting of bitmaps to prevent OOM error
            for(ImageView im : this.imageViews){
                im.setImageBitmap(null);
            }
            // Show next stimuli
            Intent intent2 = new Intent(this, TestMultipleChoice.class);
            intent2 = intent2.putExtra("experiment", exp);
            intent2 = intent2.putExtra("pretest", pretest);
            startActivity(intent2);
        }
    }
}