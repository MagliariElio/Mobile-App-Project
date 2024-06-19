package it.polito.students.showteamdetails.model

import android.util.Log
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.MemberInfoTeam
import it.polito.students.showteamdetails.entity.MemberInfoTeamFirebase
import it.polito.students.showteamdetails.entity.RoleEnum
import it.polito.students.showteamdetails.entity.toFirebase
import it.polito.students.showteamdetails.entity.toMemberInfoTeam
import it.polito.students.showteamdetails.entity.toMemberInfoTeamFirebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MemberInfoTeamModel {
    private val db = Firebase.firestore
    private val memberInfoTeamCollection = db.collection(Utils.CollectionsEnum.memberInfoTeam.name)

    suspend fun addMemberInfoTeam(memberInfoTeam: MemberInfoTeam): MemberInfoTeam? {
        return try {
            withContext(Dispatchers.IO) {
                val newMemberInfoTeam = memberInfoTeamCollection.document()
                memberInfoTeam.id = newMemberInfoTeam.id

                newMemberInfoTeam.set(memberInfoTeam.toMemberInfoTeamFirebase()).await()
                Log.d(
                    "SUCCESS",
                    "Member Info Team added successfully with id: ${memberInfoTeam.id}"
                )
                memberInfoTeam
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error adding Member Info Team", e)
            null
        }
    }

    suspend fun getMemberInfoTeamById(id: String): MemberInfoTeam? {
        val documentSnapshot = memberInfoTeamCollection.document(id).get().await()
        try {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val memberInfoTeam =
                    documentSnapshot.toObject(MemberInfoTeamFirebase::class.java)

                val member = UserModel().getUserByDocumentId(memberInfoTeam?.profile?.id!!)

                return memberInfoTeam.toMemberInfoTeam(id, member!!)
            } else {
                return null
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error getting memberInfoTeam", e)
            return null
        }
    }

    fun deleteMemberInfoTeamById(transaction: Transaction, id: String) {
        val documentMember = memberInfoTeamCollection.document(id)
        transaction.delete(documentMember)
    }

    fun addMemberInfoTeamTransaction(
        transaction: Transaction,
        memberInfoTeam: MemberInfoTeam
    ): MemberInfoTeam? {
        val newMemberInfoTeamDocRef = memberInfoTeamCollection.document()
        memberInfoTeam.id = newMemberInfoTeamDocRef.id

        return try {
            transaction.set(newMemberInfoTeamDocRef, memberInfoTeam.toMemberInfoTeamFirebase())
            memberInfoTeam
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateMemberInfoTeamRole(memberInfoTeamId: String, role: RoleEnum) {
        try {
            withContext(Dispatchers.IO) {
                memberInfoTeamCollection.document(memberInfoTeamId)
                    .update("role", role).await()
                Log.d("SUCCESS", "Member Info Team updated successfully with id: $memberInfoTeamId")
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error updating memberInfoTeam", e)
        }
    }

    suspend fun updateMemberInfoTeamTimePartecipation(memberInfoTeamId: String, timePartecipation: Utils.TimePartecipationTeamTypes) {
        try {
            withContext(Dispatchers.IO) {
                memberInfoTeamCollection.document(memberInfoTeamId)
                    .update("participationType", timePartecipation).await()
                Log.d("SUCCESS", "Member Info Team updated successfully with id: $memberInfoTeamId")
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error updating memberInfoTeam", e)
        }
    }

}