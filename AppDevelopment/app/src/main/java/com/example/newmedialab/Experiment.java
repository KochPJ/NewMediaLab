package com.example.newmedialab;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Experiment implements Serializable{

    String name;
    String max_repeats = "0";
    String auto_repeats = "1";
    String file_name = "";
    String progressbar = "true";
    String task_msg_wrt = "";
    String final_msg_wrt = "";
    String task_msg_mct = "";
    String final_msg_mct = "";
    String qnum = "4";
    String random = "true";
    ArrayList<String> IDs = new ArrayList<String>();
    ArrayList<String[]> remainingStimuli = new ArrayList<String[]>();
    ArrayList<String[]> stimuli = new ArrayList<String[]>();
    ArrayList<String> falseKinematicStimuli = new ArrayList<String>();
    ArrayList<String> falseArtificialStimuli = new ArrayList<String>();
    String[] currentSymbol;
    int currentID = 0;
    boolean finishedShowStimuli = true;

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

    public void addSymbol(String artificialVideoLink, String kinesteticVideoLink, String lastArtificialImage, String lastKinesteticImageLink){
        String[] stimArray = {artificialVideoLink, kinesteticVideoLink, lastArtificialImage, lastKinesteticImageLink};
        this.stimuli.add(stimArray);
    }

    public void directAddSymbol(String path, int index1, int index2){ //Necessary for myExperiments
        stimuli.get(index1)[index2] = path;
    }

    public void addFalseKinematicSymbol(String lastImagePath){ falseKinematicStimuli.add(lastImagePath); }

    public void addFalseArtificialSymbol(String lastImagePath){ falseArtificialStimuli.add(lastImagePath); }

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

    public ArrayList<String[]> getStimuli() { return this.stimuli; }

    public String getProgressbar(){
        return this.progressbar;
    }

    public String getMaxRepeats(){
        return this.max_repeats;
    }

    public String getAutoRepeats(){
        return this.auto_repeats;
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

    public ArrayList<String[]> getRemainingStimuli() {return remainingStimuli; }

    public String[] getCurrentSymbol() {return  this.currentSymbol; }

    public Boolean finishedShowingStimuli() {return  this.finishedShowStimuli; }

    public String getID(int num){return this.IDs.get(num); }

    public int getCurrentID(){
        return this.currentID;
    }

    public void createFile(){
        try
        {
            //TODO:  check if external storage is available (https://developer.android.com/reference/android/os/Environment.html#getExternalStorageState()) if not, save to a different directory or output a warning to the user
            File root = new File(Environment.getExternalStorageDirectory(), "KineTest/Experiments/Experiment_files/");
            String FILE_NAME = (this.name+ ".txt");
            if (!root.exists()) root.mkdirs();
            File gpxfile = new File(root, FILE_NAME);
            FileWriter writer = new FileWriter(gpxfile);

            // Add general experiment parameters
            writer.append(name).append("\n")
                    .append(progressbar).append("\n")
                    .append(max_repeats).append("\n")
                    .append(auto_repeats).append("\n")
                    .append(qnum).append("\n")
                    .append(random).append("\n")
                    .append(task_msg_wrt).append("\n")
                    .append(final_msg_wrt).append("\n")
                    .append(task_msg_mct).append("\n")
                    .append(final_msg_mct).append("\n");

            // Add paths for videos and thumbnails. Ugly, but it works
            for(int i=0; i<this.stimuli.size(); i++){
                writer.append(stimuli.get(i)[0]).append(',');
            }
            writer.append("\n");
            for(int i=0; i<this.stimuli.size(); i++){
                writer.append(stimuli.get(i)[1]).append(',');
            }
            writer.append("\n");
            for(int i=0; i<this.stimuli.size(); i++){
                writer.append(stimuli.get(i)[2]).append(',');
            }
            writer.append("\n");
            for(int i=0; i<this.stimuli.size(); i++){
                writer.append(stimuli.get(i)[3]).append(',');
            }
            writer.append("\n");

            // Paths for the false stimuli images
            for(int i=0; i<this.falseArtificialStimuli.size(); i++){
                writer.append(falseArtificialStimuli.get(i)).append(',');
            }
            writer.append("\n");

            for(int i=0; i<this.falseKinematicStimuli.size(); i++){
                writer.append(falseKinematicStimuli.get(i)).append(',');
            }
            writer.append("\n");

            // Finally Add the unique IDs using a dotcomma separated format
            // Linked set since later we need to know whether the subject is control or not
            Set<String> set = new LinkedHashSet<>(IDs);
            IDs.clear();
            IDs.addAll(set);
            for(int i=0; i<this.IDs.size(); i++){
                writer.append(IDs.get(i)).append(';');
            }
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }

    public String[] getNextStimuli() {
        //Initialize
        if(finishedShowStimuli){
            finishedShowStimuli = false;
            ArrayList<String[]> remainingStimuli = new ArrayList<>(stimuli);
            //Shuffle stimuli if random order selected
            if(Boolean.parseBoolean(random) == true){
                Collections.shuffle(remainingStimuli);
            }
        }
        //Get next stimuli
        currentSymbol = remainingStimuli.get(0);
        //Remove from list
        remainingStimuli.remove(0);
        if(remainingStimuli.isEmpty()){
            finishedShowStimuli = true;
        }
        return currentSymbol;
    }

    public ArrayList<String> getFalseSymbol(int numberOf, boolean kinematic) {
        if(kinematic){
            //Shuffle stimuli
            Collections.shuffle(falseKinematicStimuli);
            ArrayList<String> fss2 = new ArrayList<String>();
            for(int i=0; i<numberOf; i++){
                fss2.add(falseKinematicStimuli.get(i));
            }
            return fss2;
        } else {
            //Shuffle stimuli
            Collections.shuffle(falseArtificialStimuli);
            ArrayList<String> fss2 = new ArrayList<String>();
            for(int i=0; i<numberOf; i++){
                fss2.add(falseArtificialStimuli.get(i));
            }
            return fss2;
        }
    }

}
