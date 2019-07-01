package com.openclassrooms.realestatemanager.repositories

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.models.RealEstate


class RealEstateDataRepository(val realEstateDao: RealEstateDao) {

    // --- GET USER ---
    fun getRealEstate(realEstateId: Long): LiveData<RealEstate> {
        return this.realEstateDao.getRealEstate(realEstateId)
    }
}
