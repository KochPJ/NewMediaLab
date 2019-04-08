package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AppCompatActivity;

public class ExperimentFunction extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] default_functions ={"Linear","Squared","Natural"};
    public boolean function_build = false;
    public boolean editing = false;
    public Experiment exp = new Experiment("");
    LineGraphSeries<DataPoint> series;
    TextView tvProgressLabel;
    String video_speed_multiplier = "100";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_function);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
        editing = getIntent().getExtras().getBoolean("editing");

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.default_functions_spinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,default_functions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        // set a change listener on the SeekBar
        SeekBar seekBar = findViewById(R.id.speed_multiplier_slider);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = seekBar.getProgress();
        tvProgressLabel = findViewById(R.id.speed_multiplier_textbox);
        tvProgressLabel.setText("Video playback speed multiplier: " + (progress+50) + "%");
    }

    OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            tvProgressLabel.setText("Video playback speed multiplier: " + (progress+50) + "%");
            video_speed_multiplier = String.valueOf(progress+50);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    //Performing action onItemSelected and onNothing selected
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        EditText editText = (EditText)findViewById(R.id.te_experiment_function);
        switch (default_functions[position]) {
            case "Linear":
                Toast.makeText(getApplicationContext(), default_functions[position], Toast.LENGTH_LONG).show();
                editText.setText("1+x", TextView.BufferType.EDITABLE);
                break;
            case "Squared":
                Toast.makeText(getApplicationContext(), default_functions[position], Toast.LENGTH_LONG).show();
                editText.setText("1+x*x", TextView.BufferType.EDITABLE);
                break;
            case "Natural":
                Toast.makeText(getApplicationContext(), default_functions[position], Toast.LENGTH_LONG).show();
                editText.setText("1+0.667*x", TextView.BufferType.EDITABLE);
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void next(View view) {
        //pointer to the text edit

        if(function_build){
            EditText exp_function_EditText = (EditText) findViewById(R.id.te_experiment_function);

            // set the function
            exp.setFunction(exp_function_EditText.getText().toString());

            // set the video playback speed
            exp.setSpeedModifier(video_speed_multiplier);

            String name = exp.getName();
            String repeats = exp.getMaxRepeats();

            Log.d("name = ", name);
            Log.d("repeats = ", repeats);

            //If editing return to my experiments
            if (editing){
                //Save results
                exp.createFile();
                Toast.makeText(this, "Saved Experiment to " +  exp.getFile_name(),
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MyExperiments.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, ExperimentSettings.class);
                intent = intent.putExtra("experiment", exp);
                startActivity(intent);
            }
            // need to reset bool to false because else people can press back in the next view and then use any function which was not tested yet.
            function_build = false;
            editing = false;
        }else{
            Toast.makeText(this, "Function not build yet", Toast.LENGTH_SHORT).show();
        }

    }

    public void build_function(View view){

        EditText exp_function_EditText = (EditText) findViewById(R.id.te_experiment_function);
        VelocityFunction vel_function = new VelocityFunction(exp_function_EditText.getText().toString());
        Boolean working = vel_function.testFunction(100);
        Log.d("Test vel func = ", Boolean.toString(working));

        if(!working){
            function_build = false;
            Toast.makeText(this, "Function not working value below 0.01", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Build", Toast.LENGTH_SHORT).show();
            function_build = true;
            GraphView graph = (GraphView) findViewById(R.id.graph_view);
            series = new LineGraphSeries<DataPoint>();

            double xd_max = 0.0;
            for(int i = 0; i < vel_function.x.length; i++){
                if(vel_function.xd[i] > xd_max){
                    xd_max = vel_function.xd[i];
                }
                series.appendData(new DataPoint(vel_function.x[i], vel_function.xd[i]), true, vel_function.x.length);
            }
            graph.removeAllSeries();
            graph.addSeries(series);

            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(10);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(xd_max+0.5);
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setXAxisBoundsManual(true);
        }
    }
}
