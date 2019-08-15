package com.openclassrooms.realestatemanager.models

import android.content.ContentValues
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class RealEstate(@PrimaryKey(autoGenerate = true) val id: Long? = null,
                      var type: String? = null,
                      var price: String? = null,
                      var latitude: String? = null,
                      var longitude: String? = null,
                      var description: String? = null,
                      var surface: String? = null,
                      var bedrooms: String? = null,
                      var rooms: String? = null,
                      var bathrooms: String? = null,
                      var address: String,
                      var city: String,
                      var sold: String = "false",
                      var startDate: String? = null,
                      var endDate: String? = null,
                      var agent: String,
                      var pointsOfInterest: String? = null) {


    companion object {

        // --- UTILS ---
        fun fromContentValues(values: ContentValues): RealEstate {
            val realEstate = RealEstate(values.getAsLong("realEstateId"),
                    address = values.getAsString("address"),
                    city = values.getAsString("city"),
                    agent = values.getAsString("agent"))

            if (values.containsKey("type")) realEstate.type = values.getAsString("type")
            if (values.containsKey("price")) realEstate.price = values.getAsString("price")
            if (values.containsKey("latitude")) realEstate.latitude = values.getAsString("latitude")
            if (values.containsKey("longitude")) realEstate.longitude = values.getAsString("longitude")
            if (values.containsKey("description")) realEstate.description = values.getAsString("description")
            if (values.containsKey("surface")) realEstate.surface = values.getAsString("surface")
            if (values.containsKey("bedrooms")) realEstate.bedrooms = values.getAsString("bedrooms")
            if (values.containsKey("rooms")) realEstate.rooms = values.getAsString("rooms")
            if (values.containsKey("bathrooms")) realEstate.bathrooms = values.getAsString("bathrooms")
            if (values.containsKey("sold")) realEstate.sold = values.getAsString("sold")
            if (values.containsKey("startDate")) realEstate.startDate = values.getAsString("startDate")
            if (values.containsKey("endDate")) realEstate.endDate = values.getAsString("endDate")

            return realEstate
        }
    }
}