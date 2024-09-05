package com.example.aplikacijazasportsketerene.DataClasses

import com.google.firebase.Timestamp

data class Comment(
    var id: String = "",
    val courtId: String = "",
    var value: String = "",
    val posted: Timestamp = Timestamp.now(),
    val likes: Int = 0,
    val userId: String = "",
    var numOfReplies: Int = 0 // moguce postaviti ovo kao mutable state, to je valjda jedno od resenja, pa da se napravi DTO, ali za sada je ok nije tolko bitno
                            // radi podsecanja to je zbog rekompozicije kod dodavanja reply-a komentaru koji ih nema, pa da se odma prikaze "Prikaži odgovore"
    //val replies: List<Comment>
)
