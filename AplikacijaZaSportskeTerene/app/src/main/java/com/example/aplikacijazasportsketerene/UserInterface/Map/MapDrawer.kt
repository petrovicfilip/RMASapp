package com.example.aplikacijazasportsketerene.UserInterface.Map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aplikacijazasportsketerene.Location.CurrentUserLocation
import com.example.aplikacijazasportsketerene.Location.PersistedNearbyUsers
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@SuppressLint("MutableCollectionMutableState")
@Composable
fun MapDrawer(
    mapViewModel: MapDrawerViewModel = MapDrawerViewModel.getClassInstance(),
    onNavigateToAddCourt: (LatLng) -> Unit
){
    val locationUpdates = CurrentUserLocation.getClassInstance().location.collectAsState()
    val nearbyUsers by remember { mutableStateOf(PersistedNearbyUsers.persistedUsers) }
    var temporaryMarker by remember { mutableStateOf<LatLng?>(null) }

    val currentLocation = locationUpdates.value
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(currentLocation?.latitude ?: 0.0, currentLocation?.longitude ?: 0.0), 15f
        )
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        cameraPositionState = cameraPositionState,
        onMapLongClick = { latLng ->
            temporaryMarker = latLng
            onNavigateToAddCourt(latLng)
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
            if(nearbyUsers.isNotEmpty()) {
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
                                title = "Korisnik: ${it.username}"
                            )
                        }
                    }
                }
            }

            temporaryMarker?.let { latLng ->
                Marker(
                    state = MarkerState(position = latLng),
                    title = "Novi teren",
                    //icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
            }
        }
    }
}