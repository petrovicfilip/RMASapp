package com.example.aplikacijazasportsketerene.UserInterface.AddCourt

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplikacijazasportsketerene.SingletonViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class AddCourtViewModel private constructor(): ViewModel() {

    companion object : SingletonViewModel<AddCourtViewModel>(){
        fun getInstance() = Companion.getInstance(AddCourtViewModel::class.java){ AddCourtViewModel() }
    }

    val name = mutableStateOf("")

    val type = mutableStateOf("Fudbalski")

    val description = mutableStateOf("")

    val latitude = mutableStateOf(0f)

    val longitude = mutableStateOf(0f)
    
    val city = mutableStateOf("")
    
    val street = mutableStateOf("")

    val streets = mutableListOf<String>()

    val streetsMenuExpanded = mutableStateOf(false)

    var courtImagesUris = mutableStateListOf<Uri>()

    fun updateName(newName: String) {
        name.value = newName
    }

    fun updateType(newType: String) {
        type.value = newType
    }

    fun updateDescription(newDescription: String) {
        description.value = newDescription
    }

    fun updateCity(newCity: String) {
        city.value = newCity    
    }

    fun updateStreet(newStreet: String) {
        street.value = newStreet
    }

    fun setCoordinates(lat: Float, lon: Float) {
        latitude.value = lat
        longitude.value = lon
    }

    fun updateStreetsMenuExpanded(update: Boolean){
        streetsMenuExpanded.value = update
    }

     fun reverseGeocode(context: Context, onCompleteDecoding: () -> Unit,latitude : Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 5)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        addresses.forEachIndexed { index, address ->
                            if(index == 0) {
                                city.value = address.locality ?: ""
                                street.value = address.thoroughfare ?: ""
                            }
                            if(address.thoroughfare != null && !streets.contains(address.thoroughfare))
                                streets.add(address.thoroughfare)

                            if(index == addresses.size - 1)
                                streets.add("Nepoznato")
                        }
                    }
                }
            withContext(Dispatchers.Main){
                onCompleteDecoding()
            }
        }
    }
}
