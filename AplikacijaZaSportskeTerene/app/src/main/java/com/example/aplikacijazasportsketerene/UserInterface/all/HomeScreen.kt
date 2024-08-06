package com.example.aplikacijazasportsketerene.UserInterface.all

import NavigationBar
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplikacijazasportsketerene.Location.LocationService
import com.example.aplikacijazasportsketerene.MainActivity
import com.example.aplikacijazasportsketerene.Screen
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(
    navController: NavController,
    applicationContext : Context,
    navigationBar: NavigationBar){
    Scaffold(bottomBar = { navigationBar.Draw(currentScreen = Screen.Home.name) })
    {
        val locationUpdates = LocationService.locationUpdates.collectAsState()

        val currentLocation = locationUpdates.value
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(currentLocation?.latitude ?: 0.0, currentLocation?.longitude ?: 0.0), 15f
            )
        }
        Column() {
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 35.dp)
            ) {
                Text(text = "Sve ce si bude samo polako!")
            }
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 35.dp)
            ) {
                Text(text = "user: ${if (Firebase.auth.currentUser != null) Firebase.auth.currentUser!!.email else ""}")
            }
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 35.dp)
            ) {
                Button(
                    onClick = {
                        navController.popBackStack(Screen.Home.name, inclusive = true)
                        navController.navigate(Screen.LogIn.name)
                        GlobalScope.launch(Dispatchers.IO) { Firebase.auth.signOut() }
                    },
                    Modifier
                        .height(60.dp)
                        .border(
                            BorderStroke(5.dp, color = Color.Black),
                            shape = RectangleShape
                        )
                ) {
                    Text(text = "Odjavi se!")
                }
            }
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                cameraPositionState = cameraPositionState
            ) {
                currentLocation?.let {
                    Marker(
                        state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                        title = "Vaša trenutna lokacija"
                    )
                }
            }
        }
    }
}