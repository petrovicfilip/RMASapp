package com.example.aplikacijazasportsketerene.UserInterface.all

import NavigationBar
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.aplikacijazasportsketerene.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfilePage(navController: NavController, context: Context?, navigationBar: NavigationBar) {
    Scaffold(bottomBar = { navigationBar.Draw(currentScreen = Screen.Profile.name) }) {
    }
}