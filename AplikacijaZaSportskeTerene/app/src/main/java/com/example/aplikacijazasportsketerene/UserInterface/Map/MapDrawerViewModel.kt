package com.example.aplikacijazasportsketerene.UserInterface.Map

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.aplikacijazasportsketerene.Services.AccountService
import com.example.aplikacijazasportsketerene.SingletonViewModel
import com.example.aplikacijazasportsketerene.UserInterface.SignUp.SignUpViewModel


class MapDrawerViewModel private constructor(): ViewModel() {

    companion object : SingletonViewModel<MapDrawerViewModel>() {
        fun getInstance() : MapDrawerViewModel = getInstance(MapDrawerViewModel::class.java) {
            MapDrawerViewModel()
        }
    }
}