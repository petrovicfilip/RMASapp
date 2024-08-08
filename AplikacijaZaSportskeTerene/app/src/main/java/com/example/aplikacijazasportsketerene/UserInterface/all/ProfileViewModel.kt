package com.example.aplikacijazasportsketerene.UserInterface.all

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.Services.DatastoreService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class ProfileViewModel private constructor(): ViewModel() {

    companion object{
        var instance: ProfileViewModel? = null

        fun getClassInstance(): ProfileViewModel {
            return instance ?: synchronized(this) {
                return instance ?: ProfileViewModel().also { instance = it }
            }
        }
    }

    var profilePicture by mutableStateOf<Uri?>(null)

    fun getUserProfilePicture(){
        viewModelScope.launch {
            DatastoreService.getClassInstance().downloadProfilePicture(Firebase.auth.currentUser!!.uid)
        }
    }

}