package com.example.aplikacijazasportsketerene.UserInterface.all

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.Services.AccountService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LogInViewModel private constructor(
    // ...
    private val accountService: AccountService,
    context: Context
) : ViewModel() {

    companion object{
        var instance : LogInViewModel? = null

        fun getClassInstance(context: Context) : LogInViewModel {

            return instance ?: synchronized(this) {
                return instance ?: LogInViewModel(accountService = AccountService.getClassInstance(), context).also { instance = it }
            }
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

    fun OnSignUpClick(openAndPopUp : () -> Unit) {
        viewModelScope.launch {
            if(accountService.signIn(email.value, password.value)) {
                if (Firebase.auth.currentUser!!.isEmailVerified) {
                    Toast.makeText(appContext, "Ulogovani ste!", Toast.LENGTH_SHORT).show()
                    openAndPopUp()
                } else Toast.makeText(
                    appContext,
                    "Potvrdite vas email klikom na link poslat na isti!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
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