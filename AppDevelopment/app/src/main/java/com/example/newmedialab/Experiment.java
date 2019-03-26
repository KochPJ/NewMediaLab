package com.example.newmedialab;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Experiment {

    String name;

    public Experiment(String name){

        this.name = name;

    }

    public String getName(){

        return this.name;

    }

    public void createFile(){
        try
        {
            /// TO DO:  check if external storage is available (https://developer.android.com/reference/android/os/Environment.html#getExternalStorageState())
            ///         if not, save to a different directory or output a warning to the user
            File root = new File(Environment.getExternalStorageDirectory(), "Experiments");
            String FILE_NAME = (this.name+ ".txt");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, FILE_NAME);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(name);
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }

}
