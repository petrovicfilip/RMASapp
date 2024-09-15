package com.example.aplikacijazasportsketerene.UserInterface.UserCourts

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.DataClasses.Court
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import com.example.aplikacijazasportsketerene.SingletonViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserCourtsScreenViewModel: ViewModel() {

    companion object : SingletonViewModel<UserCourtsScreenViewModel>(){
        fun getInstance() = Companion.getInstance(UserCourtsScreenViewModel::class.java) { UserCourtsScreenViewModel() }
    }

    val courts = mutableStateListOf<Court>()
    val loadingCourts = mutableStateOf(true)

    fun loadCourts(userId: String){
        loadingCourts.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val loadedCourts = FirebaseDBService.getClassInstance().getUsersCourts(userId)
            withContext(Dispatchers.IO){
                if(courts.size > 0)
                    courts.clear()
                courts.addAll(loadedCourts)
                loadingCourts.value = false
            }
        }
    }
}