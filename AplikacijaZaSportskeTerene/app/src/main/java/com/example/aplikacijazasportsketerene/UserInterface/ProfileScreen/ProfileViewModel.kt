package com.example.aplikacijazasportsketerene.UserInterface.ProfileScreen

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.DataClasses.User
import com.example.aplikacijazasportsketerene.Services.AccountService
import com.example.aplikacijazasportsketerene.Services.DatastoreService
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import com.example.aplikacijazasportsketerene.SingletonViewModel
import com.example.aplikacijazasportsketerene.UserInterface.Splash.SplashScreenViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel private constructor(): ViewModel() {
    companion object : SingletonViewModel<ProfileViewModel>() {
        fun getInstance() : ProfileViewModel = getInstance(ProfileViewModel::class.java) { ProfileViewModel() }
    }

    var profilePicture by mutableStateOf<Uri?>(Uri.Builder().path("").build())
    val findingCourtsStarted = mutableStateOf(false)
    val loadingProfilePicture = mutableStateOf(false)
    //val user = mutableStateOf(User()) //REFAKTORISATI

    val username = mutableStateOf("")
    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
    val phoneNumber = mutableStateOf("")
    val email = mutableStateOf("")
    val points = mutableDoubleStateOf(0.0)

    fun loadUserData(){
        viewModelScope.launch(Dispatchers.IO){
            val user = FirebaseDBService.getClassInstance().getUser(Firebase.auth.currentUser!!.uid)

            withContext(Dispatchers.Main){
                username.value = user!!.username
                firstName.value = user.firstName!!
                lastName.value = user.lastName!!
                phoneNumber.value = user.phoneNumber!!
                email.value = user.email
                points.doubleValue = user.points
            }
        }
    }

    fun getUserProfilePicture(){
        viewModelScope.launch(Dispatchers.IO) {
            val uri = Firebase.auth.currentUser?.let {
                DatastoreService.getClassInstance().downloadProfilePicture(
                    it.uid) {
                    profilePicture = Uri.Builder()
                        .path("")
                        .build()
                    loadingProfilePicture.value = false
                }
            }
            if (uri != null)
                withContext(Dispatchers.Main){
                    profilePicture = uri
                    loadingProfilePicture.value = false
                }
        }
    }
    fun signOut(onSignOutProgress: () -> Unit, onSignedOut: () -> Unit){
        onSignOutProgress()
        AccountService.getClassInstance().signOut()
        SingletonViewModel.reset()
        onSignedOut()
    }
}