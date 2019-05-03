package com.example.newmedialab;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.VideoView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import static java.lang.Thread.sleep;

public class AddStimuli extends AppCompatActivity {

    public Experiment exp;
    public boolean editing = false;
    private static final int SELECT_VIDEO = 2;
    VideoView videoView;
    Video videoloaded = new Video(this, "loadedVideo");
    List<Double> vel_pro = new ArrayList<Double>();
    LineGraphSeries<DataPoint> series;

    private ProgressDialog progress;
    private int convert_progress;

    Spinner languageSpinner;
    private String[] languages;
    String language;
    Spinner typeSpinner;
    private String[] types;
    String type;
    Spinner stimuliSpinner;
    private String[] stimulies;
    String stimuli;
    Spinner velProTypeSpinner;
    private String [] velProTypes = {"linear", "function"};
    String velProType;
    Spinner importVelProSpinner;
    private String [] importVelPros;
    String importVelPro;

    Spinner saveSpinner;
    private String [] saveStimulies = {"Original", "Converted"};
    String saveStimuli;

    Spinner playStimuiSpinner;
    private String [] playStimulies = {"Original", "Artificial", "Converted"};
    String playStimuli;

    String currentStimuliPath;
    String currentArtificialStimuliPath;
    String currentVelProPath;
    String currentVideoImagesPath;
    String currentArtificialVideoImagesPath;
    String stimuliName;
    String savingStimuliPath;
    String savingArtificialStimuliPath;

    String savingStimuliPathLastImage;
    String savingArtificialStimuliPathLastImage;

    int current_n_frames = 0;
    double speed = 1;

    Context context;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO: make sure the user adds at least 1 video before continuing
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
        editing = getIntent().getExtras().getBoolean("editing");

        setContentView(R.layout.activity_add_stimuli);
        videoView = (VideoView)findViewById(R.id.videoViewNewStimuli);
        context = this;

        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        //get the saved languages
        String languagesDir = Environment.getExternalStorageDirectory()+ "/KineTest/Resources/Languages";
        File root = new File(languagesDir);
        if(!root.exists()) root.mkdirs();
        languages = root.list();

        updateLanguageSpinner();
        createSpinners();
    }

    public void build_function(View view){

        EditText exp_function_EditText = (EditText) findViewById(R.id.addStimuli_vel_pro_function_text_edit);
        VelocityFunction vel_function = new VelocityFunction(exp_function_EditText.getText().toString());
        vel_pro = new ArrayList<Double>();

        for(int i = 0; i < current_n_frames; i++){
            vel_pro.add(vel_function.compute_xd(i));
        }
        updateGraphView();
    }

    public void reset(View view){
        vel_pro = videoloaded.loadVelocityProfile(currentVelProPath);
        updateGraphView();
    }

    public void save(View view){

        EditText saving_name_EditText = (EditText) findViewById(R.id.addStimuli_saving_name_text_edit);
        String saving_name = saving_name_EditText.getText().toString();

        String kine_video_path = "";
        String art_video_path = currentArtificialStimuliPath;
        if(saveStimuli.equals(saveStimulies[0])){
            kine_video_path = currentStimuliPath;
        }else if(saveStimuli.equals(saveStimulies[1])){
            String Path = Environment.getExternalStorageDirectory()+ "/KineTest/Resources/temp/temp_loaded_video/converted.mp4";

            //saving converted video the given experiment dir
            String copy_to_dir = Environment.getExternalStorageDirectory() + "/KineTest/Experiments/"+exp.name+"/videos_transformed/";
            try {
                InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(Path)));
                videoloaded.copyVideoTo(inputStream, copy_to_dir, saving_name + ".mp4");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            savingStimuliPath = "KineTest/Experiments/"+exp.name+"/videos_transformed/"+saving_name+".mp4";
        }
        //addding new symbol to exp file
        exp.addSymbol(savingArtificialStimuliPath, savingStimuliPath, savingArtificialStimuliPathLastImage, savingStimuliPathLastImage);
        //clear the temp data in the temp folders
        videoloaded.clearTempFolders();

        Intent intent = new Intent(this, ExperimentSettingsWriting.class);
        intent = intent.putExtra("experiment", exp);
        intent = intent.putExtra("editing", editing);
        startActivity(intent);
    }

    public void cancel(View view){
        Intent intent = new Intent(this, ExperimentSettingsWriting.class);
        intent = intent.putExtra("experiment", exp);
        intent = intent.putExtra("editing", editing);
        startActivity(intent);
    }




    public void importVelocityProfile(View view){

        vel_pro = videoloaded.loadVelocityProfile("KineTest/ImportVelocityProfile/"+importVelPro);
        updateGraphView();

    }

    private void createSpinners() {
        velProTypeSpinner = (Spinner) findViewById(R.id.addStimuli_vel_pro_type_spinner);
        ArrayAdapter<String> spinnerArrayAdapterVelProType = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        this.velProTypes); //selected item will look like a spinner set from XML
        spinnerArrayAdapterVelProType.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        velProTypeSpinner.setAdapter(spinnerArrayAdapterVelProType);
        velProTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                velProType = velProTypes[position];
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        File myDir = new File(Environment.getExternalStorageDirectory() + "/KineTest/ImportVelocityProfile");
        if(!myDir.exists()) myDir.mkdirs();
        if (myDir.isDirectory()) {
            importVelPros = myDir.list();
        }

        importVelProSpinner = (Spinner) findViewById(R.id.addStimuli_import_vel_pro_spinner);
        ArrayAdapter<String> spinnerArrayAdapterImportVelPro = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        this.importVelPros); //selected item will look like a spinner set from XML
        spinnerArrayAdapterImportVelPro.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        importVelProSpinner.setAdapter(spinnerArrayAdapterImportVelPro);
        importVelProSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                importVelPro = importVelPros[position];
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });



        playStimuiSpinner = (Spinner) findViewById(R.id.addStimuli_play_stimuli_spinner);
        ArrayAdapter<String> spinnerArrayAdapterPlayStimuli = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        this.playStimulies); //selected item will look like a spinner set from XML
        spinnerArrayAdapterPlayStimuli.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        playStimuiSpinner.setAdapter(spinnerArrayAdapterPlayStimuli);
        playStimuiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                playStimuli = playStimulies[position];
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        saveSpinner = (Spinner) findViewById(R.id.addStimuli_save_stimuli_spinner);
        ArrayAdapter<String> spinnerArrayAdapterSaveStimuli = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        this.saveStimulies); //selected item will look like a spinner set from XML
        spinnerArrayAdapterSaveStimuli.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        saveSpinner.setAdapter(spinnerArrayAdapterSaveStimuli);
        saveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                saveStimuli = saveStimulies[position];
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

    }


    private void updateLanguageSpinner() {
        languageSpinner = (Spinner) findViewById(R.id.addStimuli_language_spinner);
        ArrayAdapter<String> spinnerArrayAdapterLanguage = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        this.languages); //selected item will look like a spinner set from XML
        spinnerArrayAdapterLanguage.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        languageSpinner.setAdapter(spinnerArrayAdapterLanguage);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                language = languages[position];
                String languagesDir = Environment.getExternalStorageDirectory() + "/KineTest/Resources/Languages/" + language;
                File root = new File(languagesDir);
                types = root.list();
                updateTypeSpinner();
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void updateTypeSpinner() {
        typeSpinner = (Spinner) findViewById(R.id.addStimuli_type_spinner);
        ArrayAdapter<String> spinnerArrayAdapterType = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        this.types); //selected item will look like a spinner set from XML
        spinnerArrayAdapterType.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        typeSpinner.setAdapter(spinnerArrayAdapterType);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                type = types[position];
                String languagesDir = Environment.getExternalStorageDirectory()+ "/KineTest/Resources/Languages/"+language+"/"+type+"/videos_kinematic";
                File root = new File(languagesDir);
                stimulies = root.list();
                updateStimuliSpinner();
            }
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void updateStimuliSpinner() {
        stimuliSpinner = (Spinner) findViewById(R.id.addStimuli_stimuli_spinner);
        ArrayAdapter<String> spinnerArrayAdapterStimuli = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        this.stimulies); //selected item will look like a spinner set from XML
        spinnerArrayAdapterStimuli.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        stimuliSpinner.setAdapter(spinnerArrayAdapterStimuli);
        stimuliSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                stimuli = stimulies[position];
                Log.d("AddStimuli", "updateSpinners: stimuli = "+stimuli);
                currentStimuliPath = Environment.getExternalStorageDirectory()+ "/KineTest/Resources/Languages/"+language+"/"+type+"/videos_kinematic/"+stimuli;

                currentArtificialStimuliPath = Environment.getExternalStorageDirectory()+ "/KineTest/Resources/Languages/"+language+"/"+type+"/videos_artificial/"+stimuli;
                savingStimuliPath = "KineTest/Resources/Languages/"+language+"/"+type+"/videos_kinematic/"+stimuli;
                savingArtificialStimuliPath = "KineTest/Resources/Languages/"+language+"/"+type+"/videos_artificial/"+stimuli;



                char[] stimuli_char = stimuli.toCharArray();
                stimuliName = "";
                for(int i = 0; i<stimuli_char.length-4; i++)
                {
                    stimuliName += stimuli_char[i];
                }


                savingStimuliPathLastImage = "KineTest/Resources/Languages/"+language+"/"+type+"/videos_original_lastImage/"+stimuliName+".png";
                savingArtificialStimuliPathLastImage = "KineTest/Resources/Languages/"+language+"/"+type+"/videos_artificial_lastImage/"+stimuliName+".png";

                currentVelProPath = "KineTest/Resources/Languages/"+language+"/"+type+"/velocityprofiles/"+stimuliName+".txt";

                vel_pro = videoloaded.loadVelocityProfile(currentVelProPath);
                currentVideoImagesPath = "KineTest/Resources/Languages/"+language+"/"+type+"/video_images/"+stimuliName;
                currentArtificialVideoImagesPath = "KineTest/Resources/Languages/"+language+"/"+type+"/video_artificial_images/"+stimuliName;

                EditText saving_stimuli_editText = (EditText) findViewById(R.id.addStimuli_saving_name_text_edit);
                saving_stimuli_editText.setText(stimuliName);


                File myDir = new File(Environment.getExternalStorageDirectory()+ "/"+currentVideoImagesPath);
                String[] children = myDir.list();
                current_n_frames = children.length;
                Log.d("test", "current_n_frames = "+  current_n_frames);

                updateGraphView();

            }
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }


    public void playStimuli(View view){
        String Path = "";
        if(playStimuli.equals(playStimulies[0])){
            Path = currentStimuliPath;
        }else if(playStimuli.equals(playStimulies[1])){
            Path = currentArtificialStimuliPath;

        }else if(playStimuli.equals(playStimulies[2])){
            Path = Environment.getExternalStorageDirectory()+ "/KineTest/Resources/temp/temp_loaded_video/converted.mp4";
        }

        Log.d("test", "path = "+Path);
        Uri outputVideo =Uri.parse(Path);
        videoView.setVideoURI(outputVideo);
        videoView.start();
    }


    public void updateGraphView(){
        GraphView graph = (GraphView) findViewById(R.id.addStimuli_graph_view);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

        double xd_max = 0.0;
        for (int i = 0; i < vel_pro.size(); i++) {
            if (vel_pro.get(i) > xd_max) {
                xd_max = vel_pro.get(i);
            }
            series.appendData(new DataPoint(i, vel_pro.get(i)), true, vel_pro.size());
        }

        graph.removeAllSeries();
        graph.addSeries(series);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(vel_pro.size());
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(xd_max);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
     }


    public void convertStimuli(View view){

        videoloaded.clearTempFolders();

        new Thread(new Runnable() {
            public void run() {
                Video video = new Video(context, "converted");
                video.setVideo_created(false);
                List<Double> vel_pro_stimuli = video.loadVelocityProfile(currentVelProPath);
                EditText exp_function_EditText = (EditText) findViewById(R.id.addStimuli_vel_pro_function_text_edit);
                VelocityFunction vel_function = new VelocityFunction(exp_function_EditText.getText().toString());
                EditText exp_speed_EditText = (EditText) findViewById(R.id.addStimuli_vel_pro_speed_text_edit);
                speed = Double.valueOf(exp_speed_EditText.getText().toString());

                video.convertVideo(currentVideoImagesPath,
                        "KineTest/Resources/temp/temp_images",
                        "KineTest/Resources/temp/temp_loaded_video",
                        vel_pro_stimuli, vel_function,velProType, speed);
                while (!video.video_created){
                    try {
                        //small 500ms sleep, so we dont need to update to much. takes time to convert
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                convert_progress++;


            }
        }).start();



        convert_progress = 0;
        progress = new ProgressDialog(this);
        progress.setMessage("Converting Video");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.setCancelable(false);
        progress.setMax(1);

        progress.show();

        final Thread t = new Thread(){
            @Override
            public void run(){
                while(convert_progress < 1){
                    try {
                        //small 200ms sleep, so we dont need to update to much. takes time to get the images
                        sleep(200);
                        progress.setProgress((int)convert_progress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                progress.cancel(); //end progress dialog
            }
        };
        t.start();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                Uri videoPath = data.getData();
                videoloaded = new Video(this, "loadedVideo");

                String copy_to_dir = Environment.getExternalStorageDirectory()+ "/KineTest/CurrentVideo";
                try {
                    InputStream inputStream = getContentResolver().openInputStream(videoPath);
                    videoloaded.copyVideoTo(inputStream, copy_to_dir, "copied_video.mp4");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                videoloaded.setVideoPath(copy_to_dir);
                try {
                    videoloaded.getImages("temp_images");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Uri outputVideo = videoloaded.createVideo("KineTest/CurrentVideoImages", "KineTest/CurrentVideo");

            }
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    Mat imageMat=new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };




}



