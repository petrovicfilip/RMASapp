package com.example.aplikacijazasportsketerene.Services

import android.net.Uri
import android.util.Log
import com.example.aplikacijazasportsketerene.DataClasses.Comment
import com.example.aplikacijazasportsketerene.DataClasses.Court
import com.example.aplikacijazasportsketerene.DataClasses.Review
import com.example.aplikacijazasportsketerene.DataClasses.User
import com.example.aplikacijazasportsketerene.DataClasses.calculateBoundingBox
import com.example.aplikacijazasportsketerene.DataClasses.haversine
import com.example.aplikacijazasportsketerene.Location.CurrentUserLocation
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseDBService private constructor() {

    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    private val users = Firebase.firestore.collection("users")
    private val courts = Firebase.firestore.collection("courts")
    private val reviews = Firebase.firestore.collection("reviews")
    private val comments = Firebase.firestore.collection("comments")
    private val likes = Firebase.firestore.collection("likes")
    private val foundCourts = Firebase.firestore.collection("foundCourts")

    //poeni za razlicite aktivnosti
    val courtPosted = 1.0
    val foundCourtPoints = 2.0
    val courtLikedByAnotherUser = 3.0

    private val scope = CoroutineScope(Dispatchers.IO)

    private val mutexForLikes = Mutex()

    companion object {
        var instance: FirebaseDBService? = null

        fun getClassInstance(): FirebaseDBService {
            return instance ?: synchronized(this) {
                return instance ?: FirebaseDBService().also { instance = it }
            }
        }
    }

    /**
    USERS DB CALLS
     **/

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
            }
            else {
                users.document(user.id).set(user)
                    .addOnSuccessListener {
                        Log.d("USER_ADD", "Korisnik sa id-jem: ${user.id} je kreiran!")
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "USER_ADD",
                            "Greska pri kreiranju korisnika sa id-jem: ${user.id}!",
                            e
                        )
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


     suspend fun getUserWithUsername(username: String): Boolean = coroutineScope {
        val job = async(Dispatchers.IO) { findUserWithUsername(username) }
        return@coroutineScope job.await().isNotEmpty()
    }

     /*suspend fun getUserForComment(commentId: String): User {
        val job = async(Dispatchers.IO) { findUserWithUsername(username) }
        return@coroutineScope job.await().isNotEmpty()
    }*/

    suspend fun getUser(uid: String): User? {
        try{
            val user = users
                .document(uid)
                .get()
                .await()

            return user.toObject<User>()
        }
        catch (e: Exception){
            Log.d("FAILED_TO_UPDATE_USER_LOCATION", "Location update failed")
            return null
        }
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
    fun updateUserLocation(uid: String, location: GeoPoint) {
        users.document(uid).update("latLon", location). //await()
        addOnSuccessListener {
            Log.d("UPDATED_USER_LOCATION", "Successfully updated user location.")
        }.addOnFailureListener() {
            Log.d("FAILED_TO_UPDATE_USER_LOCATION", "Location update failed:\n$it")
        }.addOnCanceledListener {
            Log.d("UPDATE_USER_LOCATION_CANCELED", "Location update canceled.")
        }
    }

    suspend fun findNearbyUsers(
        latitude: Double,
        longitude: Double,
        onComplete: (List<User>) -> Unit,
    ) {

        val usersCollection = firestore.collection("users")

        val docs = usersCollection.get().await()
        val nearbyUsers = mutableListOf<User>()

        val usersList = docs.mapNotNull { it.toObject<User>() }

        for (user in usersList) {
            nearbyUsers.add(user)
            Log.d("LOKEJSN", "${user.latLon?.latitude},${user.latLon?.longitude}")
        }
        // PROVERITI!!!!!!!!!!!!
        //CoroutineScope(Dispatchers.Main).launch {
            onComplete(nearbyUsers)
        //}

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

    suspend fun getUsersSortedByPoints(): List<User> {
        try{
            val returnedDocs = users
                .orderBy("points",Query.Direction.DESCENDING)
                .get()
                .await()

            return returnedDocs.mapNotNull { it.toObject<User>() }

        }
        catch(e: Exception){
            return emptyList<User>()
        }
    }


    /**
     * COURTS DB CALLS
     **/

    suspend fun addCourt(court: Court, listOfUris: List<Uri>, onUploadFinished: () -> Unit) {
        val addCourt = courts.add(court)
        val ref = addCourt.await()

        scope.launch {
            courts.document(ref.id).update("id", ref.id).await()
            users
                .document(auth.currentUser!!.uid)
                .update( "points",FieldValue.increment(courtPosted))
                .await()
        }

        val jobs = listOfUris.mapIndexed { index, uri ->

            scope.async {
                DatastoreService.getClassInstance()
                    .uploadCourtImage(uri, court.userId!!, ref.id, (index + 1).toString())
            }
        }

        jobs.awaitAll()

        withContext(Dispatchers.Main) {
            onUploadFinished()
        }
    }

    suspend fun getAllCourts(): List<Court> {

        val foundCourts = scope.async(Dispatchers.IO) {
            courts.get().await()
        }.await()

        val toReturn = foundCourts.documents.mapNotNull { it.toObject<Court>() }

        return toReturn
    }

    suspend fun getCourt(cid: String): Court?{
        val foundCourt = scope.async(Dispatchers.IO) {
            courts
                .document(cid)
                .get()
                .await()
        }.await()

        val toReturn = foundCourt.toObject<Court>()

        return toReturn
    }

    suspend fun findNearbyCourts(
        latitude: Double,
        longitude: Double,
        onComplete: (List<Court>) -> Unit,
    ) {

        val boundingBox = calculateBoundingBox(latitude, longitude, 50.0)

        val documents = courts
            .whereGreaterThanOrEqualTo("latLon", GeoPoint(boundingBox.minLat, boundingBox.minLon))
            .whereLessThanOrEqualTo("latLon", GeoPoint(boundingBox.maxLat, boundingBox.maxLon))
            .get()
            .await()

        val nearbyCourts = mutableListOf<Court>()

        for (document in documents) {
            val geoPoint = document.getGeoPoint("latLon") ?: continue
            val courtLat = geoPoint.latitude
            val courtLon = geoPoint.longitude
            val ime = document.getString("name")

            Log.d("DISTANCAaaaaaaaaaa","MOja lokacija: ${latitude}, ${longitude}!!!" + " ${courtLon},${courtLat}" + " $ime")
            val distance = haversine(latitude, longitude, courtLat, courtLon)
            Log.d("DISTANCA","DISTANCA: ${distance} metara!!!" + " ${courtLon},${courtLat}" + " $ime")

            if (distance <= 50) {
                val court = document.toObject(Court::class.java)
                nearbyCourts.add(court)
            }
        }

        val filteredCourts = filterCourts(nearbyCourts)

        if(filteredCourts.isNotEmpty())
            addVisitedCourtsAndPoints(filteredCourts)

        onComplete(filteredCourts)
    }


    suspend fun filterCourts(nearbyCourts: List<Court>,userId: String = auth.currentUser!!.uid): List<Court>{
        val userFoundCourts = firestore.collection("foundCourts").document(userId).collection("courts")

        val foundCourtsSnapshot = userFoundCourts.get().await()

        val foundCourtIds = foundCourtsSnapshot.documents.map { it.id }.toSet()

        return nearbyCourts.filter { court -> !foundCourtIds.contains(court.id) }
    }

    suspend fun addVisitedCourtsAndPoints(nearbyCourts: List<Court>, userId: String = auth.currentUser!!.uid){
        val userFoundCourts = foundCourts.document(userId).collection("courts")
        val batch = firestore.batch()

        nearbyCourts.forEach { court ->
            val courtRef = userFoundCourts.document(court.id!!)
            batch.set(courtRef, court)
        }

        val points = nearbyCourts.size.toLong()
        val userRef = users.document(userId)
        batch.update(userRef,"points",FieldValue.increment(points))

        try {
            batch.commit().await()
        } catch (e: Exception) {
            Log.d("GRESKA_PRI_DODAVAJU_POSECENIH_TERENA","Greska pri dodavanju posecenih terena: ${e.message}")
        }
    }

    /**
     * REVIEWS DB CALLS
     **/


    suspend fun addOrUpdateReview(userId: String, courtId: String, value: Int): Boolean {

        try {
            val reviewRef = reviews
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", courtId)
                .get()
                .await()

            if (reviewRef.isEmpty) {
                val newReview = Review(
                    userId = userId,
                    eventId = courtId,
                    value = value
                )
                reviews.add(newReview).await()

                 courts
                    .document(courtId)
                    .update("ratedBy",FieldValue.increment(1),
                        "rating",FieldValue.increment(value.toLong()))
                     .await()
                // ipak mi treba polje averageRating...zbog filtera, ne moze drugacije barem da ja znam...
                val court = courts
                    .document(courtId)
                    .get()
                    .await()

                val rating = court.get("rating").toString().toDouble()
                val ratedBy = court.get("ratedBy").toString().toDouble()

                courts
                    .document(courtId)
                    .update("averageRating",rating / ratedBy)
                    .await()

                return true
            }
            else {
                val reviewDocument = reviewRef.documents[0]
                reviews
                    .document(reviewDocument.id)
                    .update("value", value, "timestamp",Timestamp.now())
                    .await()

                courts
                    .document(courtId)
                    .update("rating",value)

                val court = courts
                    .document(courtId)
                    .get()
                    .await()

                val rating = court.get("rating").toString().toDouble()
                val ratedBy = court.get("ratedBy").toString().toDouble()

                courts
                    .document(courtId)
                    .update("averageRating",rating / ratedBy)
                    .await()

                return true
            }
        }
        catch (e: Exception) {
            println("Greska pri dodavanju/azuriranju rivjua!?!?: ${e.message}")
            return false
        }
    }

    suspend fun removeReview(userId: String, courtId: String): Boolean {
        try {
            val reviewRef = reviews
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", courtId)
                .get()
                .await()

            if (!reviewRef.isEmpty) {
                val valueToDecrement = reviewRef.documents[0].get("value")

                courts
                    .document(courtId)
                    .update("ratedBy",FieldValue.increment(-1),
                        "rating",FieldValue.increment(valueToDecrement.toString().toLong()))

                val reviewDocument = reviewRef.documents[0]
                reviews.document(reviewDocument.id).delete().await()
            }
            return true
        }
        catch (e: Exception) {
            println("Greska pri brisanju rivjua!?!?: ${e.message}")
            return false
        }
    }

    suspend fun getReview(userId: String, eventId: String): Review? {

        try {
            val reviewRef = reviews
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", eventId)
                .get()
                .await()

            return if(!reviewRef.isEmpty) {
                reviewRef.documents[0].toObject<Review>()
            }
            else {
                Review(
                    value = -1
                )
            }
        }
        catch (e: Exception) {
            println("Greska pri fecovanju rivjua!?!?: ${e.message}")
            return null
        }
    }

    /**
     * COMMENTS & REPLIES DB CALLS
     **/

    suspend fun addComment(userId: String,courtId: String,comment: Comment): String{
        try {
            val ref = comments.add(comment).await()
            ref.update("id",ref.id).await()

            return ref.id
        }
        catch (e: Exception) {
            println("Greska pri dodavanju komentara!?!?: ${e.message}")
            return ""
        }
    }

    suspend fun getCommentsForCourt(courtId: String): List<Comment>?{
        try{
            val comments = comments.
                whereEqualTo("courtId",courtId)
                .get()
                .await()
            return comments.documents.mapNotNull { it.toObject<Comment>() }
        }
        catch (e: Exception){
            println("Greska pri fecovanju komentara!?!?: ${e.message}")
            return null
        }
    }

    suspend fun deleteComment(commentId: String) {
        try {
            val replies = comments
                .document(commentId)
                .collection("replies")
                .get()
                .await()

            for (reply in replies.documents) {
                comments
                    .document(commentId)
                    .collection("replies")
                    .document(reply.id)
                    .delete()
                    .await()
            }

            comments.document(commentId).delete().await()

            Log.d("Firestore", "Komentar i svi odgovori uspešno obrisani: $commentId")
        } catch (e: Exception) {
            Log.e("Firestore", "Greška pri brisanju komentara: ${e.message}", e)
        }
    }

    suspend fun addReply(userId: String,commentId: String,reply: Comment): String{
        try{
            val newReply = comments
                .document(commentId)
                .collection("replies")
                .add(reply)
                .await()

            newReply
                .update("id",newReply.id)
                .await()

            comments
                .document(commentId)
                .update("numOfReplies",FieldValue.increment(1))
            return newReply.id
        }
        catch (e: Exception){
            println("Greska pri dodavanju odgovora!?!?: ${e.message}")
            return ""
        }
    }

    suspend fun getRepliesForComment(commentId: String): List<Comment>?{
        try{
            val replies = comments.
                document(commentId)
                .collection("replies")
                .get()
                .await()

            return replies.documents.mapNotNull { it.toObject<Comment>() }
            }
        catch (e: Exception){
            println("Greska pri dodavanju odgovora!?!?: ${e.message}")
            return null
        }
    }

    suspend fun deleteReply(commentId: String, replyId: String) {
        try {
            comments
                .document(commentId)
                .collection("replies")
                .document(replyId)
                .delete()
                .await()
            Log.d("Firestore", "Odgovor uspešno obrisan: $replyId")
        } catch (e: Exception) {
            Log.e("Firestore", "Greška pri brisanju odgovora: ${e.message}", e)
        }
    }

    /**
     * LIKES DB CALLS
     **/
    suspend fun likeCourt(userId: String,court: Court): Boolean{
        try {
            val likeData = hashMapOf(
                "userId" to userId,
                "courtId" to court.id
            )

            likes.document("${userId}-${court.id}").set(likeData).await()

            firestore.runTransaction { transaction ->
                val userRef = users.document(court.userId!!)
                val snapshot = transaction.get(userRef)
                val currentPoints = snapshot.getLong("points") ?: 0L
                transaction.update(userRef, "points", currentPoints + courtLikedByAnotherUser)
            }.await()

            return true
        }
        catch (e: Exception) {
            Log.d("LIKE_COURT","Greska pri like-u!?!?: ${e.message}")
            return false
        }
    }
    suspend fun dislikeCourt(userId: String, court: Court): Boolean{
        try {
            firestore.collection("likes").document("$userId-${court.id}")
                .delete()
                .await()
            firestore.runTransaction { transaction ->
                val userRef = users.document(court.userId!!)
                val snapshot = transaction.get(userRef)
                val currentPoints = snapshot.getLong("points") ?: 0L
                transaction.update(userRef, "points", currentPoints - courtLikedByAnotherUser)
            }.await()

            return true
        }
        catch (e: Exception) {
            Log.d("DISLIKE_COURT","Greska pri dislike-u!?!?: ${e.message}")
            return false
        }
    }

    suspend fun hasAlreadyLikedCourt(userId: String, courtId: String): Boolean{
        try {
            val queryResult = Firebase.firestore.collection("likes")
                .whereEqualTo("userId",userId)
                .whereEqualTo("courtId",courtId)
                .get()
                .await()

            return !queryResult.isEmpty
        }
        catch (e: Exception) {
            Log.d("HAS_LIKED_COURT","Greska: ${e.message}")
            return false
        }
    }

    /**
     * FIlTER & SEARCH DB CALLS
     **/

    // search type: naziv,grad,ulica,korisnik,radius
    // filteri: tip terena,datum postavljanja,minimalna ocena

    suspend fun searchForCourts(
        name: String = "",
        city: String = "",
        street: String = "",
        radius: Int = 0,

        types: List<String?>,
        dateBeginning: Timestamp? = null,
        dateEnd: Timestamp? = null,
        minimumRating: Int = 0
    ): List<Court> {
        var query: Query = courts

        if(name != "") {
            query = courts.whereEqualTo("name",name)
        }
        else if(city != "") {
            query = courts.whereEqualTo("city",city)
        }
        else if(street != "") {
            query = courts.whereEqualTo("street",street)
        }
        else if(radius > 0) {
            val boundingBox = calculateBoundingBox(CurrentUserLocation.getClassInstance().location.value!!.latitude,
                CurrentUserLocation.getClassInstance().location.value!!.latitude,radius.toDouble())

            query = query
                .whereGreaterThanOrEqualTo("latLon", GeoPoint(boundingBox.minLat, boundingBox.minLon))
                .whereLessThanOrEqualTo("latLon", GeoPoint(boundingBox.maxLat, boundingBox.maxLon))
        }

        if(types.isNotEmpty()) {
            val limitedTypes = if (types.size > 10) types.take(10) else types
            query = query.whereIn("type", limitedTypes)
        }

        if(dateBeginning != null && dateEnd != null) {
            query = query.whereGreaterThan("date", dateBeginning)
            query = query.whereLessThan("date", dateEnd)
        }

        if(minimumRating > 0)
            query = query.whereGreaterThanOrEqualTo("averageRating",minimumRating)

        val results = query.get().await()

        return results.mapNotNull { it.toObject<Court>() }
    }

}