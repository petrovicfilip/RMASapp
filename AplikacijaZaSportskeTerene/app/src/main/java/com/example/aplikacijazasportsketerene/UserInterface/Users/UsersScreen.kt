package com.example.aplikacijazasportsketerene.UserInterface.Users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplikacijazasportsketerene.DataClasses.User
import com.example.aplikacijazasportsketerene.R
import com.example.aplikacijazasportsketerene.Screen
import com.example.aplikacijazasportsketerene.UserInterface.NavBar.NavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    usersScreenViewModel: UsersScreenViewModel = UsersScreenViewModel.getInstance(),
    navigationBar: NavigationBar,
    navController: NavController) {

    //usersScreenViewModel.loadUsers()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Leaderboard") },
                actions = {
                    IconButton(onClick = { /* Handle sort button click */ }) {
                        Icon(painterResource(R.drawable.baseline_sort_24), contentDescription = "Sort")
                        DropdownMenuExample(usersScreenViewModel::sortUsers)
                    }
                }
            )
        },
        bottomBar = { navigationBar.Draw(currentScreen = Screen.Users.name) }
    ) { padding ->
        if(usersScreenViewModel.loadingUsers.value == false){
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                items(usersScreenViewModel.users.size) { index ->
                    usersScreenViewModel.users[index]?.let { UserCard(user = it) }
                }
            }
        }
        else
            CircularProgressIndicator(modifier = Modifier.height(200.dp))
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture
            user.profilePicture?.let {
                // TBD...
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column {
                Text(text = "${user.firstName ?: ""} ${user.lastName ?: ""}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Username: ${user.username}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Poeni: ${user.points}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun DropdownMenuExample(onSortSelected: (UsersScreenViewModel.SortType) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = !expanded }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Sort Options")
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(onClick = {
            onSortSelected(UsersScreenViewModel.SortType.BY_POINTS)
            expanded = false
        },
            text = {
                Text("Sortiraj po broju peona")
            })
        DropdownMenuItem(onClick = {
            onSortSelected(UsersScreenViewModel.SortType.BY_USERNAME)
            expanded = false
        },
            text = {
                Text("Sortiraj po username-u")
            })
        DropdownMenuItem(onClick = {
            onSortSelected(UsersScreenViewModel.SortType.BY_FIRST_NAME)
            expanded = false
        },
            text = {
                Text("Sortiraj po imenu")
            })
        DropdownMenuItem(onClick = {
            onSortSelected(UsersScreenViewModel.SortType.BY_LAST_NAME)
            expanded = false
        },
            text = {
                Text("Sortiraj po prezimenu")
            }
        )
    }
}