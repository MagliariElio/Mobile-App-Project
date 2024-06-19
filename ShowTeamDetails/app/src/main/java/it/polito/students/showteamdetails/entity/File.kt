package it.polito.students.showteamdetails.entity

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.students.showteamdetails.Utils
import java.time.LocalDateTime

data class File(
    val name: String,
    val contentType: Utils.ContentTypeEnum,
    val size: String,
    val uploadedBy: Member,
    val dateUpload: LocalDateTime,
    val uri: Uri
)

class FileFirebase {
    lateinit var name: String
    lateinit var contentType: Utils.ContentTypeEnum
    lateinit var size: String
    lateinit var uploadedBy: DocumentReference
    lateinit var dateUpload: Timestamp
    lateinit var uri: String
}

fun File.toFileFirebase(): FileFirebase {
    val fileFirebase = FileFirebase()
    fileFirebase.name = this.name
    fileFirebase.contentType = this.contentType
    fileFirebase.size = this.size
    fileFirebase.uploadedBy = Firebase.firestore.collection(Utils.CollectionsEnum.users.name)
        .document(this.uploadedBy.id)
    fileFirebase.dateUpload = Utils.convertToTimestampLocalDateTime(this.dateUpload)
    fileFirebase.uri = this.uri.toString()

    return fileFirebase
}

fun FileFirebase.toFile(uploadedBy: Member): File {
    return File(
        name = this.name,
        contentType = this.contentType,
        size = this.size,
        uploadedBy = uploadedBy,
        dateUpload = Utils.convertTimestampToLocalDateTime(this.dateUpload),
        uri = if (this.uri == "") Uri.EMPTY else Uri.parse(this.uri)
    )
}

fun convertFileListToFileFirebaseList(fileList: List<File>): List<FileFirebase> {
    return fileList.map { it.toFileFirebase() }
}

fun convertFileFirebaseListToFileList(
    fileList: List<FileFirebase>,
    usersList: List<Member>
): List<File> {
    return fileList.map { file ->
        val uploadedBy =
            usersList.find { it.id == file.uploadedBy.id } ?: Utils.memberAccessed.value
        file.toFile(uploadedBy)
    }
}