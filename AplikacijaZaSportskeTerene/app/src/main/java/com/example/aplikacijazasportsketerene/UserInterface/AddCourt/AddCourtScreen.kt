package com.example.aplikacijazasportsketerene.UserInterface.AddCourt

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

    val name by addCourtViewModel.name
    val type by addCourtViewModel.type
    val description by addCourtViewModel.description
    val city by addCourtViewModel.city
    val street by addCourtViewModel.street
    val streetsMenuExpanded by addCourtViewModel.streetsMenuExpanded


    // Za sada...
    val courtTypes = listOf("Fudbalski", "Košarkaški", "Odbojkaški")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Dodaj Teren") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.Transparent)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(20.dp),

            //horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center
            ){
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
            ){
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
            ){
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
            ){
                Text(
                    text = "Koordinate: $latitude, $longitude",
                    style = TextStyle(fontSize = 16.sp, color = Color.Gray)
                )
            }

            AddImagesForCourt()

            Button(
                onClick = {
                    // Action to be implemented later
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dodaj Teren")
            }
        }
    }
}
