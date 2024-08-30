package com.example.aplikacijazasportsketerene.Location

import android.location.Location
import com.example.aplikacijazasportsketerene.Services.AccountService
import kotlinx.coroutines.flow.MutableStateFlow

class CurrentUserLocation {

    companion object{
        var instance: CurrentUserLocation? = null

        fun getClassInstance(): CurrentUserLocation {
            return instance ?: synchronized(this) {
                return instance ?: CurrentUserLocation().also { instance = it }
            }
        }
    }

    var location = MutableStateFlow<Location?>(null)
}