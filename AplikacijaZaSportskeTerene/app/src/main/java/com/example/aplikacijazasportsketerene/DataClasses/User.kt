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
    val latLon : GeoPoint? = null
)
