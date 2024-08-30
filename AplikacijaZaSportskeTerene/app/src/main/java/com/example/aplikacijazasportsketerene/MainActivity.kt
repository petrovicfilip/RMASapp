package com.example.aplikacijazasportsketerene

import NavigationBar
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplikacijazasportsketerene.DataClasses.User
import com.example.aplikacijazasportsketerene.Location.LocationService
import com.example.aplikacijazasportsketerene.Location.LocationService.Companion.NEARBY_USERS_CHANNEL_ID
import com.example.aplikacijazasportsketerene.Location.PersistedNearbyUsers
import com.example.aplikacijazasportsketerene.Location.UsersService
import com.example.aplikacijazasportsketerene.Services.PermissionService
import com.example.aplikacijazasportsketerene.UserInterface.signup.SignUpScreen
import com.example.aplikacijazasportsketerene.UserInterface.all.HomePage
import com.example.aplikacijazasportsketerene.UserInterface.all.LoadingScreen
import com.example.aplikacijazasportsketerene.UserInterface.all.ProfilePage
import com.example.aplikacijazasportsketerene.UserInterface.all.SplashScreen
import com.example.aplikacijazasportsketerene.UserInterface.all.LogInScreen
import com.example.aplikacijazasportsketerene.ui.theme.AplikacijaZaSportskeTereneTheme
//import com.google.firebase.firestore.auth.User

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissions = PermissionService(activity = this)
        permissions.getNotificationPermissions()
        permissions.getLocationPermissions()

        enableEdgeToEdge()
        setContent {
            AplikacijaZaSportskeTereneTheme {
                //SplashScreen(navController = rememberNavController())
                NavigationInitialization(context = this)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        "location",
                        "Location",
                        NotificationManager.IMPORTANCE_LOW
                    )

                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)

                }
                Intent(applicationContext, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                    startService(this)
                }
                Intent(applicationContext, UsersService::class.java).apply {
                    action = UsersService.ACTION_START
                    startService(this)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            startService(this)
        }
        Intent(applicationContext, UsersService::class.java).apply {
            action = UsersService.ACTION_STOP
            startService(this)
        }

        // Temporary !!!!
        PersistedNearbyUsers.persistedUsers = mutableListOf(null)
    }

    @Composable
    private fun NavigationInitialization(context: Context) {

        val navController = rememberNavController()
        val navigationBar = NavigationBar(
            navigateToSearchingPage = {
                navController.popBackStack(Screen.Search.name, inclusive = true)
                navController.navigate(Screen.Search.name)
            },
            navigateToPlayersPage = {
                navController.popBackStack(Screen.Players.name, inclusive = true)
                navController.navigate(Screen.Players.name)
            },
            navigateToProfilePage = {
                navController.popBackStack(Screen.Profile.name, inclusive = true)
                navController.navigate(Screen.Profile.name)
            },
            navigateToHomePage = {
                navController.popBackStack(Screen.Home.name, inclusive = true)
                navController.navigate(Screen.Home.name)
            },
            navigateToLikedCourtsPage = {
                navController.popBackStack(Screen.Courts.name, inclusive = true)
                navController.navigate(Screen.Courts.name)
            }
        )

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
                        HomePage(
                            navController = navController,
                            applicationContext = applicationContext,
                            navigationBar = navigationBar
                        )
                    }
                    composable(Screen.Search.name) {
                        SearchPage(
                            navController = navController,
                            context = applicationContext,
                            navigationBar = navigationBar
                        )
                    }
                    composable(Screen.Courts.name) {
                        CourtsPage(
                            navController = navController,
                            context = applicationContext,
                            navigationBar = navigationBar
                        )
                    }
                    composable(Screen.Profile.name) {
                        ProfilePage(
                            navController = navController,
                            context = applicationContext,
                            navigationBar = navigationBar
                        )
                    }
                    composable(Screen.Players.name) {
                        PlayersPage(
                            navController = navController,
                            context = applicationContext,
                            navigationBar = navigationBar
                        )
                    }
                    composable(Screen.Loading.name) {
                        LoadingScreen(
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    private
    @Composable
    fun PlayersPage(navController: NavController, context: Context?, navigationBar: NavigationBar) {
        Scaffold(bottomBar = { navigationBar.Draw(currentScreen = Screen.Players.name) }) {
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    private
    @Composable
    fun CourtsPage(navController: NavController, context: Context?, navigationBar: NavigationBar) {
        Scaffold(bottomBar = { navigationBar.Draw(currentScreen = Screen.Courts.name) }) {
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    private fun SearchPage(
        navController: NavController,
        context: Context?,
        navigationBar: NavigationBar
    ) {
        Scaffold(bottomBar = { navigationBar.Draw(currentScreen = Screen.Search.name) }) {
        }
    }
}
enum class Screen {
    LogIn,
    SignIn,
    Splash,
    Home,
    Search,
    Courts,
    Profile,
    Players,
    Loading
}