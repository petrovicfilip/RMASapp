package com.example.aplikacijazasportsketerene.UserInterface.Court

import android.net.Uri
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.DataClasses.Comment
import com.example.aplikacijazasportsketerene.DataClasses.Court
import com.example.aplikacijazasportsketerene.DataClasses.User
import com.example.aplikacijazasportsketerene.Services.DatastoreService
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import com.example.aplikacijazasportsketerene.UserInterface.Home.HomeScreenViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class CourtViewModel(
    court: Court
): ViewModel() {


    //TEMPORARY - REFAKTORISATI, current user klasa!!!
    val user = mutableStateOf<User?>(null)
    fun getUser(uid: String = Firebase.auth.currentUser!!.uid){
        viewModelScope.launch {
            val result = FirebaseDBService.getClassInstance().getUser(Firebase.auth.currentUser!!.uid)

            withContext(Dispatchers.Main){
                user.value = result
            }
        }
    }

    val images = mutableStateListOf<Uri?>()
    val isLoading = mutableStateOf(true)
    val court = mutableStateOf<Court?>(court)

    val courtRating = mutableIntStateOf(court.rating)
    val courtRatedBy = mutableIntStateOf(court.ratedBy)
    val myRating = mutableIntStateOf(0)
    val reviewChecker = mutableStateOf(false)
    val isLiked = mutableStateOf(false)

    val postingReview = mutableStateOf(false)
    val fetchingComments = mutableStateOf(false)
    val fetchingReplies = mutableStateMapOf<Comment,Boolean>()
    val showReplies = mutableStateMapOf<Comment,Boolean>()
    val postingComment = mutableStateOf(false)

    var newComment = mutableStateOf(Comment())
    var newReply = mutableStateOf(Comment())
    val commentsAndReplies = mutableStateMapOf<Comment,List<Comment>?>()

    //!!!!!!!!
    //val showReplies = mutableStateOf(false)

    private val mutexComments = Mutex()
    private val mutexLikes = Mutex()

    /*companion object : SingletonViewModel<CourtViewModel>() {
        fun getInstance(court: Court) = getInstance(CourtViewModel::class.java) { CourtViewModel(court = court ) }
    }*/

    fun getImages(courtId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listOfDownloadedImageUris = DatastoreService.getClassInstance()
                .downloadCourtImages(userId = Firebase.auth.currentUser!!.uid, courtId)
            withContext(Dispatchers.Main) {
                isLoading.value = false
                images.addAll(listOfDownloadedImageUris)
            }
        }
    }

    fun getCommentsForCourt(courtId: String = court.value!!.id.toString()) {
        viewModelScope.launch(Dispatchers.IO) {
            val comments = FirebaseDBService.getClassInstance().getCommentsForCourt(courtId)

            withContext(Dispatchers.IO){
                fetchingComments.value = false

                if(commentsAndReplies.isNotEmpty())
                    commentsAndReplies.clear()

                comments?.forEach { comment ->
                    commentsAndReplies[comment] = emptyList()
                }
            }
        }
    }

    //val commentsAndReplies = mutableStateMapOf<Comment,List<Comment>>()
    fun getRepliesForComment(comment: Comment){
        viewModelScope.launch(Dispatchers.IO) {
            val replies = FirebaseDBService.getClassInstance().getRepliesForComment(comment.id)
            withContext(Dispatchers.Main){
                commentsAndReplies[comment] = replies
                fetchingReplies[comment] = false
            }
        }
    }

    fun addComment(comment: Comment,courtId: String = court.value!!.id.toString(),userId: String = Firebase.auth.currentUser!!.uid){
        viewModelScope.launch(Dispatchers.IO) {
            val commentId = FirebaseDBService.getClassInstance().addComment(Firebase.auth.currentUser!!.uid,courtId,comment)

            withContext(Dispatchers.Main) {
                /*newComment.value.id = commentId
                newComment.value.value = comment.value
                newComment.value.posterUsername = user.value!!.username
                newComment.value.courtId = courtId
                newComment.value.userId = userId*/

                val newComment = Comment(
                    id = commentId,
                    value = comment.value,
                    posterUsername = user.value!!.username,
                    courtId = courtId,
                    userId = userId
                )

                postingComment.value = false
                commentsAndReplies[newComment] = emptyList()
            }
        }

    }

    fun deleteComment(commentId: String){
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseDBService.getClassInstance().deleteComment(commentId)

            withContext(Dispatchers.Main){
                val toRemove = commentsAndReplies.keys.find { it.id == commentId }
                commentsAndReplies.remove(toRemove)
            }
        }
    }

    fun deleteReply(commentId: String, replyId: String){
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseDBService.getClassInstance().deleteReply(commentId,replyId)

            withContext(Dispatchers.Main){
                val comment = commentsAndReplies.keys.find { it.id == commentId }
                if(comment != null){
                    commentsAndReplies[comment] = commentsAndReplies[comment]?.filter {
                        it.id != replyId
                    }
                    fetchingReplies[comment] = false
                }
            }
        }

    }
    fun addReply(comment: Comment,replyText: String,posterId: String = Firebase.auth.currentUser!!.uid){
        viewModelScope.launch(Dispatchers.IO) {
            val replyId = FirebaseDBService.getClassInstance().addReply(Firebase.auth.currentUser!!.uid,comment.id,
                Comment(value = replyText, posterUsername = user.value!!.username, courtId = comment.courtId, userId = posterId))
            withContext(Dispatchers.Main){
                //fetchingReplies.value = false
                //commentsAndReplies[comment] = replies
                val stateComment = commentsAndReplies.keys.find { it.id == comment.id }
                stateComment?.numOfReplies = stateComment?.numOfReplies!! + 1

                val newReply =  Comment(
                    id = replyId,
                    value = replyText,
                    posterUsername = user.value!!.username,
                    userId = posterId,
                    courtId = comment.courtId
                )
                val commentKeyInMap = commentsAndReplies.keys.find { it.id == comment.id }
                val newValues = commentsAndReplies[commentKeyInMap]
                if(commentKeyInMap != null){
                    // hard times call for extreme measures...
                    // UPDATE: radi bolje ovako, samo Bog zna zasto, a i ne interesuje me, ne mogu vise.
                    // ko je pravio mape u kotlin da je pored mene sad da mu zalepim 2-3 vaspitne...
                    // ce okrivimo njega, jer ja nisam naso sta sam pogresio.

                    val filtered = commentsAndReplies.filter { it.key.id != commentKeyInMap.id }
                    commentsAndReplies.clear()
                    commentsAndReplies.putAll(filtered)
                   // commentsAndReplies[commentKeyInMap] = newValues
                    commentsAndReplies[commentKeyInMap] = commentsAndReplies[commentKeyInMap]?.plus(newReply)

                    getRepliesForComment(comment)
                    //fetchingReplies[commentKeyInMap] = false
                    // OVO RAZMOTRITI BLAGOVREMENO
                    showReplies[commentKeyInMap] = true // ovo ne bi trebalo da se dopise ali probavam,
                }

            }
        }

    }

    fun set(){
        courtRating.intValue = court.value!!.rating
        courtRatedBy.intValue = court.value!!.ratedBy
        myRating.intValue = 0
        reviewChecker.value = false
        postingReview.value = false

    }

    fun reset(){
        images.clear()
        isLoading.value = true
        court.value = null

        courtRating.intValue = 0
        courtRatedBy.intValue = 0
        myRating.intValue = 0
    }

    fun addOrUpdateReview(uid: String = Firebase.auth.currentUser!!.uid,
                  cid: String,
                  value: Int){
        viewModelScope.launch(Dispatchers.IO) {

            mutexComments.withLock {
                FirebaseDBService.getClassInstance().addOrUpdateReview(uid, cid, value)

                withContext(Dispatchers.Main){
                    postingReview.value = false
                    if (!reviewChecker.value) {
                        reviewChecker.value = true
                        myRating.intValue = value
                        courtRating.intValue += value
                        courtRatedBy.intValue++
                    }
                    else{
                        courtRating.intValue += value - myRating.intValue
                        myRating.intValue = value
                    }
                    HomeScreenViewModel.getInstance().updateCourt(
                       court = court.value!!.copy(
                           rating = courtRating.intValue,
                           ratedBy = courtRatedBy.intValue,
                           averageRating = (courtRating.intValue.toDouble() / courtRatedBy.intValue.toDouble())
                       )
                    )
                }
            }
        }
    }

    fun deleteReview(uid: String = Firebase.auth.currentUser!!.uid,
                     cid: String){
        viewModelScope.launch(Dispatchers.IO) {
            val review = FirebaseDBService.getClassInstance().getReview(uid,cid)
            if(review != null)
                FirebaseDBService.getClassInstance().removeReview(uid,cid)
            withContext(Dispatchers.Main){
                reviewChecker.value = false

                if(review != null) {
                    courtRating.intValue -= review.value
                    HomeScreenViewModel.getInstance().updateCourt(
                        court = court.value!!.copy(
                            rating = courtRating.intValue,
                            ratedBy = --courtRatedBy.intValue,
                            averageRating = (courtRating.intValue.toDouble() / courtRatedBy.intValue.toDouble())
                                    )
                    )
                }
                myRating.intValue = 0;
                postingReview.value = false
            }

        }
    }

    fun getMyReviewAndUpdateReviewChecker(
        uid: String = Firebase.auth.currentUser!!.uid,
        cid: String){
        viewModelScope.launch(Dispatchers.IO) {
            val retVal = FirebaseDBService.getClassInstance().getReview(uid,cid)

            withContext(Dispatchers.Main){
                if(retVal != null){
                    if(retVal.value != -1){
                        reviewChecker.value = true
                        myRating.intValue = retVal.value
                    }
                }
            }
        }

    }

    suspend fun getUserForComment(userId: String): User? {
        return viewModelScope.async(Dispatchers.IO) {
            FirebaseDBService.getClassInstance().getUser(userId)
        }.await()
    }

    fun likeOrDislikeCourt(userId: String,court: Court,likeOrDislike: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            mutexLikes.withLock {
                if (likeOrDislike) // nema potrebe vracati se na main dispatcher radi ove provere...
                    FirebaseDBService.getClassInstance().likeCourt(userId, court)
                else
                    FirebaseDBService.getClassInstance().dislikeCourt(userId, court)
            }
        }

    }

    fun hasUserLikedCourt(userId: String, courtId: String){
        viewModelScope.launch(Dispatchers.IO){
            isLiked.value = FirebaseDBService.getClassInstance().hasAlreadyLikedCourt(userId, courtId)

        }
    }

    /*fun getCourt(cid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedCourt = FirebaseDBService.getClassInstance().getCourt(cid)
            withContext(Dispatchers.Main) {
                court.value = fetchedCourt
                isLoading.value = false
                fetchedCourt?.let { getImages(it) }
            }
        }
    */
}