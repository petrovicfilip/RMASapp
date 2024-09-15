package com.example.aplikacijazasportsketerene.UserInterface.UsersProfile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.DataClasses.User
import com.example.aplikacijazasportsketerene.Services.DatastoreService
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import com.example.aplikacijazasportsketerene.SingletonViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersProfileScreenViewModel : ViewModel() {

    /*companion object : SingletonViewModel<UsersProfileScreenViewModel>(){
        fun getInstance() = Companion.getInstance(UsersProfileScreenViewModel::class.java){ UsersProfileScreenViewModel() }
    }*/
    var profilePicture by mutableStateOf<Uri?>(null)
    val findingCourtsStarted = mutableStateOf(false)
    //val user = mutableStateOf(User()) //REFAKTORISATI

    val username = mutableStateOf("")
    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
    val phoneNumber = mutableStateOf("")
    val email = mutableStateOf("")
    val points = mutableDoubleStateOf(0.0)

    fun loadUserData(userId: String){
        viewModelScope.launch(Dispatchers.IO){
            val fetchedUser = FirebaseDBService.getClassInstance().getUser(userId)

            withContext(Dispatchers.Main){
                username.value = fetchedUser!!.username
                firstName.value = fetchedUser.firstName!!
                lastName.value = fetchedUser.lastName!!
                phoneNumber.value = fetchedUser.phoneNumber!!
                email.value = fetchedUser.email
                points.doubleValue = fetchedUser.points
            }
        }
    }

    fun getUserProfilePicture(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val uri = DatastoreService.getClassInstance().downloadProfilePicture(userId)
            if (uri != null)
                withContext(Dispatchers.Main){
                    profilePicture = uri
                }
        }
    }



}