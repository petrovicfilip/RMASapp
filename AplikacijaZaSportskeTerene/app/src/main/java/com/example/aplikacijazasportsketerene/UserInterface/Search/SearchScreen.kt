package com.example.aplikacijazasportsketerene.UserInterface.Search

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplikacijazasportsketerene.DataClasses.Court
import com.example.aplikacijazasportsketerene.Screen
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = SearchViewModel.getInstance(),
    navController: NavController,
    context: Context
) {
    var expanded by remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf(TextFieldValue(searchViewModel.searchInput)) }
    val coroutineScope = rememberCoroutineScope()

    val searchTypeOptions = SearchTipovi.entries.toTypedArray()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Spacer(modifier = Modifier.height(30.dp))
        // Search Input Field with Icons
        Row(modifier = Modifier
            .fillMaxWidth()){
            TextField(
                value = searchText.value,
                onValueChange = {
                    searchText.value = it
                    searchViewModel.searchInput = it.text
                },
                modifier = Modifier
                    .padding(end = 5.dp)
                    .clip(RoundedCornerShape(5.dp)),
                placeholder = {
                    Text(getPlaceholder(searchViewModel.searchType))
                },
                leadingIcon = {
                    // Dropdown icon for search type selection
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Izaberi tip pretrage")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        searchTypeOptions.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    searchViewModel.searchType = type
                                    searchText.value = TextFieldValue("") // reset input field
                                    expanded = false
                                }
                            )
                        }
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        if(searchText.value.text == "" && searchViewModel.checkIfNoFiltersAreApplied() && searchViewModel.searchType == SearchTipovi.Ime)
                            Toast.makeText(context,"Unesi naziv terena ili izaberi filtere!",Toast.LENGTH_LONG).show()
                        else if(searchText.value.text == "" && searchViewModel.checkIfNoFiltersAreApplied() && searchViewModel.searchType == SearchTipovi.Grad)
                            Toast.makeText(context,"Unesi naziv grada ili izaberi filtere!",Toast.LENGTH_LONG).show()
                        else if(searchText.value.text == "" && searchViewModel.checkIfNoFiltersAreApplied() && searchViewModel.searchType == SearchTipovi.Ulica)
                            Toast.makeText(context,"Unesi naziv ulice ili izaberi filtere!",Toast.LENGTH_LONG).show()
                        else if(searchText.value.text == "" && searchViewModel.checkIfNoFiltersAreApplied() && searchViewModel.searchType == SearchTipovi.Radius)
                            Toast.makeText(context,"Unesi radius ili izaberi filtere!",Toast.LENGTH_LONG).show()
                        else
                        coroutineScope.launch(Dispatchers.IO) {
                            searchViewModel.searchCourts()
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .background(CardDefaults.cardColors().containerColor, RoundedCornerShape(5.dp)),
                onClick = {
                    navController.navigate(Screen.Filter.name)
            }) {
                Icon(Icons.Default.Menu, contentDescription = "Filters")
            }
        }

        if(searchViewModel.searchResults.isNotEmpty())
        {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    IconButton(onClick = {
                        val courtJson = Gson().toJson(Court())
                        navController.navigate("${Screen.SearchedCourts.name}/$courtJson")
                    }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Lokacije terena")
                    }

                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "Prikazi pronadjene terene na mapi"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Search Results
        LazyColumn {
            items(searchViewModel.searchResults) { court ->
                CourtItem(court = court, navController = navController){
                    val courtJson = Gson().toJson(court)
                    navController.navigate("${Screen.Court.name}/$courtJson")
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun CourtItem(court: Court, navController: NavController, onClick: () -> Unit,) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable(
                onClick = onClick,
                indication = rememberRipple(
                    bounded = true,
                    color = Color.Gray
                ), // Ripple effect when clicked
                interactionSource = MutableInteractionSource() // Handles interaction like press/click
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Court Name (Main Title)
            Text(
                text = court.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Court Type
            Text(
                text = "Tip: ${court.type}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Court Location (City and Street) with Location Button
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween // Align content to the left and button to the right
            ) {
                Column {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        // City Icon and Text
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "City",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = court.city,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        // Street Icon and Text
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Street",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = court.street,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // IconButton for Location
                IconButton(
                    modifier = Modifier
                        //.fillMaxSize()
                        .align(Alignment.CenterVertically),
                    onClick = {
                        val courtJson = Gson().toJson(court)
                        navController.navigate("${Screen.SearchedCourts.name}/$courtJson")
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(30.dp),
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Show Location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}



@Composable
fun getPlaceholder(type: SearchTipovi): String {
    return when (type) {
        SearchTipovi.Ime -> "Unesi naziv terena"
        SearchTipovi.Grad -> "Unesi naziv grada"
        SearchTipovi.Ulica -> "Unesi naziv ulice"
        SearchTipovi.Radius -> "Unesi radius u metrima"
    }
}
