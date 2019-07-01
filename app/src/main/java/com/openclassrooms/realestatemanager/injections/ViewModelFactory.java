package com.openclassrooms.realestatemanager.injections;

import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel;
import com.openclassrooms.realestatemanager.repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.repositories.RealEstateDataRepository;

import java.util.concurrent.Executor;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final PhotoDataRepository photoDataSource;
    private final RealEstateDataRepository realEstateDataSource;
    private final Executor executor;

    public ViewModelFactory(PhotoDataRepository photoDataSource, RealEstateDataRepository realEstateDataSource, Executor executor) {
        this.photoDataSource = photoDataSource;
        this.realEstateDataSource = realEstateDataSource;
        this.executor = executor;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RealEstateViewModel.class)) {
            return (T) new RealEstateViewModel(photoDataSource, realEstateDataSource, executor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

