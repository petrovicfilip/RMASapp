package com.example.aplikacijazasportsketerene.DataClasses

import com.google.firebase.firestore.GeoPoint

data class Court(
    val id: String? = "",
    val userId: String? = "",
    val type: String = "",
    val description: String = "",

    val rating: Int = 0,
    val ratedBy: Int = 0,

    val latLon: GeoPoint = GeoPoint(0.0,0.0),
    val street: String = "",
    val city: String = "",
    val visitedBy: Int = 0
)
