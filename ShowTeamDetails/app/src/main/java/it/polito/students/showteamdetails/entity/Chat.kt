package it.polito.students.showteamdetails.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import java.time.LocalDateTime

data class Chat(
    var id: String = "",
    var lastMessageDate: LocalDateTime? = null,
    var messages: MutableList<Message> = mutableListOf(),
    var singleSender: Member? = null,
    var teamId: String? = null //Used to reference the team if this is a group chat
)

data class FirebaseIndividualChat(
    var lastMessageDate: Timestamp? = null,
    var messages: List<DocumentReference> = listOf(),
    var users: List<DocumentReference> = listOf()
)

data class FirebaseGroupChat(
    var lastMessageDate: Timestamp? = null,
    var messages: List<DocumentReference> = listOf(),
    var teamId: DocumentReference? = null
)