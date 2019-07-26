package com.openclassrooms.realestatemanager.database.dao;

import android.database.Cursor;

import com.openclassrooms.realestatemanager.models.Photo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface PhotoDao {
    @Query("SELECT * FROM Photo WHERE realEstateId = :realEstateId")
    LiveData<List<Photo>> getPhotos(String realEstateId);

    @Query("SELECT * FROM Photo WHERE realEstateId = :realEstateId")
    Cursor getPhotoWithCursor(String realEstateId);

    @Insert
    long insertPhoto(Photo photo);

    @Update
    int updatePhoto(Photo photo);

    @Query("DELETE FROM Photo WHERE id = :photoId")
    int deletePhoto(long photoId);
}

