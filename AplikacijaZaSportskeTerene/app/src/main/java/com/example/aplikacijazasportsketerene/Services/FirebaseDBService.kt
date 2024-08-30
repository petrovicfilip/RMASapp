package com.example.aplikacijazasportsketerene.Services

import android.location.Location
import android.util.Log
import com.example.aplikacijazasportsketerene.DataClasses.User
import com.example.aplikacijazasportsketerene.DataClasses.calculateBoundingBox
import com.example.aplikacijazasportsketerene.DataClasses.haversine
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseDBService {

    private val firestore = Firebase.firestore

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
    suspend fun getUserLocation(uid: String): GeoPoint? {
        return withContext(Dispatchers.IO) {
            val user = users.document(uid).get().await().toObject<User>()
            user?.latLon
        }
    }

    //alternative... vrati deffered, pa ga sacekamo, bolje nego CoroutineScope(...).launch...
    /*suspend fun getUserLocation(uid: String): GeoPoint? {
        val userDeferred = CoroutineScope(Dispatchers.IO).async {
            users.document(uid).get().await().toObject<User>()
        }
        val user = userDeferred.await()
        return user?.latLon
    }*/
     fun updateUserLocation(uid: String,location: GeoPoint) {
        users.document(uid).update("latLon",location). //await()
        addOnSuccessListener {
            Log.d("UPDATED_USER_LOCATION","Successfully updated user location.")
        }.
        addOnFailureListener(){
            Log.d("FAILED_TO_UPDATE_USER_LOCATION", "Location update failed:\n$it")
        }.
        addOnCanceledListener {
            Log.d("UPDATE_USER_LOCATION_CANCELED", "Location update canceled.")
        }
    }
    suspend fun findNearbyUsers(latitude: Double, longitude: Double, onComplete: (List<User>) -> Unit) {

        val usersCollection = firestore.collection("users")

        val docs = usersCollection.get().await()
        val nearbyUsers = mutableListOf<User>()

        val usersList = docs.mapNotNull { it.toObject<User>() }

        for(user in usersList) {
            nearbyUsers.add(user)
            Log.d("LOKEJSN","${user.latLon!!.latitude},${user.latLon!!.longitude}")
        }
        onComplete(nearbyUsers)
//        val boundingBox = calculateBoundingBox(latitude, longitude, 50.0)
//
//        usersCollection
//            .whereGreaterThanOrEqualTo("latitude", boundingBox.minLat)
//            .whereLessThanOrEqualTo("latitude", boundingBox.maxLat)
//            .whereGreaterThanOrEqualTo("longitude", boundingBox.minLon)
//            .whereLessThanOrEqualTo("longitude", boundingBox.maxLon)
//            .get()
//            .addOnSuccessListener { documents ->
//                val nearbyUsers = mutableListOf<User>()
//
//                for (document in documents) {
//                    val userLat = document.getDouble("latitude") ?: continue
//                    val userLon = document.getDouble("longitude") ?: continue
//
//                    val distance = haversine(latitude, longitude, userLat, userLon)
//
//                    if (distance <= 10000000) {
//                        val user = document.toObject(User::class.java)
//                        nearbyUsers.add(user)
//                    }
//                }
//
//                onComplete(nearbyUsers)
//            }
//            .addOnFailureListener { exception ->
//                // Handle the error here
//                onComplete(emptyList())
//            }
    }

}