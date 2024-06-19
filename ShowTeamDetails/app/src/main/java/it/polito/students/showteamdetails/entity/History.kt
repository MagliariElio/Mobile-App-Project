package it.polito.students.showteamdetails.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.model.MemberInfoTeamModel
import java.time.LocalDateTime

data class History(
    val member: Member,
    val date: LocalDateTime,
    val value: Int,
    val delegatedMember: MemberInfoTeam? = null
)

class HistoryFirebase {
    lateinit var member: DocumentReference
    lateinit var date: Timestamp
    var value: Int = 0
    var delegatedMember: DocumentReference? = null
}

fun History.toFirebase(): HistoryFirebase {
    val fileFirebase = HistoryFirebase()

    fileFirebase.member = Firebase.firestore.collection(Utils.CollectionsEnum.users.name)
        .document(this.member.id)
    fileFirebase.date = Utils.convertToTimestampLocalDateTime(this.date)
    fileFirebase.value = this.value
    fileFirebase.delegatedMember = if (this.delegatedMember == null) {
        null
    } else {
        Firebase.firestore.collection(Utils.CollectionsEnum.memberInfoTeam.name)
            .document(this.delegatedMember.id)
    }

    return fileFirebase
}

fun HistoryFirebase.toHistory(member: Member, delegatedMember: MemberInfoTeam?): History {
    return History(
        member = member,
        date = Utils.convertTimestampToLocalDateTime(this.date),
        value = this.value,
        delegatedMember = delegatedMember,
    )
}

fun convertHistoryListToHistoryFirebaseList(historyList: List<History>): List<HistoryFirebase> {
    return historyList.map { it.toFirebase() }
}

suspend fun convertHistoryFirebaseListToHistoryList(
    historyList: List<HistoryFirebase>,
    usersList: List<Member>
): List<History> {
    return historyList.map { history ->
        val member = usersList.find { it.id == history.member.id } ?: Utils.memberAccessed.value

        val delegatedMember: MemberInfoTeam? =
            if (history.delegatedMember == null) null else MemberInfoTeamModel().getMemberInfoTeamById(
                history.delegatedMember?.id!!
            )
        history.toHistory(member, delegatedMember)
    }
}