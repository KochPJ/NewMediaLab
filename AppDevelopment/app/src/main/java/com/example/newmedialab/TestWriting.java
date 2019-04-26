package com.example.newmedialab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class TestWriting extends AppCompatActivity {

    public Experiment exp = new Experiment("");
    public String stimuli;
    RelativeLayout relativeLayout;
    Paint paint;
    View view;
    Path path2;
    Bitmap bitmap;
    Canvas canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_writing);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");

        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout1);
        view = new SketchSheetView(TestWriting.this);
        paint = new Paint();
        path2 = new Path();
        relativeLayout.addView(view, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        paint.setDither(true);
        paint.setColor(Color.parseColor("#000000"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(5);

        //Set progressbar with correct value
        ProgressBar pb = findViewById(R.id.progressBar2);
        if(Boolean.parseBoolean(exp.getProgressbar())){
            int maxSymbols = 0;
            for (char ch : exp.getSymbols().toCharArray()){
                if(ch != ',' && ch != ' '){
                    maxSymbols++;
                }
            }
            pb.setMax(maxSymbols+1);
            pb.setProgress(maxSymbols - exp.getRemainingSymbols().size());
        } else {
            pb.setAlpha(0);
        }

        //Set messages
        TextView expName = (TextView) findViewById(R.id.textView13);
        expName.setText(exp.getName());
        TextView expName2 = (TextView) findViewById(R.id.textView14);
        expName2.setText(exp.getTask_msg_wrt());

    }

    public void clearSketch(View view) {
        //TODO: (low priority) fix reset not working until drawing something else
        path2.reset();
    }

    public void showNextStimuli(View view){
        String currentSymbol = exp.getCurrentSymbol();
        int id_num = exp.getCurrentID();
        String full_id = exp.getID(id_num);

        //Create folder for subject if doesn't exist
        String path = "/KineTest/Experiments/"+ exp.name +"/"+ full_id +"/pre_test";
        File folder = new File(Environment.getExternalStorageDirectory() + path);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
            Log.d("TW.showNextStimuli", "created folder");
        }
        if (success) {
            // Save drawing to device
            FileOutputStream fos = null;
            try {
                View view2 = findViewById(R.id.relativelayout1);
                fos = new FileOutputStream((Environment.getExternalStorageDirectory() + path +"/"+ currentSymbol +".jpg"));
                Bitmap  bitmap = Bitmap.createBitmap( view2.getWidth(), view2.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                view2.draw(canvas);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            //Do something on fail
        }

        // Check if this was the last stimuli
        if(exp.finishedShowingStimuli()){
            // Save changes to experiment
            exp.createFile();
            Toast.makeText(TestWriting.this, "Finished test, saved results \n"+exp.getFinal_msg_mct(),
                    Toast.LENGTH_LONG).show();
            // Return to MyExperiments
            Intent intent = new Intent(this, MyExperiments.class);
            intent = intent.putExtra("experiment", exp);
            startActivity(intent);
        } else {
            // Show next stimuli
            Intent intent2 = new Intent(this, ShowStimuli.class);
            intent2 = intent2.putExtra("experiment", exp);
            startActivity(intent2);
        }
    }

    class SketchSheetView extends View {

        public SketchSheetView(Context context) {

            super(context);

            bitmap = Bitmap.createBitmap(820, 480, Bitmap.Config.ARGB_4444);

            canvas = new Canvas(bitmap);

            this.setBackgroundColor(Color.WHITE);
        }

        private ArrayList<DrawingClass> DrawingClassArrayList = new ArrayList<DrawingClass>();

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            DrawingClass pathWithPaint = new DrawingClass();

            canvas.drawPath(path2, paint);

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                path2.moveTo(event.getX(), event.getY());

                path2.lineTo(event.getX(), event.getY());
            }
            else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                path2.lineTo(event.getX(), event.getY());

                pathWithPaint.setPath(path2);

                pathWithPaint.setPaint(paint);

                DrawingClassArrayList.add(pathWithPaint);
            }

            invalidate();
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (DrawingClassArrayList.size() > 0) {

                canvas.drawPath(
                        DrawingClassArrayList.get(DrawingClassArrayList.size() - 1).getPath(),

                        DrawingClassArrayList.get(DrawingClassArrayList.size() - 1).getPaint());
            }
        }
    }

    public class DrawingClass {

        Path DrawingClassPath;
        Paint DrawingClassPaint;

        public Path getPath() {
            return DrawingClassPath;
        }

        public void setPath(Path path) {
            this.DrawingClassPath = path;
        }


        public Paint getPaint() {
            return DrawingClassPaint;
        }

        public void setPaint(Paint paint) {
            this.DrawingClassPaint = paint;
        }
    }

}

