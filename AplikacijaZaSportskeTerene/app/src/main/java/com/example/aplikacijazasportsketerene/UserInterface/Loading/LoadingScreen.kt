package com.example.aplikacijazasportsketerene.UserInterface.Loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingScreen(
    loadingScreenViewModel: LoadingScreenViewModel = LoadingScreenViewModel.getInstance()
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(100.dp))

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when {
                    loadingScreenViewModel.makingAccount.value -> "Pravljenje naloga..."
                    loadingScreenViewModel.uploadingProfilePicture.value -> "Upload-ovanje profilne slike..."
                    loadingScreenViewModel.uploadingCourtPictures.value -> "Upload-ovanje slika terena..."
                    loadingScreenViewModel.loggingIn.value -> "Logovanje..."
                    loadingScreenViewModel.uploadingCourtBasicInfo.value -> "Uploadovanje terena..."
                    else -> "..."
                }
            )
        }
    }

}
