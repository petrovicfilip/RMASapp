package com.example.aplikacijazasportsketerene.Services

import android.util.Log
import com.example.aplikacijazasportsketerene.DataClasses.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseDBService {

    private val users = Firebase.firestore.collection("users")

//    suspend fun addUser(user: User) {
//
//        runBlocking {
//            var success: Boolean = false
//            if (user.id == null) // mozda i izbaciti, to be decided
//                users.add(user)
//                    .addOnSuccessListener {
//                        Log.d("USER_DB_ADD", "User document created successfully!")
//                        success = true
//                    }
//                    .addOnFailureListener { e ->
//                        Log.w(
//                            "USER_DB_ADD",
//                            "Error creating user document!",
//                            e
//                        )
//                    }
//            else {
//                users.document(user.id).set(user)
//                    .addOnSuccessListener {
//                        Log.d("USER_DB_ADD", "User document: ${user.id} created successfully!")
//                    }
//                    .addOnFailureListener { e ->
//                        Log.w(
//                            "USER_DB_ADD",
//                            "Error creating user document: ${user.id}!",
//                            e
//                        )
//                    }
//            }
//        }
//    }
    // validno kroz korutine
    suspend fun addUser(user: User) {
        return suspendCancellableCoroutine { continuation ->
            if (user.id == null)/* to be decided */ {
                users.add(user)
                    .addOnSuccessListener {
                        Log.d("USER_ADD", "Korisnik kreiran uspesno!")
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { e ->
                        Log.w("USER_ADD", "Greska pri kreiranju korisnika!", e)
                        continuation.resumeWithException(e)
                    }
            } else {
                users.document(user.id).set(user)
                    .addOnSuccessListener {
                        Log.d("USER_ADD", "Korisnik sa id-jem: ${user.id} je kreiran!")
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { e ->
                        Log.w("USER_ADD", "Greska pri kreiranju korisnika sa id-jem: ${user.id}!", e)
                        continuation.resumeWithException(e)
                    }
            }
        }
    }

//    suspend fun getUserWithUsername(value: String): User? {
//        val querySnapshot = users
//            .whereEqualTo("username", value)
//            .get()
//            .await()
//
//        return querySnapshot.documents.first().toObject<User>()
//    }

   /* fun getUserWithUsername(username: String): Boolean {
        var u: List<User>? = null
        runBlocking {
            u = async { findUserWithUsername(username) }.await()
        }
        return u!!.isNotEmpty()
    }*/

    suspend fun getUserWithUsername(username: String): Boolean = coroutineScope {
        val job = async(Dispatchers.IO) { findUserWithUsername(username) }
        return@coroutineScope job.await().isNotEmpty()
    }

    suspend fun findUserWithUsername(username: String): List<User> {
        val querySnapshot = users
            .whereEqualTo("username", username)
            .get()
            .await()
        return querySnapshot.documents.mapNotNull { it.toObject<User>() }
    }
}