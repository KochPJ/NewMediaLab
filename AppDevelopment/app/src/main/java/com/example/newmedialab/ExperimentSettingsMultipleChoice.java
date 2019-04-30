package com.example.newmedialab;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ExperimentSettingsMultipleChoice extends AppCompatActivity {

    public Experiment exp = new Experiment("");
    int PICK_IMAGE_MULTIPLE = 1;
    List<Uri> imageListURI = new ArrayList<Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_settings_multiplechoice);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
        //TODO: ensure that for the false symbols only those that were not shown are available
    }

    public void saveExperiment2(View view) {
        if(imageListURI.size()>Integer.parseInt(exp.getQnum())-1){
            //set selected symbols
            for(Uri uri : imageListURI){
                exp.addFalseSymbol(uri.toString());
            }

            // Save and return to my experiments
            exp.createFile();
            Intent intent = new Intent(this, MyExperiments.class);
            intent = intent.putExtra("experiment", exp);
            startActivity(intent);
        } else {
            Toast.makeText(this, "You haven't selected enough images you need at least: "+Integer.toString(Integer.parseInt(exp.getQnum())-1),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void addFalseSymbols(View view){
        Toast.makeText(this, "Please select the images, not shown to the subjects, that you want to be used in the multiple choice experiment",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image
                if(data.getData()!=null){
                    imageListURI.add(data.getData());
                } else {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        imageListURI.add(uri);
                    }
                    Log.v("LOG_TAG", "Selected Images " + imageListURI.size());
                }
            } else {
                Toast.makeText(this, "You haven't picked an image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong: "+e, Toast.LENGTH_LONG)
                    .show();
        }

        TextView falsePaths = findViewById(R.id.textView2);
        String paths = "";
        for(Uri uri : imageListURI){
            paths +=  uri.getPath()+"\n";
        }
        falsePaths.setText(paths);

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void clearPaths(View view) {
        imageListURI = new ArrayList<Uri>();
        TextView falsePaths = findViewById(R.id.textView2);
        falsePaths.setText("");

    }

}
