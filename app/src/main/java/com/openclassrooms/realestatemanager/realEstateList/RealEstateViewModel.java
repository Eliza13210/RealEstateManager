package com.openclassrooms.realestatemanager.realEstateList;

import com.openclassrooms.realestatemanager.models.Photo;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.repositories.RealEstateDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

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

    public void init(long realestateId) {
        if (this.currentRealEstate != null) {
            return;
        }
        currentRealEstate = realEstateDataSource.getRealEstate(realestateId);
    }

    // -------------
    // FOR REAL ESTATE
    // -------------

    public LiveData<RealEstate> getRealEstate(long realEstateId) {
        return this.currentRealEstate;
    }

    public LiveData<List<RealEstate>> getAllItems() {
        return realEstateDataSource.getAllItems();
    }

    public void createRealEstate(RealEstate realEstate){
        realEstateDataSource.createRealEstate(realEstate);
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

