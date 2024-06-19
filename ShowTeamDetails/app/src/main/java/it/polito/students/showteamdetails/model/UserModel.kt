package it.polito.students.showteamdetails.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.FirebaseMember
import it.polito.students.showteamdetails.entity.FirebaseReview
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

class UserModel {
    val db = Firebase.firestore
    val userCollection = db.collection(Utils.CollectionsEnum.users.name)

    fun getNeighborUsers(loggedUser: Member = Utils.memberAccessed.value): Flow<List<Member>> =
        callbackFlow {
            val scope = this
            val job = scope.launch {
                TeamModel().getAllTeamsByMemberAccessed().collect { teamList ->
                    val neighborUsers = teamList
                        .flatMap { team -> team.teamField.membersField.members.map { member -> member.profile } }
                        .distinct()
                    trySend(neighborUsers).isSuccess
                }
            }

            awaitClose { job.cancel() }
        }


    suspend fun loadPhotoFromStorage(imgUrl: String): ImageBitmap? {
        return try {
            //Firebase.auth.signInAnonymously().await()

            // Scarica l'immagine utilizzando l'URL
            val storageRef = Firebase.storage.reference.child(imgUrl)
            val bytes = storageRef.getBytes(Long.MAX_VALUE).await()
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            bitmap.asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun storeImage(imageBitmap: ImageBitmap, imageName: String) {
        try {
            val bitmap = imageBitmap.asAndroidBitmap()
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 1, baos)
            val imageData = baos.toByteArray()

            // Upload to Firebase Storage
            val storageRef = FirebaseStorage.getInstance().reference.child(imageName)
            storageRef.putBytes(imageData).await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllUsers(loggedUser: Member = Utils.memberAccessed.value): Flow<List<Member>> =
        callbackFlow {
            val listener = db.collection("users").addSnapshotListener { result, error ->
                if (result != null) {
                    val fetchedUsers: MutableSet<Member> = mutableSetOf()
                    val users = result.documents.mapNotNull { document ->
                        val firebaseMember = document.toObject(FirebaseMember::class.java)
                        firebaseMember?.let { firebaseMember ->
                            async {
                                var imageBitmap: ImageBitmap? = null
                                if (firebaseMember.imageUrl.isNotBlank()) {
                                    imageBitmap = loadPhotoFromStorage(firebaseMember.imageUrl)
                                }

                                Member(
                                    id = document.id,
                                    photo = imageBitmap,
                                    name = firebaseMember.name,
                                    surname = firebaseMember.surname,
                                    email = firebaseMember.email,
                                    online = firebaseMember.online,
                                    password = "", // Don't store the password of the member inside the device
                                    gender = Utils.Gender.valueOf(firebaseMember.gender),
                                    birthdate = firebaseMember.birthdate.toDate().toInstant()
                                        .atZone(ZoneId.systemDefault()).toLocalDate(),
                                    nickname = firebaseMember.nickname,
                                    bio = firebaseMember.bio,
                                    address = firebaseMember.address,
                                    phoneNumber = firebaseMember.phoneNumber,
                                    technicalSkills = firebaseMember.technicalSkills,
                                    languages = firebaseMember.languages.map {
                                        Pair(
                                            Locale(it.key),
                                            it.value
                                        )
                                    },
                                    reviews = firebaseMember.reviews.map { frev ->
                                        async {
                                            Review(
                                                message = frev.message,
                                                createdBy = getUserByDocumentReference(
                                                    frev.createdBy,
                                                    loggedUser,
                                                    fetchedUsers
                                                ),
                                                dateCreation = frev.dateCreation.toDate()
                                                    .toInstant().atZone(ZoneId.systemDefault())
                                                    .toLocalDateTime(),
                                                dateModification = frev.dateModification?.toDate()
                                                    ?.toInstant()
                                                    ?.atZone(ZoneId.systemDefault())
                                                    ?.toLocalDateTime()
                                            )
                                        }
                                    }.awaitAll(),
                                    privateBirthdate = firebaseMember.privateBirthdate,
                                    privatePhone = firebaseMember.privatePhone,
                                    privateEmail = firebaseMember.privateEmail,
                                    privateGender = firebaseMember.privateGender,
                                    sound = firebaseMember.sound,
                                    vibration = firebaseMember.vibration,
                                    language = Locale(firebaseMember.language)
                                )
                            }
                        }
                    }

                    launch {
                        val resolvedUsers = users.awaitAll()
                        trySend(resolvedUsers)
                    }
                } else if (error != null) {
                    close(error)
                }
            }

            awaitClose { listener.remove() }
        }

    suspend fun getUserByDocumentReference(
        memberRef: DocumentReference,
        loggedUser: Member,
        fetchedMembers: MutableSet<Member>
    ): Member {

        val memberId = memberRef.id

        fetchedMembers.find { it.id == memberId }?.let { existingMember ->
            return existingMember
        }

        // Fetch the member data from Firestore
        val memberDocument = memberRef.get().await()
        val firebaseMember = memberDocument.toObject(FirebaseMember::class.java)


        return firebaseMember?.let { firebaseMember ->
            var imageBitmap: ImageBitmap? = null

            if (firebaseMember.imageUrl.isNotBlank()) {
                imageBitmap = loadPhotoFromStorage(firebaseMember.imageUrl)
            }

            // Create the Member object
            val member = Member(
                id = memberId,
                photo = imageBitmap, // Handle images in firebase
                name = firebaseMember.name,
                surname = firebaseMember.surname,
                email = firebaseMember.email,
                online = firebaseMember.online,
                password = firebaseMember.password,
                gender = Utils.Gender.valueOf(firebaseMember.gender),
                birthdate = firebaseMember.birthdate.toDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate(),
                nickname = firebaseMember.nickname,
                bio = firebaseMember.bio,
                address = firebaseMember.address,
                phoneNumber = firebaseMember.phoneNumber,
                technicalSkills = firebaseMember.technicalSkills,
                languages = firebaseMember.languages.map { (locale, level) ->
                    Pair(
                        Locale(locale),
                        level
                    )
                },
                reviews = emptyList(), // Initialize with an empty list first,
                privateEmail = firebaseMember.privateEmail,
                privateBirthdate = firebaseMember.privateBirthdate,
                privateGender = firebaseMember.privateGender,
                privatePhone = firebaseMember.privatePhone,
                sound = firebaseMember.sound,
                vibration = firebaseMember.vibration,
                language = Locale(firebaseMember.language)
            )

            // Add the member to fetchedMembers before processing reviews
            fetchedMembers.add(member)

            // Recursively fetch member data for createdBy review if exists
            val createdByReviews = firebaseMember.reviews.map { review ->
                val createdByMemberRef = review.createdBy
                val createdBy =
                    getUserByDocumentReference(createdByMemberRef, loggedUser, fetchedMembers)

                Review(
                    message = review.message,
                    createdBy = createdBy,
                    dateCreation = review.dateCreation.toDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    dateModification = review.dateModification?.toDate()?.toInstant()
                        ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                )
            }

            // Update the reviews in the member object
            member.copy(reviews = createdByReviews)
        } ?: throw IllegalStateException("FirebaseMember ${memberDocument.id} is null")
    }

    suspend fun getAllUsersList(loggedUser: Member = Utils.memberAccessed.value): List<Member> {
        val fetchedUsers: MutableSet<Member> = mutableSetOf()
        val users = db.collection("users").get().await().documents.mapNotNull { document ->
            val firebaseMember = document.toObject(FirebaseMember::class.java)
            firebaseMember?.let {
                var imageBitmap: ImageBitmap? = null
                if (firebaseMember.imageUrl.isNotBlank()) {
                    imageBitmap = loadPhotoFromStorage(firebaseMember.imageUrl)
                }

                val reviews = firebaseMember.reviews.map { frev ->
                    Review(
                        message = frev.message,
                        createdBy = getUserByDocumentReference(
                            frev.createdBy,
                            loggedUser,
                            fetchedUsers
                        ),
                        dateCreation = frev.dateCreation.toDate().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        dateModification = frev.dateModification?.toDate()?.toInstant()
                            ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                    )
                }

                Member(
                    id = document.id,
                    photo = imageBitmap,
                    name = firebaseMember.name,
                    surname = firebaseMember.surname,
                    email = firebaseMember.email,
                    online = firebaseMember.online,
                    password = "", // Don't store the password of the member inside the device
                    gender = Utils.Gender.valueOf(firebaseMember.gender),
                    birthdate = firebaseMember.birthdate.toDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate(),
                    nickname = firebaseMember.nickname,
                    bio = firebaseMember.bio,
                    address = firebaseMember.address,
                    phoneNumber = firebaseMember.phoneNumber,
                    technicalSkills = firebaseMember.technicalSkills,
                    languages = firebaseMember.languages.map {
                        Pair(
                            Locale(it.key),
                            it.value
                        )
                    },
                    reviews = reviews,
                    privateBirthdate = firebaseMember.privateBirthdate,
                    privatePhone = firebaseMember.privatePhone,
                    privateEmail = firebaseMember.privateEmail,
                    privateGender = firebaseMember.privateGender,
                    sound = firebaseMember.sound,
                    vibration = firebaseMember.vibration,
                    language = Locale(firebaseMember.language)
                )
            }
        }

        return users
    }

    suspend fun getUserByDocumentId(userId: String): Member? {
        val usersFlow = getAllUsersList() //getAllUsers()

        /*return usersFlow
            .mapNotNull { members ->
                members.find { member -> member.id == userId }
            }.firstOrNull()*/
        return usersFlow.find { member -> member.id == userId }
    }

    suspend fun getUserByEmail(email: String): Member? {
        val usersList = getAllUsersList()

        return usersList
            .find { member -> member.email == email }
    }

    suspend fun createNewUser(newMember: Member): Member? {
        val newMemberRef = userCollection.document()

        // Map Member to FirebaseMember
        val firebaseMember = FirebaseMember().apply {
            name = newMember.name
            surname = newMember.surname
            email = newMember.email
            online = true
            password = "" // Note: Normally you wouldn't store plain passwords
            gender = newMember.gender.name
            birthdate = Utils.convertToTimestampLocalDate(newMember.birthdate)
            nickname = newMember.nickname
            bio = ""
            address = newMember.address
            phoneNumber = newMember.phoneNumber
            technicalSkills = emptyList()
            languages = emptyMap()
            reviews = emptyList()
            privateGender = false
            privatePhone = false
            privateEmail = false
            privateBirthdate = false
            sound = false
            vibration = false
            language = "ENGLISH"
        }



        return try {
            val documentReference = userCollection.document(newMemberRef.id)
            documentReference.set(firebaseMember).await()

            // Retrieve the newly created user document
            //val documentSnapshot = documentReference.get().await()
            //val firebaseMemberFromDb = documentSnapshot.toObject(FirebaseMember::class.java)

            // Map FirebaseMember to Member
            Member(
                id = newMemberRef.id,
                photo = newMember.photo, // Already uploaded if exists
                name = firebaseMember.name,
                surname = firebaseMember.surname,
                email = firebaseMember.email,
                online = firebaseMember.online,
                password = "", // Don't store the password of the member inside the device
                gender = Utils.Gender.valueOf(firebaseMember.gender),
                birthdate = firebaseMember.birthdate.toDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate(),
                nickname = firebaseMember.nickname,
                bio = firebaseMember.bio,
                address = firebaseMember.address,
                phoneNumber = firebaseMember.phoneNumber,
                technicalSkills = firebaseMember.technicalSkills,
                languages = firebaseMember.languages.map { (locale, level) ->
                    Pair(Locale(locale), level)
                },
                reviews = emptyList(), // Initialize with an empty list first
                privateBirthdate = firebaseMember.privateBirthdate,
                privatePhone = firebaseMember.privatePhone,
                privateEmail = firebaseMember.privateEmail,
                privateGender = firebaseMember.privateGender,
                sound = firebaseMember.sound,
                vibration = firebaseMember.vibration,
                language = Locale(firebaseMember.language)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun editUser(updatedUser: Member) {
        withContext(Dispatchers.IO) {

            updatedUser.photo?.let {
                storeImage(it, "user_${updatedUser.id}.jpg")
            }

            val firebaseMember = FirebaseMember().apply {
                name = updatedUser.name
                surname = updatedUser.surname
                imageUrl = if (updatedUser.photo != null) "user_${updatedUser.id}.jpg" else ""
                email = updatedUser.email
                online = updatedUser.online
                password = updatedUser.password
                gender = updatedUser.gender.name
                birthdate = Timestamp(
                    updatedUser.birthdate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        .toEpochMilli() / 1000, 0
                )
                nickname = updatedUser.nickname
                bio = updatedUser.bio
                address = updatedUser.address
                phoneNumber = updatedUser.phoneNumber
                technicalSkills = updatedUser.technicalSkills
                languages = updatedUser.languages.associate { it.first.language to it.second }
                reviews = updatedUser.reviews.map {
                    FirebaseReview().apply {
                        message = it.message
                        createdBy = db.collection("users").document("users/${it.createdBy.id}")
                        dateCreation = Timestamp(
                            it.dateCreation.atZone(ZoneId.systemDefault()).toInstant()
                                .toEpochMilli() / 1000, 0
                        )
                        it.dateModification?.let {
                            dateModification = Timestamp(
                                it.atZone(ZoneId.systemDefault()).toInstant()
                                    .toEpochMilli() / 1000, 0
                            )
                        }
                    }
                }
                privateEmail = updatedUser.privateEmail
                privateBirthdate = updatedUser.privateBirthdate
                privatePhone = updatedUser.privatePhone
                privateGender = updatedUser.privateGender
                sound = updatedUser.sound
                vibration = updatedUser.vibration
                language = updatedUser.language.language
            }
            val memberRef = db.collection("users").document(updatedUser.id)

            try {
                memberRef.set(firebaseMember).await()
                // Optionally update local state or perform any other actions after successful update
            } catch (e: Exception) {
                // Handle any errors that occur during the update process
                e.printStackTrace()
                // Optionally throw or handle the exception
            }
        }

    }

    suspend fun removeReviewFromUser(user: Member, review: Review) {

        val userDocRef = db.collection("users").document(user.id)

        try {
            val userSnapshot = userDocRef.get().await()
            if (userSnapshot.exists()) {
                val firebaseMember = userSnapshot.toObject(FirebaseMember::class.java)

                if (firebaseMember != null) {
                    val currentReviews = firebaseMember.reviews.map { fbReview ->
                        Review(
                            message = fbReview.message,
                            createdBy = Member(
                                id = fbReview.createdBy.id, // Assuming you can fetch the createdBy Member from its ID
                                photo = null,
                                name = "",
                                surname = "",
                                email = "",
                                online = false,
                                password = "",
                                gender = Utils.Gender.Male,
                                birthdate = LocalDate.now(),
                                nickname = "",
                                bio = "",
                                address = "",
                                phoneNumber = "",
                                technicalSkills = listOf(),
                                languages = listOf(),
                                reviews = listOf(),
                                privateEmail = true,
                                privateBirthdate = true,
                                privateGender = true,
                                privatePhone = true,
                                sound = true,
                                vibration = true,
                                language = Locale("en")
                            ),
                            dateCreation = fbReview.dateCreation.toDate().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                            dateModification = fbReview.dateModification?.toDate()?.toInstant()
                                ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                        )
                    }.toMutableList()

                    // Remove the specified review from the list
                    currentReviews.removeIf { it.message == review.message && it.dateCreation == review.dateCreation }

                    // Convert the updated list of reviews back to FirebaseReview objects
                    val updatedFirebaseReviews = currentReviews.map { updatedReview ->
                        FirebaseReview().apply {
                            message = updatedReview.message
                            createdBy = db.document("users/${updatedReview.createdBy.id}")
                            dateCreation = Timestamp(
                                updatedReview.dateCreation.atZone(ZoneId.systemDefault())
                                    .toInstant().toEpochMilli() / 1000, 0
                            )
                            dateModification = updatedReview.dateModification?.let {
                                Timestamp(
                                    it.atZone(ZoneId.systemDefault()).toInstant()
                                        .toEpochMilli() / 1000, 0
                                )
                            }
                        }
                    }

                    // Update the FirebaseMember object with the new list of reviews
                    firebaseMember.reviews = updatedFirebaseReviews

                    // Update the user's document in Firestore
                    userDocRef.set(firebaseMember).await()
                    println("Review removed successfully.")
                }
            } else {
                println("User document does not exist.")
            }
        } catch (e: Exception) {
            println("Error removing review: ${e.message}")
        }
    }

    suspend fun addFirebaseReview(
        user: Member,
        review: Review,
        loggedUser: Member = Utils.memberAccessed.value
    ) {

        val userDocRef = db.collection("users").document(user.id)
        val fetchedMembers: MutableSet<Member> = mutableSetOf()

        try {
            val userSnapshot = userDocRef.get().await()
            if (userSnapshot.exists()) {
                val firebaseMember = userSnapshot.toObject(FirebaseMember::class.java)

                if (firebaseMember != null) {
                    val currentReviews = firebaseMember.reviews.map { fbReview ->
                        Review(
                            message = fbReview.message,
                            createdBy = getUserByDocumentReference(
                                fbReview.createdBy,
                                loggedUser,
                                fetchedMembers
                            ),
                            dateCreation = fbReview.dateCreation.toDate().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                            dateModification = fbReview.dateModification?.toDate()?.toInstant()
                                ?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
                        )
                    }.toMutableList()

                    // Add the new review to the list
                    currentReviews.add(review)

                    // Convert the updated list of reviews back to FirebaseReview objects
                    val updatedFirebaseReviews = currentReviews.map { updatedReview ->
                        FirebaseReview().apply {
                            message = updatedReview.message
                            createdBy = db.document("users/${updatedReview.createdBy.id}")
                            dateCreation = Timestamp(
                                updatedReview.dateCreation.atZone(ZoneId.systemDefault())
                                    .toInstant().toEpochMilli() / 1000, 0
                            )
                            dateModification = updatedReview.dateModification?.let {
                                Timestamp(
                                    it.atZone(ZoneId.systemDefault()).toInstant()
                                        .toEpochMilli() / 1000, 0
                                )
                            }
                        }
                    }

                    // Update the FirebaseMember object with the new list of reviews
                    firebaseMember.reviews = updatedFirebaseReviews

                    // Update the user's document in Firestore
                    userDocRef.set(firebaseMember).await()
                    println("Review added successfully.")
                }
            } else {
                println("User document does not exist.")
            }
        } catch (e: Exception) {
            println("Error adding review: ${e.message}")
        }
    }

    suspend fun toggleAccountSettingOption(field: String, newValue: Boolean) {
        val userId = Utils.memberAccessed.value.id
        val userRef = db.collection("users").document(userId)
        val updates = mapOf(field to newValue)

        withContext(Dispatchers.IO) {
            try {
                userRef.update(updates).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun editLanguageOption(newLanguage: Locale) {
        val userId = Utils.memberAccessed.value.id
        val userRef = db.collection("users").document(userId)
        val updates = mapOf("language" to newLanguage.language)

        withContext(Dispatchers.IO) {
            try {
                userRef.update(updates).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}