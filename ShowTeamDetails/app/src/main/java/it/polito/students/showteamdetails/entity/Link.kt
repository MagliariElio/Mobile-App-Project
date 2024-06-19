package it.polito.students.showteamdetails.entity

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.Utils.convertTimestampToLocalDateTime
import it.polito.students.showteamdetails.Utils.convertToTimestampLocalDateTime
import java.time.LocalDateTime

data class Link(
    val link: String,
    val uploadedBy: Member,
    val dateUpload: LocalDateTime,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Link

        if (link != other.link) return false
        if (uploadedBy != other.uploadedBy) return false
        if (dateUpload != other.dateUpload) return false

        return true
    }

    override fun hashCode(): Int {
        var result = link.hashCode()
        result = 31 * result + uploadedBy.hashCode()
        result = 31 * result + dateUpload.hashCode()
        return result
    }
}

class LinkFirebase {
    lateinit var link: String
    lateinit var uploadedBy: DocumentReference
    lateinit var dateUpload: Timestamp
}

fun Link.toFirebase(): LinkFirebase {
    val linkFirebase = LinkFirebase()
    linkFirebase.link = link
    linkFirebase.uploadedBy =
        Firebase.firestore.collection(Utils.CollectionsEnum.users.name).document(uploadedBy.id)
    linkFirebase.dateUpload = convertToTimestampLocalDateTime(dateUpload)
    return linkFirebase
}

fun LinkFirebase.toLink(uploadedBy: Member): Link {
    return Link(
        link = this.link,
        uploadedBy = uploadedBy,
        dateUpload = convertTimestampToLocalDateTime(this.dateUpload)
    )
}

fun convertLinkListToLinkFirebaseList(linkList: List<Link>): List<LinkFirebase> {
    return linkList.map { it.toFirebase() }
}

fun convertLinkFirebaseListToLinkList(
    linkList: List<LinkFirebase>,
    usersList: List<Member>
): List<Link> {
    return linkList.map { link ->
        val uploadedBy =
            usersList.find { it.id == link.uploadedBy.id } ?: Utils.memberAccessed.value
        link.toLink(uploadedBy)
    }
}
