package com.example.aplikacijazasportsketerene.Services

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DatastoreService private constructor() {

    companion object{
        var instance: DatastoreService? = null

        fun getClassInstance(): DatastoreService {
            return instance ?: synchronized(this) {
                return instance ?: DatastoreService().also { instance = it }
            }
        }
    }

    val datastore = Firebase.storage.reference

    val imgMetadata = storageMetadata {
        contentType = "image/jpeg"
    }


    suspend fun uploadProfilePicture(userId: String, uri: Uri) {
        val img = datastore.child("users").child(userId).child("profilePicture").child("profilePicture.jpg")

        img.putFile(uri,imgMetadata).await()
    }

    suspend fun downloadProfilePicture(userId: String) : Uri? {
        val img = datastore.child("users").child(userId).child("profilePicture").child("profilePicture.jpg")

        val uri = img.downloadUrl.await()
//            .addOnSuccessListener {
//                ProfileViewModel.getClassInstance().profilePicture = it
//            }
        return uri
    }

    /**
     * COURTS IMAGE CALLS
     **/

    suspend fun uploadCourtImage(uri: Uri,userId: String,courtId: String,imgId: String){

        val img = datastore.child("users").child(userId).child("uploadedCourtsImages").child("court-$courtId").child("$imgId.jpg")

        img.putFile(uri,imgMetadata).await() // moze i bez await, onda nema cekanja u UI, ali slike se uploaduju u pozadini...
                                             // pitati sta je bolje !!!
    }

     suspend fun  downloadCourtImages(userId: String, courtId: String): List<Uri> {
         val img = datastore.child("users").child(userId).child("uploadedCourtsImages").child("court-$courtId")

         val urisOfDownloadedImages = mutableListOf<Uri>()

         img.listAll().await().items.forEach{
             urisOfDownloadedImages.add(it.downloadUrl.await())
         }

         return urisOfDownloadedImages
     }

}