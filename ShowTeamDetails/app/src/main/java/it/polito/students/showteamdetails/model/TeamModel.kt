package it.polito.students.showteamdetails.model

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.students.showteamdetails.Fixture.RouterActionsProvider.routerActions
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.MemberInfoTeam
import it.polito.students.showteamdetails.entity.Task
import it.polito.students.showteamdetails.entity.Team
import it.polito.students.showteamdetails.entity.TeamFirebase
import it.polito.students.showteamdetails.entity.toFirebase
import it.polito.students.showteamdetails.entity.toTaskFirebase
import it.polito.students.showteamdetails.entity.toTeam
import it.polito.students.showteamdetails.viewmodel.TeamViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class TeamModel(
    val memberInfoTeamModel: MemberInfoTeamModel = MemberInfoTeamModel(),
    val taskModel: TaskModel = TaskModel()
) {
    val db = Firebase.firestore
    private val teamCollection = db.collection(Utils.CollectionsEnum.teams.name)
    private val taskCollection = db.collection(Utils.CollectionsEnum.tasks.name)
    private val userCollection = db.collection(Utils.CollectionsEnum.users.name)

    suspend fun addTeam(team: Team): Team? {
        return try {
            withContext(Dispatchers.IO) {
                // Collect member info asynchronously without blocking
                val updatedMembersList = team.membersList.map { memberInfoTeam ->
                    async(Dispatchers.IO) {
                        memberInfoTeamModel.addMemberInfoTeam(memberInfoTeam)
                    }
                }.awaitAll().filterNotNull()

                // If all members were added successfully, proceed with the transaction
                if (updatedMembersList.size == team.membersList.size) {
                    team.membersList = updatedMembersList
                    db.runTransaction { transaction ->
                        val newTeamRef = teamCollection.document()
                        team.id = newTeamRef.id

                        team.created.member = Utils.memberAccessed.value
                        team.created.timestamp = LocalDateTime.now()

                        async {
                            ChatModel().addNewGroupChat(team.id)
                        }
                        // Set the team document within the transaction
                        transaction.set(newTeamRef, team.toFirebase())
                        Log.d("SUCCESS", "Team added successfully with id: ${team.id}")

                        team
                    }.await()
                } else {
                    Log.e("ERROR", "Error adding some members")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error adding team", e)
            null
        }
    }


    suspend fun getTeamById(teamId: String): TeamViewModel? {
        return try {
            withContext(Dispatchers.IO) {
                val documentSnapshot = teamCollection.document(teamId).get().await()
                val usersList = UserModel().getAllUsersList()

                if (documentSnapshot.exists()) {
                    val teamFirebase = documentSnapshot.toObject(TeamFirebase::class.java)
                    val team = teamFirebase?.toTeam(usersList)

                    team?.let { TeamViewModel(it, routerActions) }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error getting team by ID", e)
            null
        }
    }

    suspend fun getAllTeamsByMemberAccessed(): Flow<MutableList<TeamViewModel>> = callbackFlow {
        val usersList = UserModel().getAllUsersList()

        // Initial fetch of data
        val initialTeams = teamCollection.get().await().documents.mapNotNull { documentSnapshot ->
            val teamFirebase = documentSnapshot.toObject(TeamFirebase::class.java)
            teamFirebase?.toTeam(usersList)
        }.filter {
            it.membersList.any { memberInfo -> memberInfo.profile.id == Utils.memberAccessed.value.id }
        }.map { TeamViewModel(it, routerActions) }.toMutableList()

        // Send initial data
        trySend(initialTeams).isSuccess

        // Firestore listener setup for real-time updates
        val listener = teamCollection.addSnapshotListener { result, error ->
            if (error != null) {
                Log.e("ERROR", error.toString())
                trySend(mutableListOf()).isSuccess
                return@addSnapshotListener
            }

            launch {
                if (result != null && !result.isEmpty) {
                    val teams = result.documents.mapNotNull { documentSnapshot ->
                        val teamFirebase = documentSnapshot.toObject(TeamFirebase::class.java)
                        teamFirebase?.toTeam(usersList)
                    }

                    // Filter teams based on the specified criteria
                    val filteredTeams = teams.filter {
                        it.membersList.any { memberInfo -> memberInfo.profile.id == Utils.memberAccessed.value.id }
                    }

                    // Map filtered teams to TeamViewModel
                    val teamViewModels =
                        filteredTeams.map { TeamViewModel(it, routerActions) }.toMutableList()

                    trySend(teamViewModels).isSuccess
                } else {
                    trySend(mutableListOf()).isSuccess
                }
            }
        }

        // Close the listener when the flow collection is cancelled
        awaitClose { listener.remove() }
    }

    suspend fun addTask(task: Task, teamId: String): Task? {
        return try {
            withContext(Dispatchers.IO) {
                // Eseguo una transaction così in caso di errore posso fare un rollback delle operazioni
                FirebaseFirestore.getInstance().runTransaction { transaction ->
                    // Ottieni un nuovo documento nella tasks collection
                    val newTaskRef = taskCollection.document()
                    task.id = newTaskRef.id

                    val teamIdReference =
                        Firebase.firestore.collection(Utils.CollectionsEnum.teams.name)
                            .document(teamId)
                    val taskFirebase = task.toTaskFirebase(teamIdReference)

                    taskFirebase.commentList = emptyList() // default il task viene creato senza commenti

                    // Aggiungi il task nella tasks collection
                    transaction.set(newTaskRef, taskFirebase)

                    /*// Ottieni il riferimento al documento del team
                    val teamDocumentRef = teamCollection.document(teamId)

                    // Aggiorna la tasksList del team aggiungendo il riferimento al nuovo task
                    transaction.update(
                        teamDocumentRef,
                        "tasksList",
                        FieldValue.arrayUnion(newTaskRef)
                    )*/
                    Log.d("SUCCESS", "Task added successfully with id: ${task.id}")

                    task
                }.await()
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error adding task", e)
            null
        }
    }

    suspend fun updateTeam(team: Team): Boolean {
        try {
            withContext(Dispatchers.IO) {
                if (team.picture != null) {
                    UserModel().storeImage(team.picture, "team_${team.id}.jpg")
                }
                teamCollection.document(team.id)
                    .set(team.toFirebase()).await()
                Log.d("SUCCESS", "Team updated successfully in task with id: ${team.id}")
            }
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "Error updating team", e)
            return false
        }
    }

    suspend fun deleteTeam(team: Team) {
        val documentTeam = teamCollection.document(team.id)

        try {
            val tasksList = taskModel.getAllTasksByTeamId(team.id).single()
            ChatModel().deleteGroupChatByTeamId(team.id)

            db.runTransaction { transaction ->
                // elimina tutti i task associati
                tasksList.forEach { task ->
                    taskModel.deleteTaskTransaction(transaction, task.getTask())
                }

                // elimina tutti i membri associati del team
                team.membersList.forEach {
                    memberInfoTeamModel.deleteMemberInfoTeamById(transaction, it.id)
                }

                transaction.delete(documentTeam)
            }.await()
            Log.d("SUCCESS", "The team with id ${team.id} is deleted successfully.")
        } catch (e: Exception) {
            Log.e("ERROR", "Error deleting team with id ${team.id}", e)
        }
    }

    // TODO: da testare, forse serve un aggiornamento diretto al task
    suspend fun leaveTeam(member: MemberInfoTeam, team: Team): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val tasksList = taskModel.getAllTasksByTeamId(team.id).single()

                db.runTransaction { transaction ->
                    // Update task history and remove member from delegated tasks
                    tasksList.forEach { task ->
                        task.createNewHistoryLine(LocalDateTime.now(), R.string.left_team)
                        task.delegateTasksField.deleteMemberSynch(member)
                    }

                    // Delete all member info associated with the team
                    team.membersList.forEach { memberInfo ->
                        memberInfoTeamModel.deleteMemberInfoTeamById(transaction, memberInfo.id)
                    }

                    // Remove member from team members list
                    team.membersList = team.membersList.filter { it.id != member.id }
                }.await()

                tasksList.forEach { task ->
                    taskModel.updateTask(task.getTask(), team.id)
                }

                // Update team document
                updateTeam(team)

                Log.d(
                    "SUCCESS",
                    "The member with id ${member.id} left the team with id ${team.id} successfully."
                )

                if (team.membersList.isEmpty()) {
                    deleteTeam(team)
                    Log.d(
                        "SUCCESS",
                        "The team with id ${member.id} was deleted successfully because there are no member anymore."
                    )
                }
            }
            true
        } catch (e: Exception) {
            Log.e(
                "ERROR",
                "Error removing member with id ${member.id} from team with id ${team.id}",
                e
            )
            false
        }
    }

    /**
     * @return:
     *  - -1: Member request already exists
     *  - 0: Error adding new member request
     *  - 1: Member request added successfully
     */
    suspend fun addTeamJoinRequests(teamVm: TeamViewModel, memberId: String): Int {
        if (teamVm.teamField.requestsTeamField.requests.any { it.id == memberId }) {
            return -1
        }
        try {
            withContext(Dispatchers.IO) {
                val teamId = teamVm.teamField.id
                teamCollection.document(teamId)
                    .update(
                        "requestsList",
                        FieldValue.arrayUnion(userCollection.document(memberId))
                    ).await()
                Log.d("SUCCESS", "Member request added successfully in team with id: $teamId")
            }
            return 1
        } catch (e: Exception) {
            Log.e("ERROR", "Error adding new member request", e)
            return 0
        }
    }


}