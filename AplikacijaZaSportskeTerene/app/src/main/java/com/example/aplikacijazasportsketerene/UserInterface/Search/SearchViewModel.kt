package com.example.aplikacijazasportsketerene.UserInterface.Search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.DataClasses.Court
import com.example.aplikacijazasportsketerene.Services.FirebaseDBService
import com.example.aplikacijazasportsketerene.SingletonViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel private constructor(): ViewModel() {

    companion object : SingletonViewModel<SearchViewModel>(){
        fun getInstance() = getInstance(SearchViewModel::class.java) { SearchViewModel() }
    }
    var searchType by mutableStateOf(SearchTipovi.Ime)
    var searchInput by mutableStateOf("")

    var selectedTypes by mutableStateOf(listOf<String?>())
    var dateBeginning by mutableStateOf<Timestamp?>(null)
    var dateEnd by mutableStateOf<Timestamp?>(null)
    var minimumRating by mutableIntStateOf(0)
    var searchResults by mutableStateOf(listOf<Court>())

    fun searchCourts() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){
                searchResults = FirebaseDBService.getClassInstance().searchForCourts(
                    name = if (searchType == SearchTipovi.Ime) searchInput else "",
                    city = if (searchType == SearchTipovi.Grad) searchInput else "",
                    street = if (searchType == SearchTipovi.Ulica) searchInput else "",
                    radius = if (searchType == SearchTipovi.Radius) searchInput.toIntOrNull()
                        ?: 0 else 0,
                    types = selectedTypes,
                    dateBeginning = dateBeginning,
                    dateEnd = dateEnd,
                    minimumRating = minimumRating
                )
            }
        }
    }

    fun checkIfNoFiltersAreApplied(): Boolean{
      return selectedTypes.isEmpty() && dateBeginning == null && dateEnd == null && minimumRating == 0
    }
}

enum class SearchTipovi {
    Ime, Grad, Ulica, Radius
}