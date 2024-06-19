package it.polito.students.showteamdetails.entity

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.model.MemberInfoTeamModel

data class MemberInfoTeam(
    val profile: Member,
    var role: RoleEnum,
    var participationType: Utils.TimePartecipationTeamTypes,
    var id: String = "1",
)

class MemberInfoTeamFirebase {
    lateinit var id: String
    lateinit var profile: DocumentReference
    lateinit var role: RoleEnum
    lateinit var participationType: Utils.TimePartecipationTeamTypes
}

fun MemberInfoTeamFirebase.toMemberInfoTeam(
    idMemberIfoTeamFirebase: String,
    profileMember: Member
): MemberInfoTeam {
    return MemberInfoTeam(
        id = idMemberIfoTeamFirebase,
        profile = profileMember,
        role = this.role,
        participationType = this.participationType
    )
}

fun MemberInfoTeam.toMemberInfoTeamFirebase(): MemberInfoTeamFirebase {
    val profile =
        Firebase.firestore.collection(Utils.CollectionsEnum.users.name).document(this.profile.id)
    val memberInfoTeam = MemberInfoTeamFirebase()
    memberInfoTeam.id = this.id
    memberInfoTeam.profile = profile
    memberInfoTeam.role = this.role
    memberInfoTeam.participationType = this.participationType
    return memberInfoTeam
}

fun MemberInfoTeam.toMemberInfoTeamFirebaseDocumentReference(): DocumentReference {
    return Firebase.firestore.collection(Utils.CollectionsEnum.memberInfoTeam.name)
        .document(this.id)
}

fun convertMemberInfoTeamListToMemberInfoTeamFirebaseList(memberInfoTeamList: List<MemberInfoTeam>): List<DocumentReference> {
    return memberInfoTeamList.map { it.toMemberInfoTeamFirebaseDocumentReference() }
}

suspend fun convertMemberInfoTeamFirebaseListToMemberInfoTeamList(memberInfoTeamList: List<DocumentReference>): List<MemberInfoTeam> {
    return memberInfoTeamList.mapNotNull {
        MemberInfoTeamModel().getMemberInfoTeamById(it.id)
    }
}