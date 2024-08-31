package com.example.aplikacijazasportsketerene.UserInterface.all

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.aplikacijazasportsketerene.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SplashScreenViewModel : ViewModel() {


    companion object{
        var instance : SplashScreenViewModel? = null

        fun getClassInstance() : SplashScreenViewModel {

            return instance ?: synchronized(this) {
                return instance ?: SplashScreenViewModel().also { instance = it }
            }
        }
    }

    fun redirect(navController: NavController) {
        navController.popBackStack(Screen.Splash.name, inclusive = true) // razmotriti

        if(Firebase.auth.currentUser == null)
            navController.navigate(Screen.LogIn.name)
        else
            navController.navigate(Screen.Home.name)
    }
}