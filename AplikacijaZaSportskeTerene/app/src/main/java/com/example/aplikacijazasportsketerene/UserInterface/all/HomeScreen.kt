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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplikacijazasportsketerene.MainActivity
import com.example.aplikacijazasportsketerene.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(
    navController: NavController,
    applicationContext : Context,
    navigationBar: NavigationBar){
    Scaffold(bottomBar = { navigationBar.Draw(currentScreen = Screen.Home.name) })
    {
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
        }
    }
}