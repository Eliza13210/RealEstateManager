package com.openclassrooms.realestatemanager.repositories

import android.content.ClipData.Item
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.PhotoDao
import com.openclassrooms.realestatemanager.models.Photo


class PhotoDataRepository(val photoDao: PhotoDao) {

    // --- GET ---

    fun getPhotos(realEstateId: String): LiveData<List<Photo>> {
        return this.photoDao.getPhotos(realEstateId)
    }

    // --- CREATE ---

    fun createPhoto(photo: Photo) {
        photoDao.insertPhoto(photo)
    }

    // --- DELETE ---
    fun deletePhoto(photoId: Long) {
        photoDao.deletePhoto(photoId)
    }

    // --- UPDATE ---
    fun updatePhoto(photo: Photo) {
        photoDao.updatePhoto(photo)
    }

}
