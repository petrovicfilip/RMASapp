package com.example.aplikacijazasportsketerene.UserInterface.Map

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aplikacijazasportsketerene.DataClasses.Court
import com.example.aplikacijazasportsketerene.Location.CurrentUserLocation
import com.example.aplikacijazasportsketerene.UserInterface.Search.SearchViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("UnrememberedMutableState")
@Composable
fun MapForSearchedCourts(
    searchViewModel: SearchViewModel = SearchViewModel.getInstance(),
    court: Court
) {
    val cameraPositionState = rememberCameraPositionState()

    if (court.id == "") {
        val firstCourt = searchViewModel.searchResults.firstOrNull()
        firstCourt?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(it.latLon.latitude, it.latLon.longitude),
                14f
            )
        }
    } else {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(
            LatLng(court.latLon.latitude, court.latLon.longitude),
            15f
        )
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            //.height(400.dp)
            .clip(RoundedCornerShape(16.dp))
            .navigationBarsPadding(),
         cameraPositionState = cameraPositionState,
    ) {

        Marker(
            state = MarkerState(
                position = LatLng(
                    CurrentUserLocation.getClassInstance().location.collectAsState().value!!.latitude,
                    CurrentUserLocation.getClassInstance().location.collectAsState().value!!.longitude

                )
            ),
            title = "Moja lokacija",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
            onClick = { marker ->
                if (marker.isInfoWindowShown)
                    marker.showInfoWindow()
                false
            }
        )
        if(court.id == "")
            searchViewModel.searchResults.forEach { it ->
                Marker(
                    state = MarkerState(
                        position = LatLng(
                            it.latLon.latitude,
                            it.latLon.longitude
                        )
                    ),
                    title = it.name,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
                    onClick = { marker ->
                        if (marker.isInfoWindowShown)
                            marker.showInfoWindow()
                        false
                    }
                )
            }
        else
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
                    if (marker.isInfoWindowShown)
                        marker.showInfoWindow()
                    false
                }
            )

    }
}