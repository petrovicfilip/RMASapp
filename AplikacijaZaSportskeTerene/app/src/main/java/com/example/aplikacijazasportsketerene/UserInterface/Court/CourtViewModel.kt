package com.example.aplikacijazasportsketerene.UserInterface.Court

import android.net.Uri
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.DataClasses.Court
import com.example.aplikacijazasportsketerene.Services.DatastoreService
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import com.example.aplikacijazasportsketerene.UserInterface.Home.HomeScreenViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class CourtViewModel(
    court: Court
): ViewModel() {


    val images = mutableStateListOf<Uri?>()
    val isLoading = mutableStateOf(true)
    val court = mutableStateOf<Court?>(court)

    val courtRating = mutableIntStateOf(court.rating)
    val courtRatedBy = mutableIntStateOf(court.ratedBy)
    val myRating = mutableIntStateOf(0)
    val reviewChecker = mutableStateOf(false)
    val postingReview = mutableStateOf(false)

    private val mutex = Mutex()

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

            mutex.withLock {
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