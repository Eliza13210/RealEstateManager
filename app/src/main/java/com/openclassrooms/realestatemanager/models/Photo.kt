package com.openclassrooms.realestatemanager.models

import android.content.ContentValues
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey


@Entity(foreignKeys = [
    ForeignKey(entity = RealEstate::class,
            parentColumns = ["id"],
            childColumns = ["realEstateId"],
            onDelete = CASCADE)])
data class Photo(@PrimaryKey(autoGenerate = true) val id: Long? = null, var realEstateId: Long, var url: String, var text: String? = "") {

    companion object {
        // --- UTILS ---
        fun fromContentValues(values: ContentValues): Photo {
            val photo = Photo(null, values.getAsLong("realEstateId"), values.getAsString("url"))

            if (values.containsKey("text")) photo.text = values.getAsString("text")
            return photo
        }
    }
}


