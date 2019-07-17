package com.openclassrooms.realestatemanager.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.models.RealEstate


class RealEstateDataRepository(val realEstateDao: RealEstateDao) {

    // --- GET REAL ESTATE ---
    fun getRealEstate(realEstateId: String): LiveData<RealEstate> {
        return this.realEstateDao.getRealEstate(realEstateId)
    }

    // --- GET ALL DATA ---

    fun getAllItems(): LiveData<MutableList<RealEstate>>? {
        return this.realEstateDao.getAllRealEstates()
    }

    // --- CREATE ---

    fun createRealEstate(realEstate: RealEstate) {
        this.realEstateDao.createRealEstate(realEstate)
    }

    // --- DELETE ---
    fun deleteRealEstate(realEstateId: String) {
        this.realEstateDao.deleteItem(realEstateId)
    }

    // --- UPDATE ---
    fun updateRealEstate(realEstate: RealEstate) {
        this.realEstateDao.updateItem(realEstate)
    }

}
