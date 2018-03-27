package com.example.prem.findimage.dataobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prem on 27-Mar-18.
 */

public class ImageFactory {
    List<Image> imageList;
    public static ImageFactory imageFactory;
    private ImageFactory (){
         imageList = new ArrayList<>();
    }

    public static ImageFactory getImageFactory(){
        if(imageFactory == null){
            imageFactory = new ImageFactory();
        }
        return imageFactory;
    }
    public Image getImage(int position){
        return imageList.get(position);
    }

    public void addImage(Image image){
        imageList.add(image);
    }

    public void clear(){
        imageList.clear();
    }

    public List<Image> getImageList(){
        return imageList;
    }

    public int size(){
        return imageList.size();
    }
}
