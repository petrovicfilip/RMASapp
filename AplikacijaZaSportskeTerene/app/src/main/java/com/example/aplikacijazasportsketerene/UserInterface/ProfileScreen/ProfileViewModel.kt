package com.example.aplikacijazasportsketerene.UserInterface.ProfileScreen

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.Services.DatastoreService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    val findingCourtsStarted = mutableStateOf(false)

    fun getUserProfilePicture(){
        viewModelScope.launch(Dispatchers.IO) {
            val uri = DatastoreService.getClassInstance().downloadProfilePicture(Firebase.auth.currentUser!!.uid)
            if (uri != null)
            withContext(Dispatchers.Main){
                profilePicture = uri
            }
        }
    }

}