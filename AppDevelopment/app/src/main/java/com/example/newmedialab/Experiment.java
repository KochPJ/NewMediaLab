package com.example.newmedialab;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Experiment implements Serializable{

    String name;
    String max_repeats = "0";
    String auto_repeats = "1";
    String symbols = "0,1,2";
    String falseSymbols = "5,6,7,8,9,10,11,12,13";
    String file_name = "";
    String progressbar = "true";
    String task_msg_wrt = "";
    String final_msg_wrt = "";
    String task_msg_mct = "";
    String final_msg_mct = "";
    String qnum = "4";
    String random = "true";
    ArrayList<String> IDs = new ArrayList<String>();
    ArrayList<String> remainingSymbols = new ArrayList<String>();
    String currentSymbol = "unknown";
    int currentID = 0;
    boolean finishedShowStimuli = false;

    public Experiment(String name){
        this.name = name;
        this.file_name = (this.name+ ".txt");
    }

    public void setName(String name){
        this.name = name;
        this.file_name = (this.name+ ".txt");
    }

    public void setProgressbar(String progressbar){this.progressbar = progressbar; }

    public void setRandom(String random){this.random = random; }

    public void setMaxRepeats(String max_repeats){ this.max_repeats = max_repeats; }

    public void setAutoRepeats(String auto_repeats){ this.auto_repeats = auto_repeats; }

    public void setSymbols(String symbols){ this.symbols = symbols; }

    public void setFalseSymbols(String false_symbols) { this.falseSymbols = false_symbols;}

    public void setTask_msg_wrt(String msg) {this.task_msg_wrt = msg; }

    public void setTask_msg_mct(String msg) {this.task_msg_mct = msg; }

    public void setFinal_msg_wrt(String msg) {this.final_msg_wrt = msg; }

    public void setFinal_msg_mct(String msg) {this.final_msg_mct = msg; }

    public void setQnum(String exp_qnum) { this.qnum = exp_qnum; }

    public void addID(String id) {this.IDs.add(id); }

    public void setCurrentID(int id_num) {this.currentID = id_num; }

    public String getName(){
        return this.name;
    }

    public String getProgressbar(){
        return this.progressbar;
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

    public String getFile_name(){
        return this.file_name;
    }

    public String getRandom() {return  this.random;}

    public String getTask_msg_mct() {return this.task_msg_mct; }

    public String getTask_msg_wrt() {return this.task_msg_mct; }

    public String getFinal_msg_mct() {return this.final_msg_mct; }

    public String getFinal_msg_wrt() {return this.final_msg_wrt; }

    public String getQnum() {return this.qnum; }

    public ArrayList<String> getIDs() {return IDs; }

    public ArrayList<String> getRemainingSymbols() {return remainingSymbols; }

    public String getCurrentSymbol() {return  this.currentSymbol; }

    public Boolean finishedShowingStimuli() {return  this.finishedShowStimuli; }

    public String getID(int num){return this.IDs.get(num); }

    public int getCurrentID(){
        return this.currentID;
    }

    public void createFile(){
        //TODO: Save ID list in the .txt file
        try
        {
            //TODO:  check if external storage is available (https://developer.android.com/reference/android/os/Environment.html#getExternalStorageState()) if not, save to a different directory or output a warning to the user
            File root = new File(Environment.getExternalStorageDirectory(), "KineTest/Experiments");
            String FILE_NAME = (this.name+ ".txt");
            if (!root.exists()) root.mkdirs();
            File gpxfile = new File(root, FILE_NAME);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(name).append("\n")
                    .append(progressbar).append("\n")
                    .append(max_repeats).append("\n")
                    .append(auto_repeats).append("\n")
                    .append(symbols).append("\n") //TODO: make string of paths to videos
                    .append(qnum).append("\n")
                    .append(falseSymbols).append("\n") //TODO: make string of paths to thumbnails
                    .append(random).append("\n")
                    .append(task_msg_wrt).append("\n")
                    .append(final_msg_wrt).append("\n")
                    .append(task_msg_mct).append("\n")
                    .append(final_msg_mct).append("\n");
            //Add IDs using a dotcomma separated format
            while(IDs.size() > 0){
                String ID = IDs.get(0);
                writer.append(ID+";");
                IDs.remove(0);
            }
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }

    public String getNextStimuli() {
        //Initialize
        if(remainingSymbols.isEmpty()){
            finishedShowStimuli = false;
            for (char ch : symbols.toCharArray()){
                if(ch != ',' && ch != ' '){
                    remainingSymbols.add(String.valueOf(ch));
                }
            }
            //Shuffle stimuli if random order selected
            if(Boolean.parseBoolean(random) == true){
                Collections.shuffle(remainingSymbols);
            }
        }
        //Get next stimuli
        currentSymbol = remainingSymbols.get(0);
        //Remove from list
        remainingSymbols.remove(0);
        if(remainingSymbols.isEmpty()){
            finishedShowStimuli = true;
        }
        return currentSymbol;
    }

    public ArrayList<String> getFalseSymbol(int numberOf) {
        // Create arraylist
        ArrayList<String> fss = new ArrayList<String>();
        for (char ch : falseSymbols.toCharArray()){
            if(ch != ',' && ch != ' '){
                fss.add(String.valueOf(ch));
            }
        }

        //Shuffle stimuli
        Collections.shuffle(fss);
        ArrayList<String> fss2 = new ArrayList<String>();
        for(int i=0; i<numberOf; i++){
            fss2.add(fss.get(i));
        }
        return fss2;
    }

}
