package it.polito.students.showteamdetails.model

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.Comment
import it.polito.students.showteamdetails.entity.CommentFirebase
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.toComment
import it.polito.students.showteamdetails.entity.toFirebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CommentModel {
    private val db = Firebase.firestore
    private val commentCollection = db.collection(Utils.CollectionsEnum.comments.name)

    suspend fun addComment(comment: Comment, taskId: String): Comment? {
        return try {
            withContext(Dispatchers.IO) {
                val newComment = commentCollection.document()
                comment.id = newComment.id

                newComment.set(comment.toFirebase()).await()
                Log.d("SUCCESS", "Comment added successfully with id: ${comment.id}")

                val commentReference = commentCollection.document(comment.id)
                TaskModel().addCommentInTask(commentReference, taskId)

                comment
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error adding comment", e)
            null
        }
    }

    suspend fun updateComment(comment: Comment, taskId: String): Comment? {
        return try {
            withContext(Dispatchers.IO) {
                val commentReference = commentCollection.document(comment.id)

                // Verifica se il commento esiste prima di aggiornare
                val snapshot = commentReference.get().await()
                if (snapshot.exists()) {
                    commentReference.set(comment.toFirebase()).await()
                    Log.d("SUCCESS", "Comment updated successfully with id: ${comment.id}")

                    // Aggiorna il riferimento del commento nel task, se necessario
                    TaskModel().addCommentInTask(commentReference, taskId)

                    comment
                } else {
                    // il commento non esiste, quindi si aggiunge
                    addComment(comment, taskId)
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error updating comment", e)
            null
        }
    }


    fun getCommentById(id: String): Flow<Comment?> = callbackFlow {
        val listenerRegistration = commentCollection.document(id)
            .addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    Log.e("ERROR", "Error getting comment", error)
                    trySend(null).isSuccess
                    return@addSnapshotListener
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val commentFirebase = documentSnapshot.toObject(CommentFirebase::class.java)
                    if (commentFirebase != null) {

                        // Launch a coroutine to handle suspending functions
                        val scope = CoroutineScope(Dispatchers.IO)
                        scope.launch {
                            val createdByUser =
                                UserModel().getUserByDocumentId(commentFirebase.createdBy.id)
                                    ?: Utils.memberAccessed.value
                            val comment = commentFirebase.toComment(createdByUser)
                            trySend(comment).isSuccess
                        }
                    } else {
                        trySend(null).isSuccess
                    }
                } else {
                    trySend(null).isSuccess
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    suspend fun getCommentsFromReferences(
        references: List<DocumentReference>,
        usersList: List<Member>
    ): List<Comment> {
        val comments = mutableListOf<Comment>()

        for (ref in references) {
            try {
                val documentSnapshot = ref.get().await()
                if (documentSnapshot.exists()) {
                    val comment = documentSnapshot.toObject<CommentFirebase>()
                    if (comment != null) {
                        val createBy = usersList.find { it.id == comment.createdBy.id }
                            ?: Utils.memberAccessed.value
                        comments.add(comment.toComment(createBy))
                    }
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Error getting comment", e)
            }
        }

        return comments.toList()
    }

    fun deleteCommentTransaction(transaction: Transaction, id: String) {
        val commenteReference = commentCollection.document(id)
        transaction.delete(commenteReference)
    }

    suspend fun deleteCommentById(id: String): Boolean {
        try {
            db.runTransaction { transaction ->
                deleteCommentTransaction(transaction, id)
            }.await()
            Log.d("SUCCESS", "Comment deleted successfully with id: $id")
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "Error deleting comment with id: $id", e)
            return false
        }
    }
}