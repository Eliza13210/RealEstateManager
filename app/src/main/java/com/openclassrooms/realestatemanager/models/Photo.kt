package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(entity = RealEstate::class,
            parentColumns = ["id"],
            childColumns = ["realEstateId"],
            onDelete = CASCADE)])
data class Photo(@PrimaryKey(autoGenerate = true) var id: Long, val realEstateId: Long, var url: String, val text: String)


