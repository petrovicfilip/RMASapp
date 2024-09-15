package com.example.aplikacijazasportsketerene.UserInterface.UserCourts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplikacijazasportsketerene.R
import com.example.aplikacijazasportsketerene.Screen
import com.example.aplikacijazasportsketerene.UserInterface.Search.CourtItem
import com.google.gson.Gson

@Composable
fun UserCourtsScreen(
    usersCourtsScreenViewModel: UserCourtsScreenViewModel = UserCourtsScreenViewModel.getInstance(),
    navController: NavController,
    userId: String
) {
    LaunchedEffect(userId) {
        usersCourtsScreenViewModel.loadCourts(userId)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Column(modifier = Modifier.align(Alignment.CenterHorizontally)){
            IconButton(onClick = { usersCourtsScreenViewModel.loadCourts(userId) }) {
                Icon(painter = painterResource(R.drawable.baseline_sync_24), contentDescription = "Reload")
            }
        }

        if(!usersCourtsScreenViewModel.loadingCourts.value){
            if (usersCourtsScreenViewModel.courts.isNotEmpty()) {
                LazyColumn {
                    items(usersCourtsScreenViewModel.courts) { court ->
                        CourtItem(court = court, navController = navController) {
                            val courtJson = Gson().toJson(court)
                            navController.navigate("${Screen.Court.name}/$courtJson")
                        }
                    }
                }
            } else
                Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(text = "Nema postavljenih terena!")
                }
        }
        else
            Column(modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally)) {
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ){
                    CircularProgressIndicator(modifier = Modifier.size(180.dp)
                    )
                }
            }
    }
}