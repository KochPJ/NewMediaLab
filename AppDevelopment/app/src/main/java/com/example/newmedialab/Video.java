package com.example.newmedialab;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Video {

    String videoName;
    Uri videoPath= Uri.parse("");
    int duration;
    int fps;
    Context context;
    List<Integer> xCord = new ArrayList<Integer>();
    List<Integer> yCord = new ArrayList<Integer>();
    List<Double> veloctiyProfile = new ArrayList<Double>();

    public Video(Context cont, String name, int video_fps, int video_duration, Uri uriPathToVideo){
        videoName = name;
        context = cont;
        fps = video_fps;
        duration = video_duration;
        videoPath = uriPathToVideo;
        }


    public void getImages() throws IOException {
        //set root file
        File root = new File(Environment.getExternalStorageDirectory(), "KineTest/CurrentVideoImages");


        //create root if does not exist
        if (!root.exists()) root.mkdirs();
        //remove all files from root
        deleteTempFolder("KineTest/CurrentVideoImages");

        //create the mediaMetaDataRetriver object
        MediaMetadataRetriever m = new MediaMetadataRetriever();
        //set path
        m.setDataSource(context, videoPath);

        Log.d("Video", "Duration = "+Integer.toString(duration));
        Log.d("Video", "fps = "+Integer.toString(fps));
        int timestep = (int)(1000000.0*(1.0/(float)fps));
        Bitmap b;
        List<Bitmap> video = new ArrayList<Bitmap>();
        for (int i=0;i<duration*fps;i++){
            //Add zero padding to filename
            String n = Integer.toString(i);
            char[] c = n.toCharArray();
            String number = "";
            for(int j = 0; j < 6-c.length; j++){
                number+="0";
            }
            for(int j = 0; j< c.length; j++){
                number+=c[j];
            }

            Log.i("i",String.valueOf(i));
            b = m.getFrameAtTime(i*timestep, MediaMetadataRetriever.OPTION_CLOSEST);

            FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory()+"/KineTest/CurrentVideoImages/"+number+".png");
            b.compress(Bitmap.CompressFormat.PNG, 1, fileOutputStream);
        }
    }

    public Uri createVideo(String imagePath, String videoSavePath){

        //set paths for the images and where to store the video
        String path = (Environment.getExternalStorageDirectory()+"/"+imagePath+"/");
        String resultpath = (Environment.getExternalStorageDirectory()+"/"+videoSavePath+"/");


        //clear the current video
        File root = new File(resultpath);
        //create root if does not exist
        if (!root.exists()) root.mkdirs();
        //remove all files from root
        deleteTempFolder(resultpath);


        FFmpeg ffmpeg = FFmpeg.getInstance(context);
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            String[] cmd = {"-r", "25" ,"-f","image2", "-i",path+"%06d.png","-vcodec","libx264","-crf","25","-pix_fmt","yuv420p", resultpath+videoName+".mp4"};
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("ffmpeg", "starting");
                }

                @Override
                public void onProgress(String message) {
                    Log.d("ffmpeg", "in progress");
                    Log.d("message", message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d("error", message);
                    Log.d("ffmpeg", "failed");
                }

                @Override
                public void onSuccess(String message) {
                    Log.d("ffmpeg", "success");
                    Log.d("message", message);
                }

                @Override
                public void onFinish() {
                    Toast.makeText(context, "video got created", Toast.LENGTH_LONG).show();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
        return Uri.parse(resultpath+videoName+".mp4");
    }
    private void deleteTempFolder(String dir) {
        File myDir = new File(Environment.getExternalStorageDirectory() + "/"+dir);
        if (myDir.isDirectory()) {
            String[] children = myDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(myDir, children[i]).delete();
            }
        }
    }

    public List<Double> getVelocityProfile(){
        String root = Environment.getExternalStorageDirectory()+"/KineTest/CurrentVideoImages/";
        String savePath = Environment.getExternalStorageDirectory()+"/KineTest/CurrentAnalysedVideoImages/";

        //clear "KineTest/CurrentAnalysedVideoImages" directory to store new images
        File saveAt = new File(savePath);
        if (!saveAt.exists()) saveAt.mkdirs();
        deleteTempFolder("KineTest/CurrentAnalysedVideoImages");


        File picturesDir = new File(root);
        ColorBlobDetector blobDet = new ColorBlobDetector();

        Imgcodecs imageCodecs;
        Mat img;
        xCord = new ArrayList<Integer>();
        yCord = new ArrayList<Integer>();
        veloctiyProfile = new ArrayList<Double>();
        if (picturesDir.isDirectory()){
            String[] picturePaths = picturesDir.list();
            for(int i = 0; i < picturePaths.length; i++){
                Log.d("Video", "BlobDetection i = "+Integer.toString(i));
                imageCodecs = new Imgcodecs();
                img = imageCodecs.imread(root+picturePaths[i]);
                //ini the search for the correct blob at the center of the image
                if(i == 0){
                    blobDet.x_old = (int)(img.width()/2);
                    blobDet.y_old = (int)(img.height()/2);
                }

                blobDet.process(img);
                Log.d("Video", "Blobdetection found blob at x,y = ["+Integer.toString(blobDet.x)+", "+ Integer.toString(blobDet.y)+"]");
                xCord.add(blobDet.x);
                yCord.add(blobDet.y);

                //get analysed image and write to savePath
                Mat mask = blobDet.mDrawRect;
                imageCodecs.imwrite(savePath+picturePaths[i], mask);
            }
            createVideo("KineTest/CurrentAnalysedVideoImages", "KineTest/CurrentAnalysedVideo");

            for(int i = 0; i < xCord.size()-1; i++){
                veloctiyProfile.add(getvel(xCord.get(i),yCord.get(i), xCord.get(i+1),yCord.get(i+1)));
            }
        }
        return veloctiyProfile;


    }

    private double getvel(int x1, int y1, int x2, int y2){
        double vel = Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
        return vel;
    }

    public void convertVideo(String ImagePath, String videoOutputPath, List<Double> velProfile ,VelocityFunction velocityFunction, String functionType, double speed){

        double totalVel = 0;
        double velStep = 0;
        int n_steps = velProfile.size();
        List<Integer> indecies = new ArrayList<Integer>();
        List<Double> velPosition = new ArrayList<Double>();
        List<Double> cumulative = velProfile;

        for(int i = 0; i<n_steps;i++){
            if (i ==0){
                cumulative.set(0, 0.0);
            }else{
                cumulative.set(i, cumulative.get(i)+cumulative.get(i-1));
            }
        }

        if (functionType == "normal"){

        }else if(functionType == "linear"){
            //get the constant speed
            for(int i = 0; i<n_steps;i++){
                totalVel+= velProfile.get(i);
            }
            velStep = totalVel/n_steps;

            for(int i = 0; i<n_steps+1;i++){
                velPosition.add(i*velStep);
            }

            for(int i = 0; i<velPosition.size();i++){
                indecies.add(getClosestValue(velPosition.get(i), velProfile));
            }


        }else if(functionType == "function"){

        }
    }

    private int getClosestValue(Double value, List<Double> value_list){
        //gets the index of the value in the value_list which is closest to the given value
        //substract the value and get the abs. meaning the minimum value is the closest
        List<Double> abs_value_list = new ArrayList<Double>();
        for(int i = 0; i < value_list.size(); i++){
            abs_value_list.add( Math.abs(value_list.get(i)-value) );
        }
        int index = 0;
        double minimumValue = abs_value_list.get(0);
        for(int i = 1; i < abs_value_list.size(); i++){
            if(abs_value_list.get(i)<minimumValue){
                minimumValue = abs_value_list.get(i);
                index = i;
            }
        }
        return index;
    }


}

