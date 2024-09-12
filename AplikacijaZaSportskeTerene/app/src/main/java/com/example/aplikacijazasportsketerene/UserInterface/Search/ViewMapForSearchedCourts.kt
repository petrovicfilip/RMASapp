package com.example.aplikacijazasportsketerene.UserInterface.Search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aplikacijazasportsketerene.DataClasses.Court
import com.example.aplikacijazasportsketerene.UserInterface.Map.MapForSearchedCourts

@Composable
fun ViewMapForSearchedCourts(court: Court){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 40.dp, end = 8.dp)
            .navigationBarsPadding(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)

    ) {
        Column {
            MapForSearchedCourts(court = court)
        }
    }
}