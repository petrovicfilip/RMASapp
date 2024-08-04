package com.example.aplikacijazasportsketerene

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplikacijazasportsketerene.UserInterface.signup.SignUpScreen
import com.example.aplikacijazasportsketerene.UserInterface.splash.HomePage
import com.example.aplikacijazasportsketerene.UserInterface.splash.SplashScreen
import com.example.aplikacijazasportsketerene.UserInterface.splash.LogInScreen
import com.example.aplikacijazasportsketerene.ui.theme.AplikacijaZaSportskeTereneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikacijaZaSportskeTereneTheme {
                //SplashScreen(navController = rememberNavController())
                NavigationInitialization(context = this)
            }
        }
    }

    @Composable
    private fun NavigationInitialization(context: Context) {

        val navController = rememberNavController()

        Box(modifier = Modifier.fillMaxSize()) {
            Surface(modifier = Modifier.fillMaxSize()) {
                NavHost(navController = navController, startDestination = Screen.Splash.name) {
                    composable(Screen.LogIn.name) {
                        LogInScreen(navController = navController, context = context)
                    }
                    composable(Screen.SignIn.name) {
                        SignUpScreen(navController = navController, context = context)
                    }
                    composable(Screen.Splash.name) {
                        SplashScreen(navController = navController)
                    }
                    composable(Screen.Home.name) {
                        HomePage(navController = navController)
                    }
                }
            }
        }
    }
}

enum class Screen {
    LogIn,
    SignIn,
    Splash,
    Home
}