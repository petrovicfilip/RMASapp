package com.example.aplikacijazasportsketerene.DataClasses

data class User(
    val id: String="",
    val email: String="",
    val username: String="",
    val firstName: String? = "",
    val lastName: String? = "",
    val phoneNumber: String? = "",
    val profilePicture: String? = null
)
