package it.polito.students.showteamdetails.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import java.time.LocalDateTime

data class Message(
    var id: String = "",
    var text: String = "",
    var sendDate: LocalDateTime = LocalDateTime.now(),
    var sender: Member,
    var read: Boolean = false,
    var edited: Boolean = false
)

class FirebaseMessage {
    lateinit var text: String
    lateinit var sendDate: Timestamp
    lateinit var sender: DocumentReference
    lateinit var read: Map<String, Boolean> //for each User id the boleean says if the member has read the message
    var edited: Boolean = false
}