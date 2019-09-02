package com.openclassrooms.realestatemanager.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.models.RealEstate
import java.util.*


class RealEstateDataRepository(val realEstateDao: RealEstateDao) {

    // --- GET REAL ESTATE ---
    fun getRealEstate(realEstateId: Long): LiveData<RealEstate> {
        return this.realEstateDao.getRealEstate(realEstateId)
    }

    // --- GET ALL DATA ---

    fun getAllRealEstates(): LiveData<MutableList<RealEstate>>? {
        return this.realEstateDao.getAllRealEstates()
    }

    fun searchRealEstates(query: String, args: Array<Any>): LiveData<MutableList<RealEstate>>? {
        return this.realEstateDao.searchRealEstates(SimpleSQLiteQuery(query,
                args))
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
