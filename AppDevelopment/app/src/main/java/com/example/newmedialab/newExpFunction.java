package com.example.newmedialab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AppCompatActivity;

public class newExpFunction extends AppCompatActivity {

    public boolean function_build = false;
    public Experiment exp = new Experiment("");
    LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_exp_function);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
    }


    public void next(View view) {
        //pointer to the text edit

        if(function_build){
            EditText exp_function_EditText = (EditText) findViewById(R.id.te_experiment_function);

            // set the function
            exp.setFunction(exp_function_EditText.getText().toString());

            String name = exp.getName();
            String repeats = exp.getRepeats();

            Log.d("name = ", name);
            Log.d("repeats = ", repeats);

            Intent intent = new Intent(this, NewExperimentSelectSymbols.class);
            intent = intent.putExtra("experiment", exp);
            startActivity(intent);
            // need to reset bool to false because else people can press back in the next view and then use any function which was not tested yet.
            function_build = false;
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
