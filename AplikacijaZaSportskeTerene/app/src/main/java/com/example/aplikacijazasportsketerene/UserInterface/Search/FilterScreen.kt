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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import java.util.Calendar

@Composable
fun FilterScreen(
    searchViewModel: SearchViewModel = SearchViewModel.getInstance(),
    navController: NavController,
    //onApplyFilters: () -> Unit
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Select Court Types", style = MaterialTheme.typography.titleLarge)
        val courtTypes = listOf("Fudbalski", "Kosarkaski", "Odbojkaski", "Teniski", "Golf","Neodredjen")
        courtTypes.forEach { type ->
            var checked by remember { mutableStateOf(false) }
            if(type in searchViewModel.selectedTypes)
                checked = true

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
                Text("Tip terena: $type", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Pocetni datum:", style = MaterialTheme.typography.bodyLarge)

            Button(onClick = {
                DatePickerDialog(
                    context,
                    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                        calendar.set(year, month, dayOfMonth)
                        searchViewModel.dateBeginning = Timestamp(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text("Izaberi datum")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Krajni datum:", style = MaterialTheme.typography.bodyLarge)

            Button(onClick = {
                DatePickerDialog(
                    context,
                    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                        calendar.set(year, month, dayOfMonth)
                        searchViewModel.dateEnd = Timestamp(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text("Izaberi datum")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Minimum Rating Field
        Text("Minimum Rating", style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = searchViewModel.minimumRating.toString(),
            onValueChange = { searchViewModel.minimumRating = it.toInt() },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            placeholder = { Text("Unesi minimalnu prosecnu ocenu terena (1-5)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Apply Filters Button
        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Check, contentDescription = "Apply Filters")
            Spacer(modifier = Modifier. width(8.dp))
            Text("Zavrsi")
        }
    }
}