package it.polito.students.showteamdetails.entity

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import it.polito.students.showteamdetails.Utils
import java.time.LocalDateTime

data class CreatedInfo(
    var member: Member = Utils.memberAccessed.value,
    var timestamp: LocalDateTime = LocalDateTime.now()
)

class CreatedInfoFirebase {
    lateinit var member: DocumentReference
    lateinit var timestamp: Timestamp
}

fun CreatedInfo.toFirebase(): CreatedInfoFirebase {
    val createdInfo = CreatedInfoFirebase()
    createdInfo.member =
        Firebase.firestore.collection(Utils.CollectionsEnum.users.name).document(member.id)
    createdInfo.timestamp = Utils.convertToTimestampLocalDateTime(this.timestamp)
    return createdInfo
}

fun CreatedInfoFirebase.toCreatedInfo(user: Member): CreatedInfo {
    return CreatedInfo(
        user,
        Utils.convertTimestampToLocalDateTime(timestamp)
    )
}