package com.example.aplikacijazasportsketerene.UserInterface.Court

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.aplikacijazasportsketerene.DataClasses.Court
import com.example.aplikacijazasportsketerene.R
import com.example.aplikacijazasportsketerene.SingletonViewModel

@Composable
fun CourtScreen(
    court: Court,
    courtViewModel: CourtViewModel = viewModel<CourtViewModel>(
        factory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CourtViewModel(court = court)
                        as T
            }
        }
    ),
    navController: NavController) {

    LaunchedEffect(court) {
/*        courtViewModel.set()
        courtViewModel.court.value = court
        courtViewModel.courtRating.intValue = court.rating
        courtViewModel.courtRatedBy.intValue = court.ratedBy*/
        courtViewModel.getMyReviewAndUpdateReviewChecker(cid = court.id!!)
        courtViewModel.getImages(court.id)
        //courtViewModel.set()
    }

    BackHandler {
        navController.navigateUp()
        //courtViewModel.reset()
        SingletonViewModel.resetInstance(CourtViewModel::class.java)
    }

        courtViewModel.court.value?.let {
            CourtDetails(it, courtViewModel.images,courtViewModel)
        } ?: run {
            Text("Teren nije pronadjen.")
        }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CourtDetails(court: Court, images: List<Uri?>, courtViewModel: CourtViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { _ ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp)
        ) {
            // Naziv terena
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = court.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp, top = 20.dp)
                )
            }
            // Kartica za opis terena
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Description Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Opis terena",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = court.description,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                /*Text(
                    text = court.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )*/
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kartica za adresu (ulica i grad)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Adresa",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        text = "${court.street}, ${court.city}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kartica za rejting
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating Icon",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rejting",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    if(courtViewModel.courtRatedBy.intValue != 0){
                        Text(
                            text = "${courtViewModel.courtRating.intValue.toDouble() / courtViewModel.courtRatedBy.intValue.toDouble()}/5 " +
                                    "(${courtViewModel.courtRatedBy.intValue} ocena)",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    else{
                        Text(
                            text = "0/5 (0) ocena",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prikaz slika u horizontalnoj LazyRow listi
            if (!courtViewModel.isLoading.value) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        items(images.size) { index ->
                            val uri = images[index]
                            uri?.let {
                                Image(
                                    painter = rememberAsyncImagePainter(it),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kartica za ocenjivanje
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                if(!courtViewModel.postingReview.value){
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rate Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (!courtViewModel.reviewChecker.value)
                                Text(
                                    text = "Oceni teren",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            else {
                                Text(
                                    text = "Azuriraj ocenu terena, trenutno(${courtViewModel.myRating.intValue}/5)",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Red za zvezdice
                        var selectedRating by remember { mutableStateOf(0) }
                        var userChangingReview by remember { mutableStateOf(false)}
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (i in 1..5) {
                                IconButton(
                                    onClick = {
                                        selectedRating = i
                                        userChangingReview = true
                                              },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    if(userChangingReview)
                                        Icon(
                                            painter = if (i <= selectedRating)
                                                painterResource(id = R.drawable.baseline_star_24)
                                            else
                                                painterResource(id = R.drawable.baseline_star_outline_24),
                                            contentDescription = "Ocena $i",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    else
                                        Icon(
                                            painter = if (i <= courtViewModel.myRating.intValue)
                                                painterResource(id = R.drawable.baseline_star_24)
                                            else
                                                painterResource(id = R.drawable.baseline_star_outline_24),
                                            contentDescription = "Ocena $i",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(40.dp)
                                        )
                                }
                            }
                        }

                        // Dugme za ocenjivanje koje se pojavljuje kada je ocena izabrana
                        if (selectedRating > 0) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    courtViewModel.postingReview.value = true
                                    courtViewModel.addOrUpdateReview(
                                        cid = court.id!!,
                                        value = selectedRating
                                    )
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Oceni")
                            }
                        }
                    }
                }
                else {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(

                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(45.dp))
        }
    }
}
