package com.example.prem.findimage.util;

import android.content.Context;

import com.example.prem.findimage.dataobjects.SearchObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prem on 09-Mar-18.
 */

/**
 * Provides methods to apply on Searchobject data
 */
public class DataHelper {
    Context context;
    public DataHelper(Context context){
        this.context = context;
    }
    public static List<SearchObject> filterData(String query, List<SearchObject> history){
        List<SearchObject> result = new ArrayList<>();
        for(SearchObject object : history){
            if(object.getBody().contains(query))
                result.add(object);
        }
        return result;
    }
    public static int alreadyExist(String query,List<SearchObject> history){
        int i=0;
        for(SearchObject object : history){
            if(object.getBody().equals(query))
            {
                return i;
            }
            i++;
        }
        return -1;
    }

}
