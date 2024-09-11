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
import com.example.aplikacijazasportsketerene.Services.DatastoreService
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
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
            val uri = DatastoreService.getClassInstance().downloadProfilePicture(Firebase.auth.currentUser!!.uid)
            if (uri != null)
            withContext(Dispatchers.Main){
                profilePicture = uri
            }
        }
    }

}