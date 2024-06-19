package it.polito.students.showteamdetails.model

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.students.showteamdetails.Fixture.RouterActionsProvider
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.File
import it.polito.students.showteamdetails.entity.History
import it.polito.students.showteamdetails.entity.Link
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.Task
import it.polito.students.showteamdetails.entity.TaskFirebase
import it.polito.students.showteamdetails.entity.toFileFirebase
import it.polito.students.showteamdetails.entity.toFirebase
import it.polito.students.showteamdetails.entity.toTask
import it.polito.students.showteamdetails.entity.toTaskFirebase
import it.polito.students.showteamdetails.viewmodel.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TaskModel(val commentModel: CommentModel = CommentModel()) {
    private val db = Firebase.firestore
    private val taskCollection = db.collection(Utils.CollectionsEnum.tasks.name)

    suspend fun getTaskById(taskId: String, usersList: List<Member>): Task? { //= callbackFlow {
        val documentSnapshot = taskCollection.document(taskId).get().await()
        try {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                val taskFirebase = documentSnapshot.toObject(TaskFirebase::class.java)

                return taskFirebase?.toTask(usersList)
            } else {
                return null
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error getting task", e)
            return null
        }

    }

    suspend fun getAllTasksByTeamId(teamId: String): Flow<MutableList<TaskViewModel>> = callbackFlow {
        val usersList = UserModel().getAllUsersList()

        val teamIdReference = Firebase.firestore.collection(Utils.CollectionsEnum.teams.name).document(teamId)
        val querySnapshot = taskCollection.whereEqualTo("teamId", teamIdReference).get().await()

        val tasks = mutableListOf<TaskViewModel>()

        val job = launch {
            querySnapshot.documents.forEach { document ->
                try {
                    val taskFirebase = document.toObject(TaskFirebase::class.java)
                    taskFirebase?.let {
                        val task = it.toTask(usersList)
                        val taskViewModel = TaskViewModel(
                            task = task,
                            routerActions = RouterActionsProvider.provideRouterActions()
                        )
                        tasks.add(taskViewModel)
                    }
                } catch (e: Exception) {
                    Log.e("ERROR", "Error getting tasks", e)
                }
            }
            trySend(tasks.toMutableList()).isSuccess
            close()
        }

        awaitClose { job.cancel() }
    }

    suspend fun getNumberCompletedTasksByUser(userId: String): Int {
        val createdRef = Firebase.firestore.collection(Utils.CollectionsEnum.users.name).document(userId)
        val querySnapshot = taskCollection
            .whereEqualTo("status", Utils.StatusEnum.DONE)
            .whereEqualTo("created.member", createdRef)
            .get()
            .await()

        val delegateRef = Firebase.firestore.collection(Utils.CollectionsEnum.memberInfoTeam.name).document(userId)
        val delegatedQuerySnapshot = taskCollection
            .whereEqualTo("status", Utils.StatusEnum.DONE)
            .whereArrayContains("delegateList", delegateRef)
            .get()
            .await()

        return querySnapshot.size() + delegatedQuerySnapshot.size()
    }

    suspend fun getNumberAssignedTasksByUser(userId: String): Int {
        val createdRef = Firebase.firestore.collection(Utils.CollectionsEnum.users.name).document(userId)
        val createdByUserSnapshot = taskCollection
            .whereEqualTo("created.member", createdRef)
            .get()
            .await()

        val delegateRef = Firebase.firestore.collection(Utils.CollectionsEnum.memberInfoTeam.name).document(userId)
        val delegatedToUserSnapshot = taskCollection
            .whereArrayContains("delegateList", delegateRef)
            .get()
            .await()

        return createdByUserSnapshot.size() + delegatedToUserSnapshot.size()
    }


    suspend fun addCommentInTask(commentReference: DocumentReference, taskId: String) {
        try {
            withContext(Dispatchers.IO) {
                taskCollection.document(taskId)
                    .update("commentList", FieldValue.arrayUnion(commentReference))
                Log.d("SUCCESS", "Comment added successfully in task with id: $taskId")
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error adding comment", e)
        }
    }

    suspend fun addFile(file: File, taskId: String): Boolean {
        try {
            withContext(Dispatchers.IO) {
                val fileFirebase = file.toFileFirebase()
                taskCollection.document(taskId)
                    .update("fileList", FieldValue.arrayUnion(fileFirebase)).await()
                Log.d("SUCCESS", "File added successfully in task with id: $taskId")
            }
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "Error adding file", e)
            return false
        }
    }

    suspend fun deleteFile(file: File, taskId: String): Boolean {
        val taskDocument = taskCollection.document(taskId)
        val fileToRemove = file.toFileFirebase()

        try {
            taskDocument.update("fileList", FieldValue.arrayRemove(fileToRemove)).await()
            Log.d("SUCCESS", "File removed successfully from task with id: $taskId")
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "Error removing file from task", e)
            return false
        }
    }

    suspend fun addLink(link: Link, taskId: String): Boolean {
        try {
            withContext(Dispatchers.IO) {
                val linkFirebase = link.toFirebase()
                taskCollection.document(taskId)
                    .update("linkList", FieldValue.arrayUnion(linkFirebase)).await()
                Log.d("SUCCESS", "Link added successfully in task with id: $taskId")
            }
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "Error adding link", e)
            return false

        }
    }

    suspend fun deleteLink(link: Link, taskId: String): Boolean {
        val taskDocument = taskCollection.document(taskId)
        val linkToRemove = link.toFirebase()

        try {
            taskDocument.update("linkList", FieldValue.arrayRemove(linkToRemove)).await()
            Log.d("SUCCESS", "Link removed successfully from task with id: $taskId")
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "Error removing link from task", e)
            return false

        }
    }

    suspend fun addHistory(history: History, taskId: String): Boolean {
        try {
            withContext(Dispatchers.IO) {
                val historyFirebase = history.toFirebase()
                taskCollection.document(taskId)
                    .update("historyList", FieldValue.arrayUnion(historyFirebase)).await()
                Log.d("SUCCESS", "History added successfully in task with id: $taskId")
            }
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "Error adding history", e)
            return false
        }
    }

    suspend fun updateTask(task: Task, teamId: String) {
        try {
            withContext(Dispatchers.IO) {
                val teamIdReference =
                    Firebase.firestore.collection(Utils.CollectionsEnum.teams.name).document(teamId)
                taskCollection.document(task.id)
                    .set(task.toTaskFirebase(teamIdReference)).await()
                Log.d("SUCCESS", "Task updated successfully in task with id: ${task.id}")
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error updating task", e)
        }
    }

    suspend fun updateStatus(status: Utils.StatusEnum, taskId: String) {
        try {
            withContext(Dispatchers.IO) {
                taskCollection.document(taskId)
                    .update("status", status).await()
                Log.d("SUCCESS", "Status updated successfully in task with id: $taskId")
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error updating status", e)
        }
    }

    fun deleteTaskTransaction(transaction: Transaction, task: Task) {
        val documentTask = taskCollection.document(task.id)

        // elimina tutti i commenti del task
        task.commentList.forEach { comment ->
            commentModel.deleteCommentTransaction(transaction, comment.id)
        }

        transaction.delete(documentTask)
    }

    suspend fun deleteTask(task: Task): Boolean {
        try {
            db.runTransaction { transaction ->
                deleteTaskTransaction(transaction, task)
            }.await()
            Log.d("SUCCESS", "Task deleted successfully with id: ${task.id}")
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "Error deleting task with id: ${task.id}", e)
            return false
        }
    }

}