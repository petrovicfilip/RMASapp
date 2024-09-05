package com.example.aplikacijazasportsketerene.DataClasses

import com.google.firebase.Timestamp

data class Review(
    val userId: String = "",
    val eventId: String = "",
    val value: Int = 0,
    val dateAdded: Timestamp = Timestamp.now()
)
