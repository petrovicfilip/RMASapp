package com.example.aplikacijazasportsketerene.UserInterface.all

import NavigationBar
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.aplikacijazasportsketerene.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfilePage(
    navController: NavController,
    context: Context?,
    navigationBar: NavigationBar,
    ) {
    val pvm =  ProfileViewModel.getClassInstance()

    Scaffold(bottomBar = { navigationBar.Draw(currentScreen = Screen.Profile.name) }) {
        if(pvm.profilePicture == null)
            pvm.getUserProfilePicture()
        Spacer(modifier = Modifier.height(15.dp))
        Box(
            Modifier
                .fillMaxWidth()
        ){
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (pvm.profilePicture != null) {
                        Image(
                            painter = rememberAsyncImagePainter(pvm.profilePicture),
                            contentDescription = null,
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)
                                .border(
                                    border = BorderStroke(2.dp, Color.Cyan),
                                    shape = CircleShape
                                ),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        CircularProgressIndicator(modifier = Modifier.size(128.dp))
                    }
                }
            }
        }
    }
}