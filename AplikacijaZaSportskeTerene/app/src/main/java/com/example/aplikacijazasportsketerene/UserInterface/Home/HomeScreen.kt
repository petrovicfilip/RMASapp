package com.example.aplikacijazasportsketerene.UserInterface.Home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.aplikacijazasportsketerene.Location.LocationService
import com.example.aplikacijazasportsketerene.Screen
import com.example.aplikacijazasportsketerene.UserInterface.AddCourt.AddCourtViewModel
import com.example.aplikacijazasportsketerene.UserInterface.Map.MapDrawer
import com.example.aplikacijazasportsketerene.UserInterface.NavBar.NavigationBar
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun HomePage(
    navController: NavController,
    applicationContext : Context,
    navigationBar: NavigationBar,
    homeScreenViewModel: HomeScreenViewModel = HomeScreenViewModel.getInstance()
) {
    LaunchedEffect(true) {
        withContext(Dispatchers.IO){
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                applicationContext.startService(this)
            }
        }
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
                if (homeScreenViewModel.showButtonForUsers.value) {
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
                }
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
                                .zIndex(1f)
                        ){
                            if (homeScreenViewModel.loadingProfilePicture.value) {
                                CircularProgressIndicator(modifier = Modifier.size(75.dp))
                            } else if(homeScreenViewModel.selectedUserProfilePicture.value.path != "101")
                                AsyncImage(
                                    model = homeScreenViewModel.selectedUserProfilePicture.value,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(75.dp)
                                        .clip(CircleShape)
                                        .border(
                                            border = BorderStroke(
                                                2.dp,
                                                MaterialTheme.colorScheme.primary
                                            ),
                                            shape = CircleShape
                                        ),
                                    contentScale = ContentScale.Crop,
                                )
                            else
                                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Default slika", Modifier.size(75.dp).zIndex(1f))
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