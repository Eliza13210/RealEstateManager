package com.openclassrooms.realestatemanager.database;

import android.content.Context;

import com.openclassrooms.realestatemanager.database.dao.PhotoDao;
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao;
import com.openclassrooms.realestatemanager.models.Photo;
import com.openclassrooms.realestatemanager.models.RealEstate;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {RealEstate.class, Photo.class}, version = 1, exportSchema = false)
public abstract class RealEstateDatabase extends RoomDatabase {

    // --- SINGLETON ---
    private static volatile RealEstateDatabase INSTANCE;

    // --- DAO ---
    public abstract RealEstateDao mRealEstateDao();

    public abstract PhotoDao mPhotoDao();


    // --- INSTANCE ---
    public static RealEstateDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RealEstateDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RealEstateDatabase.class, "MyDatabase.db")
                           // .addCallback(prepopulateDatabase())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // ---
/**
    private static Callback prepopulateDatabase() {
        return new Callback() {

            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);

                ContentValues contentValues = new ContentValues();
                contentValues.put("id", "1");
                contentValues.put("agent", "Théo");
                contentValues.put("type", "flat");
                contentValues.put("price", "31000");
                contentValues.put("description", "Beautiful flat");
                contentValues.put("surface", "400");
                contentValues.put("rooms", "8");
                contentValues.put("bathrooms", "4");
                contentValues.put("bedrooms", "4");
                contentValues.put("address", "123 baker street, NY");

                db.insert("RealEstate", OnConflictStrategy.IGNORE, contentValues);

                contentValues = new ContentValues();
                contentValues.put("realEstateId", 1);
                contentValues.put("id", 1);
                contentValues.put("url", "https://www.hommemaker.com/wp-content/uploads/2018/11/Kitchen_1_004_With_Food-OS.jpg");
                contentValues.put("text", "kitchen;");

                db.insert("Photo", OnConflictStrategy.IGNORE, contentValues);
            }
        };
    }*/

}
