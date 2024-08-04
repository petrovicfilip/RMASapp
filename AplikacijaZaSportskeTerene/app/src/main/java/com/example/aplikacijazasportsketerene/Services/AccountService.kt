package com.example.aplikacijazasportsketerene.Services

import com.example.aplikacijazasportsketerene.DataClasses.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class AccountService {

    private val firebaseDB = FirebaseDBService()

    val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun isVerified(): Boolean {
        val current = Firebase.auth.currentUser
        if(current != null)
            return current.isEmailVerified
        return false
    }

    suspend fun signIn(email: String, password: String):Boolean {
        try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            return true
        }
        catch (error: Exception){
            return false
        }
    }

    suspend fun signUp(email: String, password: String, username: String, firstName: String, lastName: String, phoneNumber: String) {
        val userCredential = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        signIn(email, password)//...
        val user = userCredential.user
        user?.sendEmailVerification()?.await()

        firebaseDB.addUser(
            User(
                id = Firebase.auth.currentUser!!.uid,
                email = email,
                username = username,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber
            )
        )
        signOut() //...
    }

    private fun signOut() {
        Firebase.auth.signOut()
    }

    /*suspend fun deleteAccount() {
        Firebase.auth.currentUser!!.delete().await()
    }*/
}