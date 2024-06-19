package it.polito.students.showteamdetails.model

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.Chat
import it.polito.students.showteamdetails.entity.FirebaseGroupChat
import it.polito.students.showteamdetails.entity.FirebaseIndividualChat
import it.polito.students.showteamdetails.entity.FirebaseMember
import it.polito.students.showteamdetails.entity.FirebaseMessage
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.Message
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class ChatModel {

    val db = Firebase.firestore

    fun getMyIndividualChats(): Flow<List<Chat>> = callbackFlow {
        val listener = db.collection("individualChats").addSnapshotListener { result, error ->
            if (result != null) {
                val individualChats = mutableListOf<Chat>() // Create a list to hold all chats
                val fetchedMembers = mutableSetOf<Member>() // Keep track of fetched member IDs

                // Launch a coroutine for each chat document
                result.documents.mapNotNull { chatDocument ->
                    CoroutineScope(Dispatchers.IO).async {
                        val firebaseChat = chatDocument.toObject(FirebaseIndividualChat::class.java)
                        firebaseChat?.let { chat ->
                            val messages = mutableListOf<Message>()
                            val senderReferences: List<DocumentReference> = chat.users
                            if (!senderReferences.contains(
                                    db.collection("users").document(Utils.memberAccessed.value.id)
                                )
                            ) return@async null

                            val otherUserIndex = senderReferences.indexOfFirst {
                                it != db.collection("users").document(Utils.memberAccessed.value.id)
                            }

                            // Fetch the other user asynchronously
                            val otherUserDeferred = async {
                                val firebaseMember = senderReferences[otherUserIndex].get().await()
                                    .toObject(FirebaseMember::class.java)
                                firebaseMember?.let {
                                    UserModel().getUserByDocumentReference(
                                        senderReferences[otherUserIndex],
                                        Utils.memberAccessed.value,
                                        fetchedMembers
                                    )
                                }
                            }

                            // Fetch all messages asynchronously
                            val messagesDeferred = chat.messages.map { firebaseMessageRef ->
                                async {
                                    val messageSnapshot = firebaseMessageRef.get().await()
                                    val firebaseMessage =
                                        messageSnapshot.toObject(FirebaseMessage::class.java)
                                    firebaseMessage?.let { message ->
                                        Message(
                                            id = messageSnapshot.id,
                                            text = message.text,
                                            sendDate = message.sendDate.toDate().toInstant()
                                                .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                                            sender = if (message.sender.id == Utils.memberAccessed.value.id) Utils.memberAccessed.value else otherUserDeferred.await()!!,
                                            read = message.read[Utils.memberAccessed.value.id]!!,
                                            edited = message.edited
                                        )
                                    }
                                }
                            }

                            val otherUser = otherUserDeferred.await()
                            val messagesList = messagesDeferred.awaitAll()

                            val chatId = chatDocument.id
                            val lastMessageDate = chat.lastMessageDate?.toDate()?.toInstant()
                                ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                            val chatObject = Chat(
                                id = chatId,
                                lastMessageDate = lastMessageDate,
                                messages = messagesList.filterNotNull().toMutableList(),
                                singleSender = otherUser!!
                            )
                            individualChats.add(chatObject)
                        }
                    }
                }.let { jobs ->
                    // Wait for all coroutines to complete
                    CoroutineScope(Dispatchers.IO).launch {
                        jobs.awaitAll()
                        // Send the result to the callbackFlow
                        trySend(individualChats)
                    }
                }
            } else {
                Log.e("ERROR", error.toString())
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    /*
        suspend fun getMyGroupChats(): Flow<List<Chat>> = callbackFlow {
            val fetchedUsers: MutableSet<Member> = mutableSetOf()
            val scope = this

            val job = scope.launch {
                try {
                    TeamModel().getAllTeamsByMemberAccessed().collect { teamList ->
                        val teamIds = teamList.map { it.teamField.id }

                        val listenerRegistration =
                            db.collection("groupChats").addSnapshotListener { result, error ->
                                scope.launch {
                                    if (result != null) {
                                        val groupChats = result.documents.mapNotNull { chatSnap ->
                                            val groupChat =
                                                chatSnap.toObject(FirebaseGroupChat::class.java)

                                            if (groupChat != null && teamIds.contains(groupChat.teamId?.id)) {
                                                val messages =
                                                    groupChat.messages.mapNotNull { messageRef ->
                                                        val messageSnap = messageRef.get().await()
                                                        val fireMessage =
                                                            messageSnap.toObject(FirebaseMessage::class.java)

                                                        if (fireMessage != null) {
                                                            Message(
                                                                id = messageSnap.id,
                                                                text = fireMessage.text ?: "",
                                                                sendDate = fireMessage.sendDate.toDate()
                                                                    .let { date ->
                                                                        Instant.ofEpochMilli(date.time)
                                                                            .atZone(ZoneId.systemDefault())
                                                                            .toLocalDateTime()
                                                                    } ?: LocalDateTime.MIN,
                                                                sender = fireMessage.sender.let { senderRef ->
                                                                    UserModel().getUserByDocumentReference(
                                                                        senderRef,
                                                                        Utils.memberAccessed.value,
                                                                        fetchedUsers
                                                                    )
                                                                } ?: Utils.memberAccessed.value,
                                                                read = fireMessage.read[Utils.memberAccessed.value.id]
                                                                    ?: true,
                                                                edited = fireMessage.edited
                                                            )
                                                        } else null
                                                    }.toMutableList()

                                                Chat(
                                                    id = chatSnap.id,
                                                    lastMessageDate = groupChat.lastMessageDate?.toDate()
                                                        ?.let { date ->
                                                            Instant.ofEpochMilli(date.time)
                                                                .atZone(ZoneId.systemDefault())
                                                                .toLocalDateTime()
                                                        } ?: LocalDateTime.MIN,
                                                    messages = messages,
                                                    singleSender = null,
                                                    teamId = groupChat.teamId?.id ?: ""
                                                )
                                            } else null
                                        }

                                        trySend(groupChats).isSuccess
                                    } else {
                                        Log.e("ERROR", error.toString())
                                        trySend(emptyList()).isSuccess
                                    }
                                }
                            }

                        awaitClose { listenerRegistration.remove() }
                    }
                } catch (e: Exception) {
                    Log.e("ERROR", e.toString())
                    trySend(emptyList()).isSuccess
                }
            }

            awaitClose { job.cancel() }
        }  SOSTITUITA CON UNA PIU' ROBUSTA SENZA PUNTI ESCLAMATIVI
    */
    suspend fun getMyGroupChats(): Flow<List<Chat>> = callbackFlow {
        val fetchedUsers: MutableSet<Member> = mutableSetOf()
        val scope = this

        val job = scope.launch {
            try {
                TeamModel().getAllTeamsByMemberAccessed().collect { teamList ->
                    val teamIds = teamList.map { it.teamField.id }

                    val listenerRegistration =
                        db.collection("groupChats").addSnapshotListener { result, error ->
                            scope.launch {
                                if (result != null) {
                                    val groupChats = result.documents.mapNotNull { chatSnap ->
                                        val groupChat =
                                            chatSnap.toObject(FirebaseGroupChat::class.java)

                                        if (groupChat != null && teamIds.contains(groupChat.teamId?.id)) {
                                            val messages =
                                                groupChat.messages.mapNotNull { messageRef ->
                                                    val messageSnap = messageRef.get().await()
                                                    val fireMessage =
                                                        messageSnap.toObject(FirebaseMessage::class.java)

                                                    if (fireMessage != null) {
                                                        Message(
                                                            id = messageSnap.id,
                                                            text = fireMessage.text,
                                                            sendDate = fireMessage.sendDate.toDate()
                                                                .let { date ->
                                                                    Instant.ofEpochMilli(date.time)
                                                                        .atZone(ZoneId.systemDefault())
                                                                        .toLocalDateTime()
                                                                } ?: LocalDateTime.MIN,
                                                            sender = fireMessage.sender.let { senderRef ->
                                                                UserModel().getUserByDocumentReference(
                                                                    senderRef,
                                                                    Utils.memberAccessed.value,
                                                                    fetchedUsers
                                                                )
                                                            },
                                                            read = fireMessage.read[Utils.memberAccessed.value.id]
                                                                ?: true,
                                                            edited = fireMessage.edited
                                                        )
                                                    } else null
                                                }.toMutableList()

                                            Chat(
                                                id = chatSnap.id,
                                                lastMessageDate = groupChat.lastMessageDate?.toDate()
                                                    ?.let { date ->
                                                        Instant.ofEpochMilli(date.time)
                                                            .atZone(ZoneId.systemDefault())
                                                            .toLocalDateTime()
                                                    } ?: LocalDateTime.MIN,
                                                messages = messages,
                                                singleSender = null,
                                                teamId = groupChat.teamId?.id ?: ""
                                            )
                                        } else null
                                    }

                                    trySend(groupChats).isSuccess
                                } else {
                                    Log.e("ERROR", error.toString())
                                    trySend(emptyList()).isSuccess
                                }
                            }
                        }

                    awaitClose { listenerRegistration.remove() }
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.toString())
                trySend(emptyList()).isSuccess
            }
        }

        awaitClose { job.cancel() }
    }


    suspend fun sendNewMessage(chat: Chat, newMessage: Message) {
        val messagesCollectionRef = db.collection("messages")
        val chatCollectionRef =
            if (chat.teamId != null) db.collection("groupChats") else db.collection("individualChats")
        val chatDocumentRef = chatCollectionRef.document(chat.id)
        val readMap: MutableMap<String, Boolean> = mutableMapOf()

        if (chat.teamId != null) {

            val teamViewModel = TeamModel().getTeamById(chat.teamId!!)

            teamViewModel!!.teamField.membersField.members.map { it.profile }.forEach { member ->
                readMap[member.id] = (member.id == Utils.memberAccessed.value.id)
            }

        } else {
            readMap[Utils.memberAccessed.value.id] = true
            readMap[chat.singleSender!!.id] = false
        }

        val firebaseMessage = FirebaseMessage().apply {
            text = newMessage.text
            sendDate =
                Timestamp(Date.from(newMessage.sendDate.atZone(ZoneId.systemDefault()).toInstant()))
            sender = db.collection("users").document(Utils.memberAccessed.value.id)
            edited = false
            read = readMap
        }

        // Add the new message to the messages collection
        messagesCollectionRef.add(firebaseMessage).addOnSuccessListener { documentReference ->
            val newMessageId = documentReference.id

            // Update the chat's messages array to include the new message's document ID
            chatDocumentRef.update("messages", FieldValue.arrayUnion(documentReference))
                .addOnSuccessListener {
                    Log.d("SUCCESS", "Message added successfully with id: $newMessageId")
                }
                .addOnFailureListener { e ->
                    Log.e("ERROR", "Error updating chat messages array", e)
                }
            chatDocumentRef.update(
                "lastMessageDate",
                Timestamp(Date.from(newMessage.sendDate.atZone(ZoneId.systemDefault()).toInstant()))
            )
                .addOnSuccessListener {
                    Log.d("SUCCESS", "Last message date updated correctly")
                }
                .addOnFailureListener { e ->
                    Log.e("ERROR", "Error updating chat lastSentDate", e)
                }
        }.addOnFailureListener { e ->
            Log.e("ERROR", "Error adding new message", e)
        }
    }

    fun readMessages(messages: List<Message>) = runBlocking {
        val messagesCollectionRef = db.collection("messages")

        messages.forEach { message ->
            val chatDocumentRef = messagesCollectionRef.document(message.id)
            val documentSnap = chatDocumentRef.get().await()

            val readMap = documentSnap.get("read") as MutableMap<String, Boolean>? ?: mutableMapOf()
            readMap[Utils.memberAccessed.value.id] = true

            chatDocumentRef.update("read", readMap).await()
        }
    }

    fun editMessage(newMessage: Message, newText: String) = runBlocking {
        val messagesCollectionRef = db.collection("messages")
        val messageDocumentRef = messagesCollectionRef.document(newMessage.id)

        val updates = mapOf(
            "text" to newText,
            "edited" to true
        )

        messageDocumentRef.update(updates).await()
    }

    fun deleteMessage(chat: Chat, messageId: String) = runBlocking {
        val messagesCollectionRef = db.collection("messages")
        val chatsCollectionRef =
            if (chat.teamId != null) db.collection("groupChats") else db.collection("individualChats")

        val messageDocumentRef = messagesCollectionRef.document(messageId)
        val chatDocumentRef = chatsCollectionRef.document(chat.id)

        db.runTransaction { transaction ->
            val chatDocument = transaction.get(chatDocumentRef)

            val messages = chatDocument.get("messages") as MutableList<DocumentReference>
            messages.remove(messageDocumentRef)

            transaction.update(chatDocumentRef, "messages", messages)
            transaction.delete(messageDocumentRef)
        }.await()
    }


    suspend fun addNewIndividualChat(individualChat: Chat): String {
        val newChatRef = db.collection("individualChats").document()
        individualChat.id = newChatRef.id

        val firebaseIndividualChat = FirebaseIndividualChat(
            lastMessageDate = null,
            messages = listOf(),
            users = listOf(
                db.collection("users").document(Utils.memberAccessed.value.id),
                db.collection("users").document(individualChat.singleSender!!.id)
            )
        )

        val deferredResult = CompletableDeferred<String>()

        newChatRef.set(firebaseIndividualChat)
            .addOnSuccessListener {
                Log.d("SUCCESS", "Chat added successfully with id: ${individualChat.id}")
                deferredResult.complete(individualChat.id)
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error adding chat", e)
                deferredResult.completeExceptionally(e)
            }

        return deferredResult.await()
    }


    suspend fun addNewGroupChat(teamId: String): String {
        val newChatRef = db.collection("groupChats").document()
        val newGroupChatId = newChatRef.id

        val firebaseGroupChat = FirebaseGroupChat(
            lastMessageDate = null,
            messages = listOf(),
            teamId = db.collection("teams").document(teamId),
        )

        val deferredResult = CompletableDeferred<String>()

        newChatRef.set(firebaseGroupChat)
            .addOnSuccessListener {
                Log.d("SUCCESS", "Chat added successfully with id: ${newGroupChatId}")
                deferredResult.complete(newGroupChatId)
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error adding chat", e)
                deferredResult.completeExceptionally(e)
            }

        return deferredResult.await()
    }


}