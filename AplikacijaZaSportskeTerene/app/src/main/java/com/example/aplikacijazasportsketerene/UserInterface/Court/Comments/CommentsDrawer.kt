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
import androidx.compose.material.icons.filled.Delete
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
import androidx.lifecycle.viewModelScope
import com.example.aplikacijazasportsketerene.DataClasses.Comment
import com.example.aplikacijazasportsketerene.R
import com.example.aplikacijazasportsketerene.UserInterface.Court.CourtViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

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
                        courtViewModel.addComment(Comment().copy(value = commentText, userId = Firebase.auth.currentUser!!.uid,
                            courtId = courtViewModel.court.value!!.id!!, posterUsername = courtViewModel.user.value!!.username)) // updatuje newComment
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

                    if(courtViewModel.showReplies[comment] == null)
                        courtViewModel.showReplies[comment] = false
                    if(courtViewModel.fetchingReplies[comment] == null)
                        courtViewModel.fetchingReplies[comment] = false

                    SingleComment(
                        comment = comment,
                        replies = courtViewModel.commentsAndReplies[comment] ?: emptyList(),
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
    courtViewModel: CourtViewModel
) {
    var showReplies by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    var isReplying by remember { mutableStateOf(false) }

/*    courtViewModel.showReplies[comment] = false
    courtViewModel.fetchingReplies[comment] = false*/
    //val currentReplies by remember { mutableStateOf(courtViewModel.commentsAndReplies[comment] ?: emptyList()) }

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
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = comment.posterUsername, // Ovo je placeholder za ime korisnika
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.weight(1f))
                if(comment.userId == Firebase.auth.currentUser!!.uid) {
                    IconButton(onClick = { courtViewModel.deleteComment(comment.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "delete comment"
                        )
                    }
                }
            }

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
                            // da azurirm odgovore u bazi i treba da updatujem numOfReplies na taj
                            isReplying = true
                            courtViewModel.fetchingReplies[comment] = true
                            courtViewModel.addReply(comment, replyText) // Dodaj odgovor
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
                if(courtViewModel.showReplies[comment] == null)// ne bi trebalo da moze da se desi!!!
                    courtViewModel.showReplies[comment] = true // ne bi trebalo da moze da se desi!!!
                else courtViewModel.showReplies[comment] = !courtViewModel.showReplies[comment]!!
                if(courtViewModel.showReplies[comment] == true) {
                    courtViewModel.fetchingReplies[comment] = true
                    courtViewModel.getRepliesForComment(comment)
                }
            }) {
                if(comment.numOfReplies > 0){
                    Text(
                        if (courtViewModel.showReplies[comment] != null && courtViewModel.showReplies[comment] == true) "Sakrij odgovore" else "Prikaži odgovore",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if(courtViewModel.fetchingReplies[comment] != null && courtViewModel.fetchingReplies[comment] == true)
            {
                Column(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(modifier = Modifier.height(60.dp))
                }
            }
            else if (courtViewModel.fetchingReplies[comment] != null && courtViewModel.showReplies[comment] == true && courtViewModel.fetchingReplies[comment] == false) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    replies.forEach { reply ->
                        Reply(reply, courtViewModel, comment.id)
                    }
                }
            }
        }
    }
}

@Composable
fun Reply(reply: Comment,courtViewModel: CourtViewModel,commentId: String) {
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
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = reply.posterUsername,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.weight(1f))
                if(reply.userId == Firebase.auth.currentUser!!.uid) {
                    IconButton(onClick = {
                        val comment = courtViewModel.fetchingReplies.keys.find { it.id == commentId }
                        if(comment != null)
                            courtViewModel.fetchingReplies[comment] = true
                        courtViewModel.deleteReply(commentId,reply.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "delete reply"
                        )
                    }
                }
            }

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