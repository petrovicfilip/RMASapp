package com.example.aplikacijazasportsketerene.DataClasses

import com.google.firebase.firestore.GeoPoint

data class User(
    val id: String="",
    val email: String="",
    val username: String="",
    val firstName: String? = "",
    val lastName: String? = "",
    val phoneNumber: String? = "",
    val profilePicture: String? = null,
    val latLon : GeoPoint? = GeoPoint(0.0,0.0),

    val points: Double = 0.0,

    val totalRating: Int = 0,
    val ratedBy: Int = 0
)
