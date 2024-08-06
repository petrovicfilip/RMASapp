package com.example.aplikacijazasportsketerene.UserInterface.signup

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.Services.AccountService
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel private constructor(
    val accountService: AccountService,
    context: Context
) : ViewModel() {

    companion object {
        var instance: SignUpViewModel? = null

        fun getClassInstance(context: Context): SignUpViewModel {
            return instance ?: synchronized(this) {
                return instance ?: SignUpViewModel(accountService = AccountService(),context).also { instance = it }
            }
        }
    }

    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val username = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")
    val phoneNumber = MutableStateFlow("")
    val passwordVisible = MutableStateFlow(false)
    val confirmPasswordVisible = MutableStateFlow(false)
    private val appContext = context

    fun updateFirstName(newFirstName: String) {
        firstName.value = newFirstName
    }

    fun updateLastName(newLastName: String) {
        lastName.value = newLastName
    }

    fun updateUsername(newUsername: String) {
        username.value = newUsername
    }

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
    }

    fun updatePhoneNumber(newPhoneNumber: String) {
        phoneNumber.value = newPhoneNumber
    }

    fun togglePasswordVisibility() {
        passwordVisible.value = !passwordVisible.value
    }

    fun toggleConfirmPasswordVisibility() {
        confirmPasswordVisible.value = !confirmPasswordVisible.value
    }

    fun onSignUpClick(openAndPopUp: () -> Unit) {

        viewModelScope.launch {
            if (email.value == "" || password.value == "" || confirmPassword.value == "" ||
                username.value == "" || username.value == "" || lastName.value == "" || phoneNumber.value == ""
            )
                Toast.makeText(appContext, "Popunite sva polja!", Toast.LENGTH_SHORT).show()
            else if (password.value.length < 8) {
                Toast.makeText(
                    appContext,
                    "Šifra mora biti duza od 8 karaktera!",
                    Toast.LENGTH_LONG
                ).show()
            } else if (password.value != confirmPassword.value) {
                Toast.makeText(appContext, "Šifre se ne podudaraju!", Toast.LENGTH_SHORT).show()
            } else if (FirebaseDBService().getUserWithUsername(username.value))
                Toast.makeText(
                    appContext,
                    "Postoji korisnik sa istim username-om!",
                    Toast.LENGTH_SHORT
                ).show()
            else {
                accountService.signUp(
                    email.value,
                    password.value,
                    username.value,
                    firstName.value,
                    lastName.value,
                    phoneNumber.value
                )
                Toast.makeText(
                    appContext,
                    "Poslat je link za verifikaciju na vas mail!",
                    Toast.LENGTH_SHORT
                ).show()
                openAndPopUp()
            }
        }
    }
}
