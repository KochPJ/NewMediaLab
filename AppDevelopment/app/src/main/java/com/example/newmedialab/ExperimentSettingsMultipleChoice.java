package com.example.newmedialab;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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
    ImageView kineImageView;
    Spinner kineImageSpinner;
    String[] kineImages;
    int kineImagePos;
    int artImagePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_settings_multiplechoice);
        Intent i = getIntent();
        exp = (Experiment)i.getSerializableExtra("experiment");
        kineImageView = (ImageView) findViewById(R.id.ExpSettingMultiCho_imviewkine);
        updateSpinners();
    }

    public void updateSpinners(){

        kineImages = new String[imageListURI_kin.size()];
        for(int i = 0; i < imageListURI_kin.size(); i++){
            kineImages[i] = imageListURI_kin.get(i).toString();
        }
        kineImageSpinner = (Spinner) findViewById(R.id.ExpSettingMultiCho_kine_spinner);
        ArrayAdapter<String> spinnerArrayAdapterKineImages = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                       this.kineImages); //selected item will look like a spinner set from XML
        spinnerArrayAdapterKineImages.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        kineImageSpinner.setAdapter(spinnerArrayAdapterKineImages);
        kineImageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                kineImageView.setImageURI(imageListURI_kin.get(position));
                kineImagePos = position;
            }
            public void onNothingSelected(AdapterView<?> parentView) {
                kineImageView.setImageURI(Uri.parse("@android:drawable/btn_dialog"));
                Log.d("test", "here");
            }
        });
    }



    public void cancel(View view){
        exp.deleteExperiment();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }


    public void saveExperiment2(View view) {
        if(imageListURI_kin.size()>=Integer.parseInt(exp.getQnum())-1){
            //set selected symbols
            for(Uri uri : imageListURI_kin){
                try {
                    exp.addFalseSymbol(PathUtil.getPath(this,uri));
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
            Toast.makeText(this, "You haven't selected enough images you need at least: "+Integer.toString(Integer.parseInt(exp.getQnum())-1)+" images.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void deleteFalseSymbolsKinestetic(View view){
        imageListURI_kin.remove(kineImagePos);
        updateSpinners();
    }

    public void deleteFalseSymbolsArtificial(View view){
        imageListURI_art.remove(artImagePos);
        updateSpinners();
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
        updateSpinners();

        super.onActivityResult(requestCode, resultCode, data);
    }

}
