package it.polito.students.showteamdetails.entity

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.Utils.convertTimestampToLocalDateTime
import it.polito.students.showteamdetails.model.UserModel
import java.time.LocalDateTime

data class Review(
    val message: String,
    val createdBy: Member,
    val dateCreation: LocalDateTime,
    val dateModification: LocalDateTime?
)

class FirebaseReview {
    lateinit var message: String
    lateinit var createdBy: DocumentReference
    lateinit var dateCreation: Timestamp
    var dateModification: Timestamp? = null
}

suspend fun FirebaseReview.toReview(): Review {
    return Review(
        message = this.message,
        createdBy = UserModel().getUserByDocumentId(this.createdBy.id)
            ?: Utils.memberAccessed.value,
        dateCreation = convertTimestampToLocalDateTime(this.dateCreation),
        dateModification = if (this.dateModification == null) null else {
            convertTimestampToLocalDateTime(this.dateModification!!)
        }
    )
}

fun Review.toFirebaseReview(): FirebaseReview {
    val firestoreReview = FirebaseReview()
    firestoreReview.message = this.message
    firestoreReview.createdBy =
        Firebase.firestore.collection(Utils.CollectionsEnum.users.name).document(this.createdBy.id)
    firestoreReview.dateCreation = Utils.convertToTimestampLocalDateTime(this.dateCreation)
    if (this.dateModification != null) {
        firestoreReview.dateModification =
            Utils.convertToTimestampLocalDateTime(this.dateModification)
    }
    return firestoreReview
}

fun convertReviewListToFirebaseList(reviews: List<Review>): List<FirebaseReview> {
    return reviews.map { it.toFirebaseReview() }
}
