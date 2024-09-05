package com.example.aplikacijazasportsketerene.UserInterface.Home

import android.location.Location
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.DataClasses.Court
import com.example.aplikacijazasportsketerene.Location.CurrentUserLocation
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import com.example.aplikacijazasportsketerene.SingletonViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeScreenViewModel private constructor(): ViewModel(){

    val courts = mutableStateListOf<Court>()

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

    fun updateCourt(court: Court){
        val index = courts.indexOfFirst { it.id == court.id }
        courts[index] = court
    }


}