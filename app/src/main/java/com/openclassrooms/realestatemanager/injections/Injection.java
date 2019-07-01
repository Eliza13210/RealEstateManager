package com.openclassrooms.realestatemanager.injections;

import android.content.Context;

import com.openclassrooms.realestatemanager.database.RealEstateDatabase;
import com.openclassrooms.realestatemanager.repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.repositories.RealEstateDataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {

    public static PhotoDataRepository providePhotoDataSource(Context context) {
        RealEstateDatabase database = RealEstateDatabase.getInstance(context);
        return new PhotoDataRepository(database.mPhotoDao());
    }

    public static RealEstateDataRepository provideRealEstateDataSource(Context context) {
        RealEstateDatabase database = RealEstateDatabase.getInstance(context);
        return new RealEstateDataRepository(database.mRealEstateDao());
    }

    public static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        PhotoDataRepository dataSourceItem = providePhotoDataSource(context);
        RealEstateDataRepository dataSourceUser = provideRealEstateDataSource(context);
        Executor executor = provideExecutor();
        return new ViewModelFactory(dataSourceItem, dataSourceUser, executor);
    }
}
