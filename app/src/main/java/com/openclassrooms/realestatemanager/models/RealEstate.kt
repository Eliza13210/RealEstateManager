package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class RealEstate(@PrimaryKey(autoGenerate = true) val id: Long? = null,
                      var type: String?,
                      var price: String?,
                      var latitude: String?,
                      var longitude: String?,
                      var description: String?,
                      var surface: String?,
                      var bedrooms: String?,
                      var rooms: String?,
                      var bathrooms: String?,
                      var address: String?,
                      var sold: Boolean?,
                      var startDate: String?,
                      var endDate: String?,
                      var agent: String

//TODO POINTS OF INTEREST
)