package com.example.aplikacijazasportsketerene.UserInterface.Home

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.DataClasses.Court
import com.example.aplikacijazasportsketerene.DataClasses.User
import com.example.aplikacijazasportsketerene.Services.DatastoreService
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import com.example.aplikacijazasportsketerene.SingletonViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeScreenViewModel private constructor(): ViewModel(){

    val courts = mutableStateListOf<Court>()
    var selectedUser = mutableStateOf<User?>(null)
    val selectedUserProfilePicture = mutableStateOf<Uri>(Uri.Builder().path("").build())
    val showButtonForUsers = mutableStateOf(false)
    val selectedMarkerForUser =  mutableStateOf<Court?>(null)
    val loadingProfilePicture = mutableStateOf(false)
    companion object : SingletonViewModel<HomeScreenViewModel>(){
        fun getInstance() = getInstance(HomeScreenViewModel::class.java) { HomeScreenViewModel() }
    }

    fun getCourts() {
        viewModelScope.launch(Dispatchers.IO) {
            val newCourts = FirebaseDBService.getClassInstance().getAllCourts()
            val currentCourts = courts.toList() //
            newCourts.forEach { court ->
                var alreadyContained = false
                currentCourts.forEach {
                    if (it.id == court.id)
                        alreadyContained = true
                }
                if (!alreadyContained) {
                    withContext(Dispatchers.Main) {
                        courts.add(court)
                    }
                }
            }
        }
    }


    fun getSelectedUsersProfilePicture(userId: String){
        loadingProfilePicture.value = true
        viewModelScope.launch(Dispatchers.IO) {
                val imgUri = DatastoreService.getClassInstance()
                    .downloadProfilePicture(userId){
                        selectedUserProfilePicture.value = Uri.Builder()
                            .path("101")
                            .build()
                        loadingProfilePicture.value = false
                    }
            withContext(Dispatchers.Main){
                if (imgUri != null) {
                    selectedUserProfilePicture.value = imgUri
                }
                loadingProfilePicture.value = false
            }
        }
    }

    fun updateCourt(court: Court){
        val index = courts.indexOfFirst { it.id == court.id }
        courts[index] = court
    }


}