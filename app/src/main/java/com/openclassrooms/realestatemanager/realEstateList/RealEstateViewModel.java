package com.openclassrooms.realestatemanager.realEstateList;

import android.util.Log;

import com.openclassrooms.realestatemanager.models.Photo;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.repositories.RealEstateDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.sqlite.db.SimpleSQLiteQuery;

public class RealEstateViewModel extends ViewModel {

    // REPOSITORIES
    private final RealEstateDataRepository realEstateDataSource;
    private final PhotoDataRepository photoDataSource;
    private final Executor executor;

    // DATA
    @Nullable
    private LiveData<RealEstate> currentRealEstate;

    public RealEstateViewModel(PhotoDataRepository photoDataSource, RealEstateDataRepository realEstateDataSource, Executor executor) {
        this.photoDataSource = photoDataSource;
        this.realEstateDataSource = realEstateDataSource;
        this.executor = executor;
    }

    public void init(Long realestateId) {

        if (this.currentRealEstate != null) {
            return;
        }
        currentRealEstate = realEstateDataSource.getRealEstate(realestateId);
    }

    // -------------
    // FOR REAL ESTATE
    // -------------

    public LiveData<RealEstate> getRealEstate(long realEstateId) {
        return realEstateDataSource.getRealEstate(realEstateId);
    }

    public LiveData<List<RealEstate>> fetchAllRealEstates() {
        return realEstateDataSource.getAllRealEstates();
    }

    //HANDLE SEARCH QUERY
    public LiveData<List<RealEstate>> searchRealEstates(String query, String[] args) {
        return realEstateDataSource.searchRealEstates(query, args);
    }

    public long createRealEstate(RealEstate realEstate) {
        return realEstateDataSource.createRealEstate(realEstate);
    }

    public void updateRealEstate(RealEstate realEstate) {
        executor.execute(() -> {
            realEstateDataSource.updateRealEstate(realEstate);
        });
    }

    public void deleteRealEstate(long id) {
        executor.execute(() -> {
            realEstateDataSource.deleteRealEstate(id);
        });
    }

    // -------------
    // FOR PHOTO
    // -------------

    public LiveData<List<Photo>> getPhotos(long realEstateId) {
        return photoDataSource.getPhotos(realEstateId);
    }

    public void createPhoto(Photo photo) {
        executor.execute(() -> {
            photoDataSource.createPhoto(photo);
        });
    }

    public void deletePhoto(long photoId) {
        executor.execute(() -> {
            photoDataSource.deletePhoto(photoId);
        });
    }

    public void updatePhoto(Photo photo) {
        executor.execute(() -> {
            photoDataSource.updatePhoto(photo);
        });
    }

}

