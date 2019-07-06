package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class RealEstate(@PrimaryKey(autoGenerate = true) val id: Long? = null,
                      var photo: String,
                      var type: String,
                      var price: String,
                      var location: String,
                      var description :String,
                      var surface: String,
                      var bedrooms: String,
                      var rooms: String,
                      var bathrooms:String,
                      var address: String
                      )