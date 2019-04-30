package com.example.newmedialab;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class Video implements Serializable {

    String videoName;
    double duration;
    double fps;
    int n_frames;
    int get_images_progress = 0;
    int get_velPro_progress = 0;
    boolean video_created;
    Boolean got_video_images = false;
    String videoPath;
    Context context;
    List<Integer> xCord = new ArrayList<Integer>();
    List<Integer> yCord = new ArrayList<Integer>();
    List<Double> veloctiyProfile = new ArrayList<Double>();

    public Video(Context cont, String name){
        videoName = name;
        context = cont;
        }


    public void setVideoPath(String path){
        videoPath = path;
        getVideoData();
    }

    public void setVideo_created(Boolean status){
        video_created = status;
    }

    public void setVeloctiyProfile(List<Double> veloctiy_profile){
        veloctiyProfile = veloctiy_profile;
    }

    public void getVideoData(){
        FFmpegMediaMetadataRetriever mFFmpegMediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
        mFFmpegMediaMetadataRetriever.setDataSource(videoPath);
        String mVideoDuration =  mFFmpegMediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
        String mVideoFps = mFFmpegMediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FRAMERATE);
        fps = Double.valueOf(mVideoFps); //frames per second
        duration = Double.valueOf(mVideoDuration); // duration in miliseconds
        n_frames = (int)(fps*(duration/1000)); // get duration in seconds and multiplay with fps to get n_images
        Log.d("Video", "getVideoData: Duration = "+mVideoDuration);
        Log.d("Video", "getVideoData: fps = "+mVideoFps);
        Log.d("Video", "getVideoData: number of frames = "+Integer.valueOf(n_frames));
    }

    public void getImages(String saving_path) throws IOException {
        String save_to_folder = "KineTest/Resources/Temp/"+saving_path;
        //set root file
        File root = new File(Environment.getExternalStorageDirectory(), save_to_folder);
        //create root if does not exist
        if (!root.exists()) root.mkdirs();
        //remove all files from root
        deleteTempFolder(save_to_folder);

        //create the mediaMetaDataRetriver object
        FFmpegMediaMetadataRetriever m = new FFmpegMediaMetadataRetriever();
        //set path
        m.setDataSource(videoPath);

        //get the timestep between images in microseconds
        int timestep = (int)(1000000.0*(1.0/fps));
        Bitmap b;
        List<Bitmap> video = new ArrayList<Bitmap>();
        for (int i=0;i<n_frames;i++){
            //set the progress for the progress dialog
            get_images_progress = i+1;
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
            b = m.getFrameAtTime(i*timestep, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);

            //rotate them by 180, FFmpegMediaMetadataRetriever somehow reads the images upside down
            //Matrix matrix = new Matrix();
            //matrix.postRotate(180);
            //b =  Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);

            FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory()+"/"+save_to_folder+"/"+number+".png");
            b.compress(Bitmap.CompressFormat.PNG, 1, fileOutputStream);
        }
        got_video_images = true;
    }

    public Uri createVideo(String imagePath, String videoSavePath){

        //set paths for the images and where to store the video
        String path = (Environment.getExternalStorageDirectory()+"/"+imagePath+"/");
        String resultpath = (Environment.getExternalStorageDirectory()+"/"+videoSavePath+"/");


        //clear the current video
        File root = new File(resultpath);
        //create root if does not exist
        if (!root.exists()) root.mkdirs();
        //remove the video file if it exsists
        deleteFileInFolder(videoSavePath, videoName+".mp4");


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
                    video_created = true;
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            Log.d("ffmpeg", "failed");
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

    private void deleteFileInFolder(String dir, String name) {
        File myDir = new File(Environment.getExternalStorageDirectory() + "/"+dir);
        File[] directoryListing = myDir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                // Do something with child
                String file_name = child.getName();
                if(file_name.equals(name)){
                    child.delete();
                    }
            }
        }
    }



    public List<Double> getVelocityProfile(String imageDir){

        String saveTo = "KineTest/Resources/Temp/temp_images_analysed";
        String root = Environment.getExternalStorageDirectory()+"/"+imageDir+"/";
        String savePath = Environment.getExternalStorageDirectory()+"/"+saveTo+"/";

        //clear "KineTest/CurrentAnalysedVideoImages" directory to store new images
        File saveAt = new File(savePath);
        if (!saveAt.exists()) saveAt.mkdirs();
        deleteTempFolder(saveTo);


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
                //set progress for progress dialog
                get_velPro_progress = i+1;
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
            createVideo(saveTo, "KineTest/Resources/Temp/temp_loaded_video");

            for(int i = 0; i < xCord.size()-1; i++){
                veloctiyProfile.add(getvel(xCord.get(i),yCord.get(i), xCord.get(i+1),yCord.get(i+1)));
            }
        }
        return veloctiyProfile;


    }


    public void saveVelocityProfile(String Path){
        String savePath = Environment.getExternalStorageDirectory()+"/"+ Path+"/";
        //clear "KineTest/VelocityProfiles" directory to store new images
        File saveAt = new File(savePath);
        if(!saveAt.exists()) saveAt.mkdirs();
        // convert array of symboles to string
        try
        {
            /// TODO:  check if external storage is available (https://developer.android.com/reference/android/os/Environment.html#getExternalStorageState()) if not, save to a different directory or output a warning to the user
            String FILE_NAME = (this.videoName+ ".txt");
            File gpxfile = new File(saveAt, FILE_NAME);
            FileWriter writer = new FileWriter(gpxfile);
            for(int i = 0; i < veloctiyProfile.size(); i++){
                writer.append(Double.toString(veloctiyProfile.get(i))).append("\n");

            }
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }

    public List<Double> loadVelocityProfile(String Path){
        String loadingPath = Environment.getExternalStorageDirectory()+"/"+Path;
        File file = new File(loadingPath);
        veloctiyProfile = new ArrayList<Double>();
        // Do something with child
        Scanner sc = null;
        try {
            Log.d("Video", "loadVelPro: child path = "+file.getPath());
            Log.d("Video", "loadVelPro: child name = "+file.getName());
            sc = new Scanner(file);
            int c = 0;
            Experiment exp = new Experiment("");

            while(sc.hasNextLine()){
                String line = (sc.nextLine());
                Log.d("Video", "loadVelPro: line i, value = "+c+ ", "+line);
                veloctiyProfile.add( Double.valueOf(line));
                c++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return veloctiyProfile;
    }


    private double getvel(int x1, int y1, int x2, int y2){
        double vel = Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
        return vel;
    }

    public void convertVideo(String ImagePath, String ConvertedImagePath,String videoOutputPath, List<Double> velProfile ,VelocityFunction velocityFunction, String functionType, double speed){

        String pathImages = (Environment.getExternalStorageDirectory()+"/"+ImagePath+"/");
        String pathConvert = (Environment.getExternalStorageDirectory()+"/"+ConvertedImagePath+"/");
        String pathOutput = (Environment.getExternalStorageDirectory()+"/"+videoOutputPath+"/");

        //create and clear the converted image saving dir
        File root = new File(Environment.getExternalStorageDirectory()+"/"+ConvertedImagePath );
        if (!root.exists()) root.mkdirs();
        deleteTempFolder(pathConvert);

        double totalVel = 0;
        double velStep = 0;
        int n_steps = velProfile.size();
        Log.d("Video", "convertVideo: n_steps = " +n_steps);

        //get the constant total distance
        for(int i = 0; i<n_steps;i++){
            totalVel+= velProfile.get(i);
        }
        Log.d("Video", "convertVideo: totalVel = " +totalVel);

        List<Integer> indecies = new ArrayList<Integer>();
        List<Double> velPosition = new ArrayList<Double>();
        List<Double> cumulative = new ArrayList<Double>();
        cumulative.add(0, 0.0);

        Log.d("Video", "cumulative i, value = "+ Integer.toString(0)+ ", "+ Double.toString(cumulative.get(0)));
        for(int i = 0; i<n_steps;i++){
            cumulative.add(velProfile.get(i)+cumulative.get(i));
            Log.d("Video", "cumulative i, value = "+ Integer.toString(i+1)+ ", "+ Double.toString(cumulative.get(i+1)));
        }


        if (functionType == "normal"){

        }else if(functionType == "linear"){

            //get constant speed
            velStep = totalVel/(n_steps);
            Log.d("Video", "convertVideo: velStep = " +velStep);



            for(int i = 0; i<n_steps+1;i++){
                velPosition.add(velStep*i);
            }

            for(int i = 0; i<velPosition.size();i++){
                indecies.add(getClosestValue(velPosition.get(i), cumulative));
            }

        }else if(functionType == "function"){

            //get the velocities of the function and get the constant total distance of the function
            double[] xd = new double[n_steps];
            double totalVel_function = 0.0;
            for(int i = 0; i<n_steps;i++){
                xd[i] = velocityFunction.compute_xd(i);
                totalVel_function += xd[i];
                Log.d("Video", "convertVideo: xd i, value = " +Integer.toString(i)+ ", "+ Double.toString(xd[i]));
            }


            Log.d("Video", "convertVideo: totalVel_function = " +totalVel_function);



            List<Double> cumulative_function = new ArrayList<Double>();
            cumulative_function.add(0, 0.0);
            //Log.d("Video", "cumulative_function i, value = "+ Integer.toString(0)+ ", "+ Double.toString(cumulative_function.get(0)));
            for(int i = 0; i<n_steps;i++){
                //get the cumulative_function and normalize to fit the profile
                cumulative_function.add( ( (xd[i]+cumulative_function.get(i)))) ;
                //Log.d("Video", "cumulative_function i, value = "+ Integer.toString(i+1)+ ", "+ Double.toString(cumulative_function.get(i+1)));
            }

            //normalize the cumulative function to fit the velocity profile of the video
            double norm = totalVel/totalVel_function;
            Log.d("Video", "convertVideo: norm = " +norm);
            List<Double> cumulative_function_norm = new ArrayList<Double>();
            for(int i = 0; i<cumulative_function.size(); i++){
                cumulative_function_norm.add(cumulative_function.get(i)*norm);
                Log.d("Video", "cumulative_function_norm i, value = "+ Integer.toString(i)+ ", "+ Double.toString(cumulative_function_norm.get(i)));
            }

            for(int i = 0; i<cumulative_function_norm.size();i++){
                indecies.add(getClosestValue(cumulative_function_norm.get(i), cumulative));
            }

        }


        Imgcodecs imageCodecs;
        Mat img;
        for(int i = 0; i < indecies.size(); i++){
            Log.d("Video", "Convert video: i = "+Integer.toString(i)+ " and index = "+Integer.toString(indecies.get(i)));
            //Add zero padding to i, needed to get the right image and copy into the correct dir
            char[] c_get = Integer.toString(indecies.get(i)).toCharArray();
            String n_get = "";
            for(int j = 0; j < 6-c_get.length; j++){
                n_get+="0";
            }
            for(int j = 0; j< c_get.length; j++){
                n_get+=c_get[j];
            }

            //Add zero padding indecies.get(i), needed for storing new image
            char[] c_set = Integer.toString(i).toCharArray();
            String n_set = "";
            for(int j = 0; j < 6-c_set.length; j++){
                n_set+="0";
            }
            for(int j = 0; j< c_set.length; j++){
                n_set+=c_set[j];
            }

            imageCodecs = new Imgcodecs();
            //Log.d("Video", "Convert video: get path = " + pathImages+n_get+".png");
            //Log.d("Video", "Convert video: set path = " + pathConvert+n_set+".png");
            img = imageCodecs.imread(pathImages+n_get+".png"); //get image with index from found indecies
            //Log.d("Video", "Convert video: w,h = " + Integer.toString(img.width())+", "+Integer.toString(img.height()));
            imageCodecs.imwrite(pathConvert+n_set+".png", img); //set image with new name to dir
        }


        createVideo(ConvertedImagePath, videoOutputPath);

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

    public void copyVideoTo(InputStream video, String target_dir, String name) throws IOException {

        File saveAt = new File(target_dir);
        if (!saveAt.exists()) saveAt.mkdirs();
        try {
            OutputStream out = new FileOutputStream(target_dir+"/"+name);
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = video.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            video.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void clearTempFolders(){
        //clearing all temp data in the temp folders
        String dir = "KineTest/Resources/Temp/temp_images";
        File root = new File(Environment.getExternalStorageDirectory()+"/"+dir);
        if (root.exists()){
            deleteTempFolder(dir);
        }
        dir = "KineTest/Resources/Temp/temp_images_analysed";
        root = new File(Environment.getExternalStorageDirectory()+"/"+dir);
        if (root.exists()){
            deleteTempFolder(dir);
        }
        dir = "KineTest/Resources/Temp/temp_loaded_video";
        root = new File(Environment.getExternalStorageDirectory()+"/"+dir);
        if (root.exists()){
            deleteTempFolder(dir);
        }
        dir = "KineTest/Resources/Temp/temp_artificial_images";
        root = new File(Environment.getExternalStorageDirectory()+"/"+dir);
        if (root.exists()){
            deleteTempFolder(dir);
        }

    }



}

