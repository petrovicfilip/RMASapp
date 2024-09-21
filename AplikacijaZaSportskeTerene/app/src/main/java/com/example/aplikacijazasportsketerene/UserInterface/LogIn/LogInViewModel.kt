package com.example.aplikacijazasportsketerene.UserInterface.LogIn

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.Services.AccountService
import com.example.aplikacijazasportsketerene.SingletonViewModel
import com.example.aplikacijazasportsketerene.UserInterface.Loading.LoadingScreenViewModel
import com.example.aplikacijazasportsketerene.UserInterface.SignUp.SignUpViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogInViewModel private constructor(
    // ...
    private val accountService: AccountService,
    context: Context
) : ViewModel() {

    /*companion object{
        var instance : LogInViewModel? = null

        fun getClassInstance(context: Context) : LogInViewModel {

            return instance ?: synchronized(this) {
                return instance ?: LogInViewModel(accountService = AccountService.getClassInstance(), context).also { instance = it }
            }
        }
    }*/

    companion object : SingletonViewModel<LogInViewModel>() {
        fun getInstance(context: Context) : LogInViewModel = getInstance(LogInViewModel::class.java) {
            LogInViewModel(accountService = AccountService.getClassInstance(), context)
        }
    }

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val passwordVisible = MutableStateFlow(false)
    val appContext = context

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun togglePasswordVisibility() {
        passwordVisible.value = !passwordVisible.value
    }

    fun onSignInClick(openAndPopUp : () -> Unit, loading : () -> Unit, popBack : () -> Unit) {
        LoadingScreenViewModel.getInstance().loggingIn.value = true
        loading()
        viewModelScope.launch {
            if(accountService.signIn(email.value, password.value)) {
                if (Firebase.auth.currentUser!!.isEmailVerified) {
                    LoadingScreenViewModel.getInstance().loggingIn.value = false
                    Toast.makeText(appContext, "Ulogovani ste!", Toast.LENGTH_SHORT).show()

                    withContext(Dispatchers.Main){
                        openAndPopUp()
                    }
                } else {
                    withContext(Dispatchers.Main){
                        popBack()
                    }
                    Toast.makeText(
                        appContext,
                        "Potvrdite vas email klikom na link poslat na isti!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else {
                withContext(Dispatchers.Main){
                    popBack()
                }
                if(email.value == "" && password.value == "")
                    Toast.makeText(appContext, "Unesite e-mail i sifru!", Toast.LENGTH_SHORT).show()
                else if(email.value == "")
                    Toast.makeText(appContext, "Unesite e-mail!", Toast.LENGTH_SHORT).show()
                else if (password.value == "")
                    Toast.makeText(appContext, "Unesite sifru!", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(appContext, "Pogresni podaci za prijavu!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}