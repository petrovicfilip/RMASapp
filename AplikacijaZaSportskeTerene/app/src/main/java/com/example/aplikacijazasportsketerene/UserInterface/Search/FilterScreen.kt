package com.example.aplikacijazasportsketerene.UserInterface.Search

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplikacijazasportsketerene.R
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun FilterScreen(
    searchViewModel: SearchViewModel = SearchViewModel.getInstance(),
    navController: NavController
) {
    BackHandler {
        searchViewModel.selectedTypes = listOf()
        searchViewModel.dateBeginning = null
        searchViewModel.dateEnd = null
        searchViewModel.minimumRating = 0
        navController.navigateUp()
    }

    val calendar = Calendar.getInstance()
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var selectedStartDate by remember { mutableStateOf<String?>(null) }
    var selectedEndDate by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()
                ){
                    Text("Izaberi tip terena", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(modifier = Modifier.size(30.dp),
                        painter = painterResource(R.drawable.baseline_sports_basketball_24), contentDescription = "Lopta" )
                }

                val courtTypes = listOf("Fudbalski", "Kosarkaski", "Odbojkaski", "Teniski", "Golf", "Neodredjen")
                courtTypes.forEach { type ->
                    var checked by remember { mutableStateOf(type in searchViewModel.selectedTypes) }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                checked = isChecked
                                if (checked) {
                                    searchViewModel.selectedTypes += type
                                } else {
                                    searchViewModel.selectedTypes -= type
                                }
                            }
                        )
                        Text(type, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        // Start Date Picker in Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Početni datum:", style = MaterialTheme.typography.bodyLarge)

                Button(onClick = {
                    DatePickerDialog(
                        context,
                        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                            calendar.set(year, month, dayOfMonth)
                            searchViewModel.dateBeginning = Timestamp(calendar.time)
                            selectedStartDate = dateFormat.format(calendar.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text("Izaberi datum")
                }

                selectedStartDate?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Izabrani datum: $it", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // End Date Picker in Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Krajni datum:", style = MaterialTheme.typography.bodyLarge)

                Button(onClick = {
                    DatePickerDialog(
                        context,
                        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                            calendar.set(year, month, dayOfMonth)
                            searchViewModel.dateEnd = Timestamp(calendar.time)
                            selectedEndDate = dateFormat.format(calendar.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text("Izaberi datum")
                }

                selectedEndDate?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Izabrani datum: $it", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Minimum Rating Field in Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)){
                Text(text = "Izaberi minimalnu srednju ocenu terena:")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 1..5) {
                        IconButton(
                            onClick = {
                                searchViewModel.minimumRating = i
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                painter = if (i <= searchViewModel.minimumRating)
                                    painterResource(id = R.drawable.baseline_star_24)
                                else
                                    painterResource(id = R.drawable.baseline_star_outline_24),
                                contentDescription = "Ocena $i",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
                Text(text = "Izabrana ocena: ${searchViewModel.minimumRating}")
            }
        }

        // Apply Filters Button
        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                //.padding(16.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = "Apply Filters")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Završi")
        }
    }
}