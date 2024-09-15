package com.example.aplikacijazasportsketerene.UserInterface.SignUp

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.Services.AccountService
import com.example.aplikacijazasportsketerene.Services.DatastoreService
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import com.example.aplikacijazasportsketerene.UserInterface.Loading.LoadingScreenViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpViewModel private constructor(
    val accountService: AccountService,
    context: Context
) : ViewModel() {

    companion object {
        var instance: SignUpViewModel? = null

        fun getClassInstance(context: Context): SignUpViewModel {
            return instance ?: synchronized(this) {
                return instance ?: SignUpViewModel(accountService = AccountService.getClassInstance(),context).also { instance = it }
            }
        }
    }

    val passwordPattern = "^(?=.*[A-Z])(?=.*[!@#\$%^&*(),.?\":{}|<>]).{10,}$".toRegex()

    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val username = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")
    val phoneNumber = MutableStateFlow("")
    val passwordVisible = MutableStateFlow(false)
    val confirmPasswordVisible = MutableStateFlow(false)
    var profilePicture by mutableStateOf<Uri?>(null)

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

    fun updateUri(newUri: Uri?) {
        profilePicture = newUri
    }

    fun togglePasswordVisibility() {
        passwordVisible.value = !passwordVisible.value
    }

    fun toggleConfirmPasswordVisibility() {
        confirmPasswordVisible.value = !confirmPasswordVisible.value
    }

    // SA RAZLOGOM NIJE LEPO NAPISANO!!!
    suspend fun makeAccount(openAndPopUp: () -> Unit, openUpLoading: () -> Unit): Boolean?{

        openUpLoading()

        val toReturn = withContext(Dispatchers.IO){
            try{
                LoadingScreenViewModel.getInstance().makingAccount.value = true
                accountService.signUp(
                    email.value,
                    password.value,
                    username.value,
                    firstName.value,
                    lastName.value,
                    phoneNumber.value
                )
            }
            catch(exception: FirebaseAuthUserCollisionException){
                return@withContext null
            }
            LoadingScreenViewModel.getInstance().makingAccount.value = false
            if (profilePicture != null) {
                LoadingScreenViewModel.getInstance().uploadingProfilePicture.value = true
                    DatastoreService.getClassInstance()
                        .uploadProfilePicture(Firebase.auth.currentUser!!.uid, profilePicture!!)
                LoadingScreenViewModel.getInstance().uploadingProfilePicture.value = false
                // TODO else { ... }
            }
            accountService.signOut()

            withContext(Dispatchers.Main){
                openAndPopUp()
            }
            return@withContext true
        }
        return toReturn
    }
    fun onSignUpClick(openAndPopUp: () -> Unit, openUpLoading: () -> Unit, popUp: () -> Unit) {

        viewModelScope.launch {
            if (email.value == "" || password.value == "" || confirmPassword.value == "" ||
                username.value == "" || username.value == "" || lastName.value == "" || phoneNumber.value == ""
            )
                Toast.makeText(appContext, "Popunite sva polja!", Toast.LENGTH_SHORT).show()
            else if (!passwordPattern.matches(password.value)) {
                Toast.makeText(
                    appContext,
                    "Šifra mora biti barem 10 karaktera!",
                    Toast.LENGTH_SHORT
                ).show()
                Toast.makeText(
                    appContext,
                    " Takodje, mora sadrzati barem 1 veliko slovo i neki specijalni znak (npr. !,?,* itd)!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.value != confirmPassword.value) {
                Toast.makeText(appContext, "Šifre se ne podudaraju!", Toast.LENGTH_SHORT).show()
            } else if (FirebaseDBService.getClassInstance().getUserWithUsername(username.value))
                Toast.makeText(
                    appContext,
                    "Postoji korisnik sa istim username-om!",
                    Toast.LENGTH_SHORT
                ).show()
            else if (FirebaseDBService.getClassInstance().getUserWithUsername(username.value))
                Toast.makeText(
                    appContext,
                    "Postoji korisnik sa istim username-om!",
                    Toast.LENGTH_SHORT
                ).show()
            else {
               withContext(Dispatchers.Main){
                  val returned =  makeAccount(openAndPopUp,openUpLoading)
                   if(returned == null) {
                       popUp()
                       Toast.makeText(
                           appContext,
                           "Postoji korisnik sa istim mail-om!",
                           Toast.LENGTH_SHORT
                       ).show()
                   }
               }
            }
        }
    }
}
