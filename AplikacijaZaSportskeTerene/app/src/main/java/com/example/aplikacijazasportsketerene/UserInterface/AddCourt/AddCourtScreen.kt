package com.example.aplikacijazasportsketerene.UserInterface.AddCourt

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplikacijazasportsketerene.Screen


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddCourtScreen(
    latitude: Float,
    longitude: Float,
    addCourtViewModel: AddCourtViewModel = AddCourtViewModel.getInstance(),
    navController: NavController,
    context: Context
) {
    LaunchedEffect(Unit) {
        addCourtViewModel.setCoordinates(latitude, longitude)
        //addCourtViewModel.reverseGeocode(context,{})
    }

    BackHandler {
        navController.navigateUp()
        addCourtViewModel.reset()
    }

    val name by addCourtViewModel.name
    val type by addCourtViewModel.type
    val description by addCourtViewModel.description
    val city by addCourtViewModel.city
    val street by addCourtViewModel.street
    val streetsMenuExpanded by addCourtViewModel.streetsMenuExpanded

    //val isVisible by addCourtViewModel.isVisible

    val isVisible by remember { mutableStateOf(false) }

    // Za sada...
    val courtTypes = listOf("Fudbalski", "Košarkaški", "Odbojkaški")

    /*AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },  // Počinje sa dna ekrana
            animationSpec = tween(durationMillis = 300)  // Trajanje animacije 300ms
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },  // Vraća se ka dnu ekrana
            animationSpec = tween(durationMillis = 300)
        )
    )*/
    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Dodaj Teren") },
                navigationIcon = {
                    IconButton(onClick = {
                        addCourtViewModel.reset()
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
                .background(Color.Transparent)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(20.dp),

            //horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { addCourtViewModel.updateName(it) },
                    label = { Text("Naziv terena") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            var expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tip terena") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                        }
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    courtTypes.forEach { courtType ->
                        DropdownMenuItem(
                            text = { Text(courtType) },
                            onClick = {
                                addCourtViewModel.updateType(courtType)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = {},
                    readOnly = true, // Grad je zaključan
                    label = { Text("Grad") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                OutlinedTextField(
                    value = street,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ulica") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { addCourtViewModel.updateStreetsMenuExpanded(true) }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                        }
                    }
                )
                DropdownMenu(
                    expanded = streetsMenuExpanded,
                    onDismissRequest = { addCourtViewModel.updateStreetsMenuExpanded(false) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    addCourtViewModel.streets.forEach { street ->
                        DropdownMenuItem(
                            text = { Text(street) },
                            onClick = {
                                addCourtViewModel.updateStreet(street)
                                addCourtViewModel.updateStreetsMenuExpanded(false)
                            }
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { addCourtViewModel.updateDescription(it) },
                    label = { Text("Opis") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }

            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Koordinate: $latitude, $longitude",
                    style = TextStyle(fontSize = 16.sp, color = Color.Gray)
                )
            }

            AddImagesForCourt()

            Button(
                onClick = {
                    addCourtViewModel.uploadCourt(
                        {
                            navController.navigate(Screen.Loading.name)
                        }, {
                            navController.popBackStack(Screen.Home.name, inclusive = false)
                        })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Text("Dodaj Teren")
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
