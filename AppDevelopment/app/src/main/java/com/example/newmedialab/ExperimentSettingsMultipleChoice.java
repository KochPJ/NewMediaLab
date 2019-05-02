package com.example.newmedialab;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ExperimentSettingsMultipleChoice extends AppCompatActivity {

    public Experiment exp;
    int PICK_IMAGE_MULTIPLE_ART = 1;
    int PICK_IMAGE_MULTIPLE_KIN = 2;
    List<Uri> imageListURI_art = new ArrayList<Uri>();
    List<Uri> imageListURI_kin = new ArrayList<Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_settings_multiplechoice);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
    }

    public void saveExperiment2(View view) {
        if(imageListURI_art.size()>=Integer.parseInt(exp.getQnum())-1
            && imageListURI_kin.size()>=Integer.parseInt(exp.getQnum())-1){
            //set selected symbols
            for(Uri uri : imageListURI_art){
                try {
                    exp.addFalseArtificialSymbol(PathUtil.getPath(this,uri));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            for(Uri uri : imageListURI_kin){
                try {
                    exp.addFalseKinematicSymbol(PathUtil.getPath(this,uri));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            // Save and return to my experiments
            exp.createFile();
            Intent intent = new Intent(this, MyExperiments.class);
            intent = intent.putExtra("experiment", exp);
            startActivity(intent);
        } else {
            Toast.makeText(this, "You haven't selected enough images you need at least: "+Integer.toString(Integer.parseInt(exp.getQnum())-1)+" for both conditions.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void addFalseSymbolsKinestetic(View view){
        Toast.makeText(this, "Please select the images for the experimental group that you want to be used in the multiple choice experiment",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE_KIN);
    }

    public void addFalseSymbolsArtificial(View view){
        Toast.makeText(this, "Please select the images for the control group that you want to be used in the multiple choice experiment",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE_ART);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if ((requestCode == PICK_IMAGE_MULTIPLE_KIN || requestCode == PICK_IMAGE_MULTIPLE_ART)
                    && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image
                if(data.getData()!=null){
                    if (requestCode == PICK_IMAGE_MULTIPLE_KIN){
                        imageListURI_kin.add(data.getData());
                    } else {
                        imageListURI_art.add(data.getData());
                    }
                } else {
                    if (requestCode == PICK_IMAGE_MULTIPLE_KIN) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            imageListURI_kin.add(uri);
                        }
                        Log.v("LOG_TAG", "Selected Images " + imageListURI_kin.size());
                    } else {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            imageListURI_art.add(uri);
                        }
                        Log.v("LOG_TAG", "Selected Images " + imageListURI_art.size());
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked an image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong: "+e, Toast.LENGTH_LONG)
                    .show();
        }

        // Update textviews
        TextView falsePaths = findViewById(R.id.textView2);
        TextView falsePaths2 = findViewById(R.id.textView24);
        String paths = "";
        String paths2 = "";
        for(Uri uri : imageListURI_art){
            paths +=  uri.getPath()+"\n";
        }
        for(Uri uri : imageListURI_kin){
            paths2 +=  uri.getPath()+"\n";
        }
        falsePaths.setText(paths);
        falsePaths2.setText(paths2);

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void clearPaths(View view) {
        imageListURI_art = new ArrayList<Uri>();
        imageListURI_kin = new ArrayList<Uri>();
        TextView falsePaths = findViewById(R.id.textView2);
        falsePaths.setText("");
        TextView falsePaths2 = findViewById(R.id.textView24);
        falsePaths2.setText("");
    }

}
