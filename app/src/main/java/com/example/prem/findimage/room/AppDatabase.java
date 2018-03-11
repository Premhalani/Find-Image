package com.example.prem.findimage.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.prem.findimage.dataobjects.SearchObject;

/**
 * Created by Prem on 07-Mar-18.
 */

/**
 *  Creates and maintains a database to store search history
 */
@Database(entities = {SearchObject.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
    private static AppDatabase INSTANCE;

    public static AppDatabase getAppDatabase(Context context){
        if(INSTANCE == null)
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"search-history").build();
        return INSTANCE;
    }

    public void destroyInstance(){
        INSTANCE = null;
    }
}
