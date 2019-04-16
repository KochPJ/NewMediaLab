package com.example.newmedialab;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

public class Experiment implements Serializable{

    String name;
    String max_repeats = "0";
    String auto_repeats = "1";
    String speed_modifier = "100";
    String symbols = "0,1,2,3,4,5,6,7,8,9,10";
    String function = "2x";
    String file_name = "";
    String progressbar = "false";
    String experiment_type = "unknown";
    String task_msg, final_msg = "";
    String qnum = "";


    public Experiment(String name){
        this.name = name;
        this.file_name = (this.name+ ".txt");
    }

    public void setName(String name){
        this.name = name;
        this.file_name = (this.name+ ".txt");
    }

    public void setExperimentType(String experiment_type){
        this.experiment_type = experiment_type;
    }

    public void setProgressbar(String progressbar){
        this.progressbar = progressbar;
    }

    public void setMaxRepeats(String max_repeats){
        this.max_repeats = max_repeats;
    }

    public void setAutoRepeats(String auto_repeats){
        this.auto_repeats = auto_repeats;
    }

    public void setSymbols(String symbols){
        this.symbols = symbols;
    }

    public void setFunction(String function){
        this.function = function;
    }

    public void setSpeedModifier(String speed_modifier){
        this.speed_modifier = speed_modifier;
    }

    public void setMessages(String exp_task_msg, String exp_final_msg) {this.task_msg = exp_task_msg; this.final_msg = exp_final_msg; }

    public void setQnum(String exp_qnum) { this.qnum = exp_qnum; }

    public String getName(){
        return this.name;
    }

    public String getProgressbar(){
        return this.progressbar;
    }

    public String getExperimentType(){
        return this.experiment_type;
    }

    public String getMaxRepeats(){
        return this.max_repeats;
    }

    public String getAutoRepeats(){
        return this.auto_repeats;
    }

    public String getSymbols(){
        return this.symbols;
    }

    public String getFunction(){
        return this.function;
    }

    public String getFile_name(){
        return this.file_name;
    }

    public String getSpeed_modifier(){
        return this.speed_modifier;
    }

    public String getTask_msg() {return this.task_msg; }

    public String getFinal_msg() {return this.final_msg; }

    public String getQnum() {return this.qnum; }

    public void createFile(){

        // convert array of symboles to string
        try
        {
            /// TODO:  check if external storage is available (https://developer.android.com/reference/android/os/Environment.html#getExternalStorageState()) if not, save to a different directory or output a warning to the user
            File root = new File(Environment.getExternalStorageDirectory(), "KineTest/Experiments");
            String FILE_NAME = (this.name+ ".txt");
            if (!root.exists()) root.mkdirs();
            File gpxfile = new File(root, FILE_NAME);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(name).append("\n").append(experiment_type).append("\n").append(progressbar).append("\n").append(max_repeats).append("\n").append(auto_repeats).append("\n").append(symbols).append("\n").append(function).append("\n").append(speed_modifier);

            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }

}
