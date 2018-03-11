package com.example.prem.findimage.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.prem.findimage.dataobjects.SearchObject;

import java.util.List;

/**
 * Created by Prem on 07-Mar-18.
 */

/**
 * DAO for room to execute various queries
 */
@Dao
public interface TaskDao {
    @Query("SELECT * FROM searchobject")
    List<SearchObject> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertALL(SearchObject... tasks);

    @Query("DELETE FROM searchobject")
    void deleteAll();

    @Delete
    void delete(SearchObject task);

}
