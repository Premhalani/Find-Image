package com.example.prem.findimage.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * LRU Cache to store the images in memory for fast loading and smooth scrolling
 *
 */

public class Cache {

    private static Cache cache;
    private LruCache<String, Bitmap> lruCache;
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    // Use 1/8th of the available memory for this memory cache.
    final int cacheSize = maxMemory / 6;

    public Cache(){
        lruCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount()/1024;
            }
        };
    }

    public static Cache getCache(){
        if(cache == null){
            cache = new Cache();
        }
        return cache;
    }

    public void addBitmapToCache(String key, Bitmap bitmap){
        if(getBitmapFromMemCache(key)==null){
            lruCache.put(key,bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key){
        return lruCache.get(key);
    }


}
