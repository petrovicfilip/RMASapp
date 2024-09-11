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
    val postingComment = mutableStateOf(false)

    var newComment = mutableStateOf<Comment>(Comment())
    val commentsAndReplies = mutableStateMapOf<Comment,List<Comment>?>()

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
                fetchingReplies[comment] = false
                commentsAndReplies[comment] = replies
            }
        }
    }

    fun addComment(comment: Comment,courtId: String = court.value!!.id.toString()){
        viewModelScope.launch(Dispatchers.IO) {
            val commentId = FirebaseDBService.getClassInstance().addComment(Firebase.auth.currentUser!!.uid,courtId,comment)

            withContext(Dispatchers.Main){
                newComment.value.id = commentId
                newComment.value.value = comment.value

                postingComment.value = false
                commentsAndReplies[newComment.value] = emptyList()
            }
        }

    }

    fun addReply(comment: Comment,replyText: String){
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseDBService.getClassInstance().addReply(Firebase.auth.currentUser!!.uid,comment.id,
                Comment(value = replyText))
            withContext(Dispatchers.Main){
                //fetchingReplies.value = false
                //commentsAndReplies[comment] = replies
                val stateComment = commentsAndReplies.keys.find { it == comment }
                stateComment?.numOfReplies = stateComment?.numOfReplies!! + 1
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
                           ratedBy = courtRatedBy.intValue
                       )
                    )
                }
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

    fun likeOrDislikeCourt(userId: String,courtId: String){
        viewModelScope.launch(Dispatchers.IO) {
            mutexLikes.withLock {
                if (isLiked.value) // nema potrebe vracati se na main dispatcher radi ove provere...
                    FirebaseDBService.getClassInstance().likeCourt(userId, courtId)
                else
                    FirebaseDBService.getClassInstance().dislikeCourt(userId, courtId)
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