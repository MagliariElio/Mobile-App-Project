package it.polito.students.showteamdetails.entity

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.Utils.convertTimestampToLocalDateTime
import it.polito.students.showteamdetails.Utils.convertToTimestampLocalDateTime
import java.time.LocalDateTime

data class Comment(
    val comment: String,
    val createdBy: Member,
    val dateCreation: LocalDateTime,
    val dateModification: LocalDateTime,
    var id: String = "",
)

class CommentFirebase {
    lateinit var comment: String
    lateinit var createdBy: DocumentReference
    lateinit var dateCreation: Timestamp
    lateinit var dateModification: Timestamp
    lateinit var id: String
}

fun Comment.toFirebase(): CommentFirebase {
    val comment = CommentFirebase()
    comment.comment = this.comment
    comment.createdBy =
        Firebase.firestore.collection(Utils.CollectionsEnum.users.name).document(this.createdBy.id)
    comment.dateCreation = convertToTimestampLocalDateTime(this.dateCreation)
    comment.dateModification = convertToTimestampLocalDateTime(this.dateModification)
    comment.id = this.id
    return comment
}

fun CommentFirebase.toComment(createdBy: Member): Comment {
    return Comment(
        comment = this.comment,
        createdBy = createdBy,
        dateCreation = convertTimestampToLocalDateTime(this.dateCreation),
        dateModification = convertTimestampToLocalDateTime(this.dateModification),
        id = this.id,
    )
}