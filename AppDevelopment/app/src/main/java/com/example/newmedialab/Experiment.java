package com.example.newmedialab;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;

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
    ArrayList<String> falseStimuli = new ArrayList<String>();
    String[] currentSymbol;
    int currentID = 0;
    boolean finishedShowStimuli = true;
    boolean noneOption = true;

    public Experiment(String name){
        this.name = name;
        this.file_name = (this.name+ ".txt");
    }

    public void setName(String name){
        this.name = name;
        this.file_name = (this.name+ ".txt");
    }

    public void deleteSubjects(){
        this.IDs = new ArrayList<String>();
        this.setCurrentID(0);
        //delete folders
        File dir = new File(Environment.getExternalStorageDirectory()+"/KineTest/Experiments/"+name);
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                File child = new File(Environment.getExternalStorageDirectory()+"/KineTest/Experiments/"+name+"/"+children[i]);
                Log.d("child", "deleteSubjects: "+children[i]);
                if((child.isDirectory() && (!child.toString().contains("videos_transformed")))){
                    try {
                        Log.d("child2", "deleteSubjects: "+child.toString());
                        Log.d("child3", "deleteSubjects: "+child.getAbsolutePath());
                        FileUtils.deleteDirectory(child);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        this.createFile();
    }

    public void deleteExperiment(){
        //delete folder
        File dir = new File(Environment.getExternalStorageDirectory()+"/KineTest/Experiments/"+name);
        if (dir.isDirectory())
        {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //delete file
        dir = new File(Environment.getExternalStorageDirectory()+"/KineTest/Experiments/Experiment_files");
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                if(children[i].equals(name+".txt")){
                    new File(dir, children[i]).delete();
                }
            }
        }
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

    public void addFalseSymbol(String lastImagePath){ falseStimuli.add(lastImagePath); }

    public void setTask_msg_wrt(String msg) {this.task_msg_wrt = msg; }

    public void setTask_msg_mct(String msg) {this.task_msg_mct = msg; }

    public void setFinal_msg_wrt(String msg) {this.final_msg_wrt = msg; }

    public void setFinal_msg_mct(String msg) {this.final_msg_mct = msg; }

    public void setQnum(String exp_qnum) { this.qnum = exp_qnum; }

    public void addID(String id) {this.IDs.add(id); }

    public void setCurrentID(int id_num) {this.currentID = id_num; }

    public void setNoneOption(Boolean noneOption) { this.noneOption = noneOption; }

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

    public ArrayList<String> getIDs() {
        ArrayList<String> IDsCopy = new ArrayList<String>();
        for(String id:IDs){
            if(!id.equals("NaS")) {
                IDsCopy.add(id);
            }
        }
        return IDsCopy;
    }

    public ArrayList<String> getTrueIDs() { return this.IDs; }

    public ArrayList<String[]> getRemainingStimuli() {return remainingStimuli; }

    public String[] getCurrentSymbol() {return  this.currentSymbol; }

    public Boolean finishedShowingStimuli() {return  this.finishedShowStimuli; }

    public String getID(int num){return this.IDs.get(num); }

    public ArrayList<String> getFalseStimuli(){ return falseStimuli; }

    public int getCurrentID(){
        return this.currentID;
    }

    public boolean getNoneOption(){ return noneOption; }

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
                    .append(final_msg_mct).append("\n")
                    .append(Boolean.toString(noneOption)).append("\n");

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

            for(int i = 0; i<this.falseStimuli.size(); i++){
                writer.append(falseStimuli.get(i)).append(',');
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
            //Deep copy
            for(int i=0; i<stimuli.size(); i++){
                remainingStimuli.add(stimuli.get(i).clone());
            }
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

    public ArrayList<String> getFalseSymbol(int numberOf) {
        //Shuffle stimuli
        Collections.shuffle(falseStimuli);
        ArrayList<String> fss2 = new ArrayList<String>();
        for(int i=0; i<numberOf; i++){
            fss2.add(falseStimuli.get(i));
        }
        return fss2;
    }
}
