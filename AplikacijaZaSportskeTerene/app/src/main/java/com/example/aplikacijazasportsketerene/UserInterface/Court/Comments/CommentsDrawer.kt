package com.example.aplikacijazasportsketerene.UserInterface.Court.Comments

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikacijazasportsketerene.DataClasses.Comment
import com.example.aplikacijazasportsketerene.R
import com.example.aplikacijazasportsketerene.UserInterface.Court.CourtViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun CommentsSection(
    courtViewModel: CourtViewModel
) {
    var showComments by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Sekcija za dodavanje komentara
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = commentText,
                onValueChange = { commentText = it },
                cursorBrush = SolidColor(Color.Gray),
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    color = Color.Gray
                ),
                decorationBox = { innerTextField ->
                    if (commentText.isEmpty()) {
                        Text("Dodaj komentar...", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))
                    }
                    innerTextField()
                }
            )

            IconButton(
                onClick = {
                    if (commentText.isNotEmpty()) {
                        courtViewModel.postingComment.value = true
                        courtViewModel.newComment.value.value = commentText
                        courtViewModel.addComment(Comment().copy(value = commentText, userId = Firebase.auth.currentUser!!.uid, courtId = courtViewModel.court.value!!.id!!)) // updatuje newComment
                        commentText = ""
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Dodaj komentar", tint = MaterialTheme.colorScheme.primary)
            }
        }

        // IconButton za prikaz komentara
        IconButton(
            onClick = {
            showComments = !showComments
                if(showComments) {
                    courtViewModel.fetchingComments.value = true
                    courtViewModel.getCommentsForCourt()
                }
        }) {
            Icon(
                painter = painterResource((R.drawable.baseline_insert_comment_24)),
                contentDescription = "Prikaži komentare",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        if (showComments && !courtViewModel.fetchingComments.value && !courtViewModel.postingComment.value) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                courtViewModel.commentsAndReplies.keys.forEach { comment ->
                    SingleComment(
                        comment = comment,
                        replies = courtViewModel.commentsAndReplies[comment] ?: emptyList(),
                        onReplyClick = { replyText ->
                            courtViewModel.commentsAndReplies[comment] = courtViewModel.commentsAndReplies[comment]?.plus(Comment(value = replyText))
                            // da azurirm odgovore u bazi i treba da updatujem numOfReplies na taj
                            courtViewModel.addReply(comment,replyText)
                        },
                        courtViewModel
                    )
                }
            }
        }
        else if(courtViewModel.fetchingComments.value || courtViewModel.postingComment.value ){
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                CircularProgressIndicator(Modifier.height(100.dp))
            }
        }
    }
}


@Composable
fun SingleComment(
    comment: Comment, // Tekst komentara
    replies: List<Comment>, // Lista odgovora na komentar
    onReplyClick: (String) -> Unit, // Funkcija za dodavanje odgovora
    courtViewModel: CourtViewModel
) {
    var showReplies by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    var isReplying by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Prikaz korisničkog imena (fiksno za sada)
            Text(
                text = courtViewModel.user.value!!.username, // Ovo je placeholder za ime korisnika
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Prikaz teksta komentara
            Text(
                text = comment.value,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dugme za odgovor
            TextButton(onClick = { isReplying = !isReplying }) {
                Text("Odgovori", color = MaterialTheme.colorScheme.primary)
            }

            // Prikaz polja za unos odgovora kada se klikne na "Odgovori"
            if (isReplying) {
                BasicTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = Color.Gray
                    ),
                    cursorBrush = SolidColor(Color.Gray),
                    decorationBox = { innerTextField ->
                        if (replyText.isEmpty()) {
                            Text(
                                "Dodaj odgovor...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                        innerTextField()
                    }
                )

                Button(
                    onClick = {
                        if (replyText.isNotEmpty()) {
                            onReplyClick(replyText) // Dodaj odgovor
                            replyText = "" // Resetuj polje za unos odgovora
                            isReplying = false
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Dodaj odgovor")
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Prikaz odgovora ako postoje
            TextButton(onClick = {
                showReplies = !showReplies
                if(showReplies) {
                    courtViewModel.fetchingReplies[comment] = true
                    courtViewModel.getRepliesForComment(comment)
                }
            }) {
                if(comment.numOfReplies > 0){
                    Text(
                        if (showReplies) "Sakrij odgovore" else "Prikaži odgovore",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (showReplies && !courtViewModel.fetchingReplies[comment]!!) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    replies.forEach { reply ->
                        Reply(reply, courtViewModel)
                    }
                }
            }
            else if(courtViewModel.fetchingReplies[comment] == null){
            }
            else if(courtViewModel.fetchingReplies[comment]!!)
            {
                Column(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
fun Reply(reply: Comment,courtViewModel: CourtViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp), // Pomeramo reply karticu malo udesno da bude jasno da je odgovor
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary) //0xff657273
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)) {
            // Prikaz korisničkog imena odgovora (placeholder za sada)
            Text(
                text = courtViewModel.user.value!!.username,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Prikaz teksta odgovora
            Text(
                text = reply.value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
    Spacer(modifier = Modifier.height(5.dp))
}