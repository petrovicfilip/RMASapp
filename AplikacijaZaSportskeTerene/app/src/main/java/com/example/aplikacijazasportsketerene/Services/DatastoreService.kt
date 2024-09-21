package com.example.aplikacijazasportsketerene.Services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.ui.text.input.KeyboardType.Companion.Uri
import androidx.core.location.LocationRequestCompat.Quality
import com.google.firebase.Firebase
import com.google.firebase.FirebaseError
import com.google.firebase.FirebaseException
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.storage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URI

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


    suspend fun uploadProfilePicture(userId: String, uri: Uri, context: Context) {
        val img = datastore.child("users").child(userId).child("profilePicture").child("profilePicture.jpg")

        val job1 = img.putFile(uri,imgMetadata)
        val job2 = CoroutineScope(Dispatchers.IO).async{
            uploadCompressedProfilePicture(userId, compressImage(uri, context))
        }

        job1.await()
        job2.join()
    }

    fun compressImage(uri: Uri, context: Context, quality: Int = 50): ByteArray{
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality,outputStream)

        return outputStream.toByteArray()
    }

    suspend fun uploadCompressedProfilePicture(userId: String, byteArray: ByteArray){
        val img = datastore.child("users").child(userId).child("profilePicture").child("compressed_profilePicture.jpg")

        img.putBytes(byteArray).await()
    }

    suspend fun downloadProfilePicture(userId: String, onRetrievalFailuere: () -> Unit) : Uri? {
        val img = datastore.child("users").child(userId).child("profilePicture").child("profilePicture.jpg")

        try{
            val uri = img.downloadUrl.await()
//            .addOnSuccessListener {
//                ProfileViewModel.getClassInstance().profilePicture = it
//            }
            return uri
        }

        catch (e: StorageException) {
            when (e.errorCode) {
                -13010 -> {
                    Log.e("StorageError", "Objekat ne postoji na navedenoj lokaciji: ${e.message}")
                    withContext(Dispatchers.Main) { onRetrievalFailuere()
                    }
                    return null
                }
                else -> {
                    Log.e("StorageError", "Greška prilikom pristupa: ${e.message}")
                    withContext(Dispatchers.Main){
                        onRetrievalFailuere()
                    }
                    return null
                }
            }
        }

        catch (ex: FirebaseException){
            return null
        }
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