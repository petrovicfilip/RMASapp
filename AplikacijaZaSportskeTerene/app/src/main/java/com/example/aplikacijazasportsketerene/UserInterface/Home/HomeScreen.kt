package com.example.aplikacijazasportsketerene.UserInterface.Home

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.aplikacijazasportsketerene.Location.CurrentUserLocation
import com.example.aplikacijazasportsketerene.Location.PersistedNearbyUsers
import com.example.aplikacijazasportsketerene.R
import com.example.aplikacijazasportsketerene.Screen
import com.example.aplikacijazasportsketerene.Services.AccountService
import com.example.aplikacijazasportsketerene.UserInterface.AddCourt.AddCourtViewModel
import com.example.aplikacijazasportsketerene.UserInterface.Map.MapDrawer
import com.example.aplikacijazasportsketerene.UserInterface.NavBar.NavigationBar
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun HomePage(
    navController: NavController,
    applicationContext : Context,
    navigationBar: NavigationBar,
    homeScreenViewModel: HomeScreenViewModel = HomeScreenViewModel.getInstance()
) {
    LaunchedEffect(Unit) {

    }
    Scaffold(
        bottomBar = { navigationBar.Draw(currentScreen = Screen.Home.name) },
        modifier = Modifier
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 40.dp, end = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(8.dp)

            ) {
                Column {
                    MapDrawer(
                        onNavigateToAddCourt = { latLng ->
                            val latitude = Uri.encode(latLng.latitude.toString())
                            val longitude = Uri.encode(latLng.longitude.toString())

                            navController.navigate(Screen.Loading.name)
                            AddCourtViewModel.getInstance()
                                .reverseGeocode(context = applicationContext, onCompleteDecoding = {
                                    navController.popBackStack(
                                        Screen.Loading.name,
                                        inclusive = true
                                    )
                                    navController.navigate("${Screen.AddCourt.name}/$latitude/$longitude")
                                }, latitude = latLng.latitude, longitude = latLng.longitude)
                            //navController.navigate("${Screen.AddCourt.name}/$latitude/$longitude")
                        },
                        navController = navController
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                if (homeScreenViewModel.selectedUser.value != null) {
                    val user = homeScreenViewModel.selectedUser.value!!
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .zIndex(1f) // Ensure the image is above other elements
                        ){
                            if (homeScreenViewModel.selectedUserProfilePicture.value != null) {
                                AsyncImage(
                                    model = homeScreenViewModel.selectedUserProfilePicture.value,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(75.dp)
                                        .clip(CircleShape)
                                        .border(
                                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                            shape = CircleShape
                                        ),
                                    contentScale = ContentScale.Crop,
                                )
                            } else
                                CircularProgressIndicator(modifier = Modifier.size(75.dp))
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Korisnicko ime",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Korisnicko ime",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = user.username,
                                            style = MaterialTheme.typography.bodyMedium
                                        )


                                    }
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Ime i prezime",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Ime i prezime",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = user.firstName!! + " " + user.lastName!!,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Telefon",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Telefon",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = user.phoneNumber!!,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}