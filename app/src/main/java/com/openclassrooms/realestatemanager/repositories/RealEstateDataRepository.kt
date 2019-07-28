package com.openclassrooms.realestatemanager.repositories

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.models.RealEstate


class RealEstateDataRepository(val realEstateDao: RealEstateDao) {

    // --- GET REAL ESTATE ---
    fun getRealEstate(realEstateId: Long): LiveData<RealEstate> {
        return this.realEstateDao.getRealEstate(realEstateId)
    }

    // --- GET ALL DATA ---

    fun getAllItems(): LiveData<MutableList<RealEstate>>? {
        return this.realEstateDao.getAllRealEstates()
    }

    // --- CREATE ---

    fun createRealEstate(realEstate: RealEstate): Long {
        return this.realEstateDao.createRealEstate(realEstate)
    }

    // --- DELETE ---
    fun deleteRealEstate(realEstateId: Long) {
        this.realEstateDao.deleteItem(realEstateId)
    }

    // --- UPDATE ---
    fun updateRealEstate(realEstate: RealEstate) {
        this.realEstateDao.updateItem(realEstate)
    }

}
