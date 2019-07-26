package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import android.content.ClipData.Item
import android.content.ContentValues


@Entity(foreignKeys = [
    ForeignKey(entity = RealEstate::class,
            parentColumns = ["id"],
            childColumns = ["realEstateId"],
            onDelete = CASCADE)])
data class Photo(@PrimaryKey(autoGenerate = true) val id: Long? = null, var realEstateId: String, var url: String, var text: String?) {

    // --- UTILS ---
    fun fromContentValues(values: ContentValues): Photo {
        var photo = Photo(null, "", "", "")

        if (values.containsKey("text")) photo.text = values.getAsString("text")
        if (values.containsKey("url")) photo.url = values.getAsString("url")
        if (values.containsKey("realEstateId")) photo.realEstateId = values.getAsString("realEstateId")

        return photo
    }
}


