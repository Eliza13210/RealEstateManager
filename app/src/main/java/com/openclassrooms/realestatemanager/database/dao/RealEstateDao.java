package com.openclassrooms.realestatemanager.database.dao;

import android.database.Cursor;

import com.openclassrooms.realestatemanager.models.RealEstate;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

@Dao
public interface RealEstateDao {

    @Query("SELECT * FROM RealEstate")
    LiveData<List<RealEstate>> getAllRealEstates();

    //CHECK IF REAL ESTATE ALREADY EXISTS
    @Query("SELECT * FROM RealEstate WHERE latitude = :latitude AND longitude =:longitude")
    LiveData<RealEstate> checkLatLng(String latitude, String longitude);

    //TELL WHICH TABLE TO BE OBSERVED IN ORDER TO GET LIVE DATA
    @RawQuery(observedEntities = RealEstate.class)
    LiveData<List<RealEstate>> searchRealEstates(SupportSQLiteQuery query);

    @Query("SELECT * FROM RealEstate WHERE id = :id")
    LiveData<RealEstate> getRealEstate(long id);

    @Query("SELECT * FROM RealEstate WHERE id = :id")
    Cursor getRealEstateWithCursor(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createRealEstate(RealEstate realEstate);

    @Update
    int updateItem(RealEstate item);

    @Query("DELETE FROM RealEstate WHERE id = :itemId")
    int deleteItem(long itemId);

}
