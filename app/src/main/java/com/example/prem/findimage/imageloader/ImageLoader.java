package com.example.prem.findimage.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;

import com.example.prem.findimage.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Custom Image loader that uses LRU Cache to cache the images and displays the images from the url into the recycler view.
 * Uses thread pool executor to use worker threads to asynchronously download multiple images (5) and display them.
 */

public class ImageLoader {
    private Context context;
    private Map<ImageView,String> imageViewMap = Collections.synchronizedMap(new WeakHashMap<ImageView,String>());
    private Cache cache;
    private ExecutorService executorService;
    private Handler handler = new Handler();
    public ImageLoader(Context context){
        this.context = context;
        cache = Cache.getCache();
        executorService = Executors.newFixedThreadPool(5);
    }

    /**
     * Main method that is called by the adapter to display the image.
     * Checks the cache for the image, if there then uses that otherwise intiates the download process
     * Also sets image stub while the task is being executed
     * @param imageView
     * @param url
     */
    public void displayImageInView(ImageView imageView, String url){
        imageViewMap.put(imageView,url);
        Bitmap bitmap = cache.getBitmapFromMemCache(url);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }else{
            putBitmapInQueue(url,imageView);
            imageView.setImageResource(R.drawable.ic_white);
        }
    }

    /**
     * Creates a new photo object and new runnable and passes the runnable to the thread pool to execute when threads are available
     * @param url
     * @param imageView
     */
    public void putBitmapInQueue(String url, ImageView imageView){
        Photo photo = new Photo(imageView, url);
        PhotoLoadRunnable photoLoadRunnable = new PhotoLoadRunnable(photo);
        executorService.submit(photoLoadRunnable);
    }

    /**
     * The bitmap is downloaded from the given Url using URL connection
     * @param url
     * @return
     */
    public Bitmap getBitmapFromUrl(String url){
        Bitmap bitmap = null;
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setInstanceFollowRedirects(true);
            InputStream is=connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            addBitmapToCache(url,bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public boolean imageViewRecycled(Photo photo){
        String key = imageViewMap.get(photo.imageView);
        if(key == null || !key.equals(photo.url)){
            return true;
        }
        return false;
    }

    /**
     * Adds bitmap to the lru cache
     * @param url
     * @param bitmap
     */
    public void addBitmapToCache(String url,Bitmap bitmap){
        cache.addBitmapToCache(url,bitmap);
    }

    /**
     * Custom object to store the mapping between imageview and url
     */
    private class Photo{
        ImageView imageView;
        String url;
        public Photo(ImageView imageView, String url){
            this.imageView = imageView;
            this.url = url;
        }
    }


    /**
     * Runnable that gets the bitmap from the url and also calls the handler to update the ui
     * This runnable is passed to the thread pool executor to execute when a thread is available
     */
    private class PhotoLoadRunnable implements Runnable {
        private Photo photo;
        public PhotoLoadRunnable(Photo photo){
            this.photo = photo;
        }
        @Override
        public void run() {

            Bitmap bitmap = getBitmapFromUrl(photo.url);
            if(imageViewRecycled(photo))
                return;
            BitmapDisplayer bitmapDisplayer = new BitmapDisplayer(photo,bitmap);
            handler.post(bitmapDisplayer);
        }
    }

    /**
     * Responseible for displaying the image in the imageview
     * Runs inside the handler on main UI thread
     */
    private class BitmapDisplayer implements Runnable{
        private Photo photo;
        private Bitmap bitmap;
        public BitmapDisplayer(Photo photo, Bitmap bitmap){
            this.bitmap = bitmap;
            this.photo = photo;
        }

        @Override
        public void run() {
            if(imageViewRecycled(photo))
                return;
            if(bitmap != null)
                photo.imageView.setImageBitmap(bitmap);
        }
    }
}
