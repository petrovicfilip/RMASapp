package com.example.aplikacijazasportsketerene.DataClasses

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.gson.annotations.SerializedName

data class Court(
    val id: String? = "",
    val userId: String? = "",
    val type: String = "",
    val description: String = "",
    val name: String = "",

    val rating: Int = 0,
    val ratedBy: Int = 0,

    val latLon: GeoPoint = GeoPoint(0.0,0.0),
    val street: String = "",
    val city: String = "",
    val visitedBy: Int = 0,
    val timePosted: Timestamp = Timestamp.now()
)
