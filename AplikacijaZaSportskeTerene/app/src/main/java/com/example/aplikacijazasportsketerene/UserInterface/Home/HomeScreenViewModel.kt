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
    val selectedUserProfilePicture = mutableStateOf<Uri?>(null)
    val showButtonForUsers = mutableStateOf(false)
    val selectedMarkerForUser =  mutableStateOf<Court?>(null)
    companion object : SingletonViewModel<HomeScreenViewModel>(){
        fun getInstance() = getInstance(HomeScreenViewModel::class.java) { HomeScreenViewModel() }
    }

    fun getCourts(){
        viewModelScope.launch(Dispatchers.IO) {
            val newCourts = FirebaseDBService.getClassInstance().getAllCourts()
            newCourts.forEach{ court ->
                var alreadyContained = false
                courts.forEach {
                    if(it.id == court.id)
                        alreadyContained = true
                }
                if(!alreadyContained)
                    withContext(Dispatchers.Main){
                        courts.add(court)
                    }
               /* Location("first").apply{
                    latitude = CurrentUserLocation.getClassInstance().location.value!!.latitude
                    longitude = CurrentUserLocation.getClassInstance().location.value!!.latitude
                }.distanceTo()*/
            }
        }
    }

    fun getSelectedUsersProfilePicture(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                selectedUserProfilePicture.value = DatastoreService.getClassInstance()
                    .downloadProfilePicture(userId)
            }
        }
    }

    fun updateCourt(court: Court){
        val index = courts.indexOfFirst { it.id == court.id }
        courts[index] = court
    }


}