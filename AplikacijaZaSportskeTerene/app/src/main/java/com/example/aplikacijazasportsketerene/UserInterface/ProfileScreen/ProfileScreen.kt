package com.example.aplikacijazasportsketerene.UserInterface.ProfileScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.aplikacijazasportsketerene.Location.CourtsService
import com.example.aplikacijazasportsketerene.Location.LocationService
import com.example.aplikacijazasportsketerene.Screen
import com.example.aplikacijazasportsketerene.Services.AccountService
import com.example.aplikacijazasportsketerene.UserInterface.NavBar.NavigationBar
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfilePage(
    navController: NavController,
    context: Context?,
    navigationBar: NavigationBar,
    ) {
    val profileViewModel = ProfileViewModel.getClassInstance()

    LaunchedEffect(Unit) {
        profileViewModel.loadUserData()
    }

    Scaffold(
        bottomBar = { navigationBar.Draw(currentScreen = Screen.Profile.name) },
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (profileViewModel.profilePicture == null)
            profileViewModel.getUserProfilePicture()
        Spacer(modifier = Modifier.height(15.dp))
        Column(
            Modifier
                .navigationBarsPadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it)
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(8.dp)
                ){
                    Box(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                    ){
                        if (profileViewModel.profilePicture != null) {
                            Image(
                                painter = rememberAsyncImagePainter(profileViewModel.profilePicture),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(128.dp)
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
                        } else {
                            CircularProgressIndicator(modifier = Modifier
                                .padding(10.dp)
                                .size(128.dp)
                            )
                        }
                    }
                }
            }
            //Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardColors(containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black, disabledContentColor = MaterialTheme.colorScheme.primary, disabledContainerColor = Color.Gray
                    ),
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
                                text = profileViewModel.username.value,
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
                    shape = RoundedCornerShape(8.dp),
                    colors = CardColors(containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black, disabledContentColor = MaterialTheme.colorScheme.primary, disabledContainerColor = Color.Gray
                    ),
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
                                text = profileViewModel.firstName.value + " " + profileViewModel.lastName.value,
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
                    shape = RoundedCornerShape(8.dp),
                    colors = CardColors(containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.Black, disabledContentColor = MaterialTheme.colorScheme.primary, disabledContainerColor = Color.Gray
                ),
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
                                text = profileViewModel.phoneNumber.value,
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
                    shape = RoundedCornerShape(8.dp),
                    colors = CardColors(containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black, disabledContentColor = MaterialTheme.colorScheme.primary, disabledContainerColor = Color.Gray
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "E-mail",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "E-mail",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = profileViewModel.email.value,
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
                    shape = RoundedCornerShape(8.dp),
                    colors = CardColors(containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black, disabledContentColor = MaterialTheme.colorScheme.primary, disabledContainerColor = Color.Gray
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Sakupljeni poeni",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Sakupljeni poeni",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = profileViewModel.points.doubleValue.toString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }


            Column(
                //horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(5.dp)
            ){
                Button(
                    modifier = Modifier.padding(5.dp),
                    onClick = {
                    profileViewModel.findingCourtsStarted.value =
                        !profileViewModel.findingCourtsStarted.value

                    if (profileViewModel.findingCourtsStarted.value) {
                        Intent(context, CourtsService::class.java).apply {
                            action = LocationService.ACTION_START
                            context?.startService(this)
                        }
                    } else {
                        Intent(context, CourtsService::class.java).apply {
                            action = LocationService.ACTION_STOP
                            context?.startService(this)
                        }
                    }
                }) {
                    if (!profileViewModel.findingCourtsStarted.value)
                        Text(text = "Pokreni trazenje terena")
                    else
                        Text(text = "Zaustavi trazenje terena")
                }

                Button(
                    modifier = Modifier.padding(5.dp),
                    onClick = {
                    AccountService.getClassInstance().signOut()
                }) {
                    Text(text = "Odjavi se")
                }
            }
        }

    }
}