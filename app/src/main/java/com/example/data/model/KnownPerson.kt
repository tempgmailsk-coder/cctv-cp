package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "known_persons")
data class KnownPerson(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val relationship: String, // "Family / Homeowner", "Relative", "Regular Visitor", "Staff / Delivery"
    val photoUri: String = "",
    val faceTag: String = "",
    val confidenceBaseline: Float = 0.85f,
    val addedTimestamp: Long = System.currentTimeMillis()
)
