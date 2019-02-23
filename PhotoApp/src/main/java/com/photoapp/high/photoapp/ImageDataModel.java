package com.photoapp.high.photoapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageDataModel implements Serializable {

    protected static List<String> allUriImages;
    protected static List<String> allHashtagImages;


    public ImageDataModel() {
        allUriImages = new ArrayList<String>();
        allHashtagImages = new ArrayList<String>();
    }

    public static List<String> getAllUriImages() {
        return allUriImages;
    }

    public static List<String> getAllHashtagImages() {
        return allHashtagImages;
    }

    public static int getAllUriImagesSize() {
        return allUriImages.size();
    }

    public static int getAllHashtagImagesSize() {
        return allHashtagImages.size();
    }



}
