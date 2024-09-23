package com.example.aplikacijazasportsketerene.UserInterface.Map

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplikacijazasportsketerene.DataClasses.Court
import com.example.aplikacijazasportsketerene.Location.CurrentUserLocation
import com.example.aplikacijazasportsketerene.Location.PersistedNearbyUsers
import com.example.aplikacijazasportsketerene.Screen
import com.example.aplikacijazasportsketerene.UserInterface.Home.HomeScreenViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.gson.Gson
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@SuppressLint("MutableCollectionMutableState")
@Composable
fun MapDrawer(
    mapViewModel: MapDrawerViewModel = MapDrawerViewModel.getInstance(),
    onNavigateToAddCourt: (LatLng) -> Unit,
    homeScreenViewModel: HomeScreenViewModel = HomeScreenViewModel.getInstance(),
    navController: NavController
){
    homeScreenViewModel.getCourts()
    val locationUpdates = CurrentUserLocation.getClassInstance().location.collectAsState()
    val nearbyUsers by remember { mutableStateOf(PersistedNearbyUsers.persistedUsers) }
    var temporaryMarker by remember { mutableStateOf<LatLng?>(null) }


    val markersState = remember { mutableStateOf<List<Court>>(listOf()) }
    var isCameraUpdated by remember { mutableStateOf(false) }
    //val selectedMarkerForUser = remember { mutableStateOf<Court?>(null) }
    //val showButtonForUsers = remember { mutableStateOf(false) }


    val currentLocation = locationUpdates.value
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(currentLocation?.latitude ?: 0.0, currentLocation?.longitude ?: 0.0), 15f
        )
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            if (!isCameraUpdated && it.latitude != 0.0 && it.longitude != 0.0) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    LatLng(it.latitude, it.longitude), 15f
                )
                isCameraUpdated = true
            }
        }
    }

    Column(
    ){
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(400.dp)
                .clip(RoundedCornerShape(16.dp)),
            cameraPositionState = cameraPositionState,
            onMapLongClick = { latLng ->
                Log.d("MapDrawer", "Long click detected at: $latLng") // Log za debug
                onNavigateToAddCourt(latLng)
                temporaryMarker = latLng
                homeScreenViewModel.showButtonForUsers.value = false
            },
            onMapClick = {
                homeScreenViewModel.showButtonForUsers.value = false
                homeScreenViewModel.selectedUser.value = null
            }
        ) {
            currentLocation?.let { it ->
                Circle(
                    center = LatLng(it.latitude, it.longitude),
                    radius = 50.0,
                    strokeColor = Color(0xFF1E88E5),
                    strokeWidth = 3f,
                    fillColor = Color(0x301E88E5)
                )
                Marker(
                    state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                    title = "Vaša trenutna lokacija"
                )
                if (nearbyUsers.isNotEmpty()) {
                    nearbyUsers.forEach { user ->
                        user?.let {
                            it.latLon?.let { latLon ->
                                Marker(
                                    state = MarkerState(
                                        position = LatLng(
                                            latLon.latitude,
                                            latLon.longitude
                                        )
                                    ),
                                    title = "Korisnik: ${it.username}",
                                    onClick = {
                                        if (it.isInfoWindowShown)
                                            it.showInfoWindow()
                                        homeScreenViewModel.showButtonForUsers.value = false
                                        homeScreenViewModel.selectedUser.value = user
                                        homeScreenViewModel.getSelectedUsersProfilePicture(user.id)
                                        false
                                    }
                                )
                            }
                        }
                    }
                }

                homeScreenViewModel.courts.forEach { court ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                court.latLon.latitude,
                                court.latLon.longitude
                            )
                        ),
                        title = court.name,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
                        onClick = { marker ->
                            homeScreenViewModel.selectedUser.value = null
                            if (marker.isInfoWindowShown)
                                marker.showInfoWindow()
                            homeScreenViewModel.showButtonForUsers.value = false
                            val selectedCourt =
                                homeScreenViewModel.courts.find { it.name == marker.title }
                            homeScreenViewModel.selectedMarkerForUser.value = selectedCourt
                            homeScreenViewModel.showButtonForUsers.value = true
                            false
                        }
                    )
                }
            }
        }
       /* if (homeScreenViewModel.showButtonForUsers.value) {
            Button(
                onClick = {
                    homeScreenViewModel.selectedMarkerForUser.value?.let { court ->
                        val courtJson = Gson().toJson(court)
                        navController.navigate("${Screen.Court.name}/$courtJson")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Pogledaj detalje")
            }
        }*/
    }
}
