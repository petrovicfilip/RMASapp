package com.example.aplikacijazasportsketerene.UserInterface.Loading

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.aplikacijazasportsketerene.SingletonViewModel

class LoadingScreenViewModel private constructor() : ViewModel() {

    companion object : SingletonViewModel<LoadingScreenViewModel>() {
        fun getInstance() = Companion.getInstance(LoadingScreenViewModel::class.java){LoadingScreenViewModel()}
    }

    val makingAccount = mutableStateOf(false)
    val uploadingCourtBasicInfo = mutableStateOf(false)
    val loggingIn = mutableStateOf(false)
    val uploadingProfilePicture = mutableStateOf(false)
    val uploadingCourtPictures = mutableStateOf(false)

}