package com.example.aplikacijazasportsketerene

import com.example.aplikacijazasportsketerene.UserInterface.NavBar.NavigationBar
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aplikacijazasportsketerene.DataClasses.Court
import com.example.aplikacijazasportsketerene.Location.CourtsService
import com.example.aplikacijazasportsketerene.Location.LocationService
import com.example.aplikacijazasportsketerene.Location.PersistedNearbyUsers
import com.example.aplikacijazasportsketerene.Location.UsersService
import com.example.aplikacijazasportsketerene.Services.PermissionService
import com.example.aplikacijazasportsketerene.UserInterface.AddCourt.AddCourtScreen
import com.example.aplikacijazasportsketerene.UserInterface.Court.CourtScreen
import com.example.aplikacijazasportsketerene.UserInterface.Home.HomePage
import com.example.aplikacijazasportsketerene.UserInterface.Loading.LoadingScreen
import com.example.aplikacijazasportsketerene.UserInterface.ProfileScreen.ProfilePage
import com.example.aplikacijazasportsketerene.UserInterface.Splash.SplashScreen
import com.example.aplikacijazasportsketerene.UserInterface.LogIn.LogInScreen
import com.example.aplikacijazasportsketerene.UserInterface.Search.FilterScreen
import com.example.aplikacijazasportsketerene.UserInterface.Search.SearchScreen
import com.example.aplikacijazasportsketerene.UserInterface.Search.ViewMapForSearchedCourts
import com.example.aplikacijazasportsketerene.UserInterface.SignUp.SignUpScreen
import com.example.aplikacijazasportsketerene.ui.theme.AplikacijaZaSportskeTereneTheme
import com.google.gson.Gson


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
                // POMERITI NA ODGOVARAJUCA MESTA BALGOVREMENO...
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
        Intent(applicationContext, CourtsService::class.java).apply {
            action = UsersService.ACTION_STOP
            startService(this)
        }

        // Temporary !!!!
        PersistedNearbyUsers.getClassInstance().reset()
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
            },
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
                    composable(
                        route = "${Screen.AddCourt.name}/{latitude}/{longitude}",
                        arguments = listOf(
                            navArgument("latitude") { type = NavType.FloatType },
                            navArgument("longitude") { type = NavType.FloatType }
                        )
                    ) { backStackEntry ->
                        val latitude = backStackEntry.arguments?.getFloat("latitude") ?: 0f
                        val longitude = backStackEntry.arguments?.getFloat("longitude") ?: 0f
                        AddCourtScreen(latitude = latitude, longitude = longitude, navController = navController, context = context)
                    }
                    composable(
                        route = "${Screen.Court.name}/{court}",
                        arguments = listOf(navArgument("court") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val courtJson = backStackEntry.arguments?.getString("court")
                        val court = Gson().fromJson(courtJson, Court::class.java)
                        court?.let { CourtScreen(court = it, navController = navController) }
                    }
                    composable(Screen.Search.name){
                        SearchScreen(navController = navController, context = context)
                    }
                    composable(
                        route = "${Screen.SearchedCourts.name}/{court}",
                        arguments = listOf(navArgument("court") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val courtJson = backStackEntry.arguments?.getString("court")
                        val court = Gson().fromJson(courtJson, Court::class.java)
                        ViewMapForSearchedCourts(court)
                    }
                    composable(Screen.Filter.name){
                        FilterScreen(navController = navController)
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
    Loading,
    AddCourt,
    Court,
    SearchedCourts,
    Filter
}