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


    public void getImages(VelocityFunction velocityFunction) throws IOException {
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

    public Uri createVideo(){

        //set paths for the images and where to store the video
        String path = (Environment.getExternalStorageDirectory()+"/KineTest/CurrentVideoImages/");
        String resultpath = (Environment.getExternalStorageDirectory()+"/KineTest/CurrentVideo/");


        //clear the current video
        File root = new File(resultpath);
        //create root if does not exist
        if (!root.exists()) root.mkdirs();
        //remove all files from root
        deleteTempFolder("KineTest/CurrentVideo");


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
                    String path2 = Environment.getExternalStorageDirectory()+"/KineTest/results";
                    Log.d("Files", "Path: " + path2);
                    File directory = new File(path2);
                    File[] files = directory.listFiles();
                    Log.d("Files", "Size: "+ files.length);
                    for (int i = 0; i < files.length; i++)
                    {
                        Log.d("Files", "FileName:" + files[i].getName());
                    }
                    Toast.makeText(context, "saved the video", Toast.LENGTH_LONG).show();
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
                blobDet.process(img);
                Log.d("Video", "Blobdetection found blob at x,y = ["+Integer.toString(blobDet.x)+", "+ Integer.toString(blobDet.y)+"]");
                xCord.add(blobDet.x);
                yCord.add(blobDet.y);
            }

            for(int i = 0; i < xCord.size()-1; i++){
                veloctiyProfile.add(getvel(xCord.get(i),yCord.get(i), xCord.get(i+1),yCord.get(i+1)));
            }
        }
        return veloctiyProfile;


    }

    public double getvel(int x1, int y1, int x2, int y2){
        double vel = Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
        return vel;
    }
}

