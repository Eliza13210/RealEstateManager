package com.openclassrooms.realestatemanager.database.dao;

import com.openclassrooms.realestatemanager.models.RealEstate;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RealEstateDao {

        @Query("SELECT * FROM RealEstate")
        LiveData<List<RealEstate>> getAllRealEstates();

        @Query("SELECT * FROM RealEstate WHERE id = :id")
        LiveData<RealEstate> getRealEstate(long id);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void createRealEstate(RealEstate realEstate);

        @Update
        int updateItem(RealEstate item);

        @Query("DELETE FROM RealEstate WHERE id = :itemId")
        int deleteItem(long itemId);

}
