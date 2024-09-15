package com.example.aplikacijazasportsketerene.UserInterface.UsersLeaderboard

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.DataClasses.User
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import com.example.aplikacijazasportsketerene.SingletonViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class UsersScreenViewModel : ViewModel() {

    val users = mutableStateListOf<User?>(null)
    //val users: StateFlow<List<User>> = _users
    val loadingUsers = mutableStateOf(true)

    private var sortType = SortType.BY_POINTS

    companion object : SingletonViewModel<UsersScreenViewModel>() {
        fun getInstance() = getInstance(UsersScreenViewModel::class.java){
            UsersScreenViewModel()
        }
    }

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch(Dispatchers.IO){

            val sortedUsers = FirebaseDBService.getClassInstance().getUsersSortedByPoints()

            withContext(Dispatchers.Main){
                if(users.size > 0)
                    users.clear()
                users.addAll(sortedUsers)
                loadingUsers.value = false
            }
        }
    }

    fun sortUsers(sortType: SortType = this.sortType) {
        this.sortType = sortType
        val sortedUsers = when (sortType) {
            SortType.BY_POINTS -> users.sortedByDescending { it?.points }
            SortType.BY_USERNAME -> users.sortedBy { it?.username?.lowercase(Locale.getDefault()) }
            SortType.BY_FIRST_NAME -> users.sortedBy { it?.firstName?.lowercase(Locale.getDefault()) }
            SortType.BY_LAST_NAME -> users.sortedBy { it?.lastName?.lowercase(Locale.getDefault()) }
        }
        users.clear()
        users.addAll(sortedUsers)
    }

    enum class SortType {
        BY_POINTS,
        BY_USERNAME,
        BY_FIRST_NAME,
        BY_LAST_NAME
    }
}