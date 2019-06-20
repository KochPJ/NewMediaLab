package com.example.newmedialab;


import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class ColorBlobDetector {
    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(25,0,40);
    private Scalar mUpperBound = new Scalar(120,255,255);

    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.1;
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(25,50,50,0);
    private Mat mSpectrum = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();





    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();
    Mat mDrawRect = new Mat();
    int x;
    int y;
    int x_old = 0;
    int y_old = 0;

    public void setColorRadius(Scalar radius) {
        mColorRadius = radius;
    }

    public void setHsvColor(Scalar hsvColor) {
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;

        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH-minH; j++) {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }

    public Mat getSpectrum() {
        return mSpectrum;
    }

    public void setMinContourArea(double area) {
        mMinContourArea = area;
    }

    public void process(Mat rgbaImage) {
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);


        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();

        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        mDrawRect = rgbaImage;
        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        Point pt1;
        Point pt2;
        List<Integer> xpos = new ArrayList<Integer>();
        List<Integer> ypos = new ArrayList<Integer>();
        List<Rect> rects = new ArrayList<Rect>();

        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
                Core.multiply(contour, new Scalar(4,4), contour);
                //get the center of all blobs
                Rect rect = Imgproc.boundingRect(contour);
                rects.add(rect);
                xpos.add(rect.x+ (int)(0.5*rect.width));
                ypos.add(rect.y+ (int)(0.5*rect.height));
                mContours.add(contour);
                }
        }

        if (xpos.size()>0) {
            List<Double> distances = new ArrayList<Double>();
            for (int i = 0; i < xpos.size(); i++) {
                distances.add(Math.sqrt(Math.pow(x_old - xpos.get(i), 2) + Math.pow(y_old - ypos.get(i), 2)));
            }
            int index = 0;
            double smallestDist = distances.get(0);
            for (int i = 0; i < distances.size(); i++) {
                if (distances.get(i) < smallestDist) {
                    smallestDist = distances.get(i);
                    index = i;
                }
            }
            x = xpos.get(index);
            y = ypos.get(index);
            Rect rect = rects.get(index);
            pt1 = new Point(rect.x, rect.y);
            pt2 = new Point(rect.x+rect.width, rect.y+rect.height);
            Scalar s = new Scalar(0,0,255);
            Imgproc.rectangle(mDrawRect, pt1, pt2, s, 5);
        }else{
            x = x_old;
            y = y_old;
        }

        x_old = x;
        y_old = y;




    }

    public List<MatOfPoint> getContours() {
        return mContours;
    }
}