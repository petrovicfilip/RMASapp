package com.example.aplikacijazasportsketerene.UserInterface.NavBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.aplikacijazasportsketerene.R
import com.example.aplikacijazasportsketerene.Screen

class NavigationBar(
    private val navigateToHomePage: () -> Unit,
    private val navigateToSearchingPage: () -> Unit,
    private val navigateToLikedCourtsPage: () -> Unit,
    private val navigateToPlayersPage: () -> Unit,
    private val navigateToProfilePage: () -> Unit,
) {
    @Composable
    fun Draw(currentScreen: String?) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 45.dp)
                .background(color = CardDefaults.cardColors().containerColor)
                .drawBehind {
                    val borderSize = 2.dp.toPx()
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, borderSize / 2),
                        end = Offset(size.width, borderSize / 2),
                        strokeWidth = borderSize
                    )
                }

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TransparentIconButton(
                    onClick = navigateToHomePage,
                    icon = if(currentScreen == Screen.Home.name)
                        Icons.Filled.Home
                    else Icons.Outlined.Home,
                    modifier = Modifier.weight(2f)
                )
                TransparentIconButton(
                    onClick = navigateToSearchingPage,
                    icon = if(currentScreen == Screen.Search.name)
                        Icons.Filled.Search
                    else Icons.Outlined.Search,
                    modifier = Modifier.weight(2f)
                )
                TransparentIconButton(
                    onClick = navigateToPlayersPage,
                    icon = if(currentScreen == Screen.Players.name)
                        ImageVector.vectorResource(id = R.drawable.baseline_people_24)
                    else ImageVector.vectorResource(id = R.drawable.baseline_people_outline_24),
                    modifier = Modifier.weight(2f)
                )
                TransparentIconButton(
                    onClick = navigateToLikedCourtsPage,
                    icon = if(currentScreen == Screen.Courts.name)
                        Icons.Filled.Place
                    else Icons.Outlined.Place,
                    modifier = Modifier.weight(2f)
                )
                TransparentIconButton(
                    onClick = navigateToProfilePage,
                    icon = if(currentScreen == Screen.Profile.name)
                        Icons.Filled.Person
                    else Icons.Outlined.Person,
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }
}


@Composable
fun TransparentIconButton(onClick: () -> Unit, icon: ImageVector, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp), // Remove elevation
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
    }
}