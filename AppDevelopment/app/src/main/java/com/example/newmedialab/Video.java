package com.example.newmedialab;


import android.util.Log;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Video {
    File file;
    FrameGrab grab;
    List<Picture> video;

    public Video(InputStream ins) throws IOException, JCodecException {
        whenConvertingToFile_thenCorrect(ins);
        File file = new File("/data/data/com.example.newmedialab/files/output.mp4");


        video = new ArrayList<Picture>();
        FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(file));
        Picture picture;
        int i = 1;
        while (null != (picture = grab.getNativeFrame())) {
            video.add(picture);
//            Log.d("picture"+Integer.toString(i), Integer.toString(picture.getWidth()) + "x" + Integer.toString(picture.getHeight()));
            Log.d("picture", "i = "+Integer.toString(i));
            i++;
        }



    }

    public void whenConvertingToFile_thenCorrect(InputStream initialStream)
            throws IOException {

        String filesPath = "/data/data/com.example.newmedialab/files/";

        byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);

        File targetFile = new File(filesPath+"output.mp4");
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
    }

}
