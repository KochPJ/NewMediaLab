package com.example.newmedialab;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

public class Experiment implements Serializable{

    String name;
    String repeats = "0";
    String symbols = "0,1,2,3,4,5,6,7,8,9,10";
    String function = "2x";
    String file_name = "";


    public Experiment(String name){
        this.name = name;
        this.file_name = (this.name+ ".txt");
    }

    public void setRepeats(String repeats){
        this.repeats = repeats;
    }

    public void setSymbols(String symbols){
        this.symbols = symbols;
    }

    public void setFunction(String function){
        this.function = function;
    }

    public String getName(){
        return this.name;
    }

    public String getRepeats(){
        return this.repeats;
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
            writer.append(name).append("\n").append(repeats).append("\n").append(symbols).append("\n").append(function);

            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }

}
