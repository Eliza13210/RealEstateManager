package com.openclassrooms.realestatemanager.database;

import android.content.ContentValues;
import android.content.Context;

import com.openclassrooms.realestatemanager.database.dao.PhotoDao;
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao;
import com.openclassrooms.realestatemanager.models.Photo;
import com.openclassrooms.realestatemanager.models.RealEstate;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
                                .addCallback(prepopulateDatabase())
                                .build();
                    }
                }
            }
            return INSTANCE;
        }

        // ---

        private static Callback prepopulateDatabase(){
            return new Callback() {

                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", 1);
                    contentValues.put("type", "flat");
                    contentValues.put("price", "31000");
                    contentValues.put("location", "Manhattan");
                    contentValues.put("photo", "https://images.victorianplumbing.co.uk/images/Legend-Traditional-Bathroom-Suite_P.jpg");

                    db.insert("RealEstate", OnConflictStrategy.IGNORE, contentValues);
                }
            };
        }

}
