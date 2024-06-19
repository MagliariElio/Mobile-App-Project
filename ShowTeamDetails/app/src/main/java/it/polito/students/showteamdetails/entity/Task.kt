package it.polito.students.showteamdetails.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.model.CommentModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

data class Task(
    var id: String = "",
    var groupID: String = "",
    var title: String = "",
    var status: Utils.StatusEnum = Utils.StatusEnum.PENDING,
    var startDate: LocalDate = LocalDate.now(),
    var startTime: LocalTime = LocalTime.now(),
    var dueDate: LocalDate = LocalDate.now().plusDays(1),
    var dueTime: LocalTime = LocalTime.now(),
    var repeat: Utils.RepeatEnum = Utils.RepeatEnum.NO_REPEAT,
    var repeatEndDate: LocalDate = LocalDate.now(),
    var description: String = "",
    var category: Utils.CategoryEnum = Utils.CategoryEnum.MARKETING,
    var tagList: List<String> = emptyList(),
    var historyList: List<History> = emptyList(),
    var fileList: List<File> = emptyList(),
    var linkList: List<Link> = emptyList(),
    var commentList: List<Comment> = emptyList(),
    val delegateList: List<MemberInfoTeam> = emptyList(),
    var created: CreatedInfo = CreatedInfo(Utils.memberAccessed.value, LocalDateTime.now()),
)

class TaskFirebase {
    var id: String = ""
    var groupID: String = ""
    var title: String = ""
    var status: Utils.StatusEnum = Utils.StatusEnum.PENDING
    var startDate: Timestamp = Timestamp.now()
    var dueDate: Timestamp = Timestamp.now()
    var repeat: Utils.RepeatEnum = Utils.RepeatEnum.NO_REPEAT
    var repeatEndDate: Timestamp = Timestamp.now()
    var description: String = ""
    var category: Utils.CategoryEnum = Utils.CategoryEnum.MARKETING
    var tagList: List<String> = emptyList()
    var historyList: List<HistoryFirebase> = emptyList()
    var fileList: List<FileFirebase> = emptyList()
    var linkList: List<LinkFirebase> = emptyList()
    var commentList: List<DocumentReference> = emptyList()
    var delegateList: List<DocumentReference> = emptyList()
    lateinit var created: CreatedInfoFirebase
}

suspend fun convertFlowListToTaskList(flowList: List<Flow<Task?>>): List<Task> {
    val taskList = mutableListOf<Task>()

    for (flow in flowList) {
        val task = flow.first()
        taskList.add(task!!)
    }

    return taskList
}

suspend fun TaskFirebase.toTask(usersList: List<Member>): Task {
    val startDate = Utils.convertTimestampToLocalDate(this.startDate)
    val startTime = Utils.convertTimestampToLocalTime(this.startDate)
    val dueDate = Utils.convertTimestampToLocalDate(this.dueDate)
    val dueTime = Utils.convertTimestampToLocalTime(this.dueDate)

    val commentsList = CommentModel().getCommentsFromReferences(this.commentList, usersList)

    val createdMember =
        usersList.find { it.id == this.created.member.id } ?: Utils.memberAccessed.value

    return Task(
        id = this.id,
        groupID = this.groupID,
        title = this.title,
        description = this.description,
        status = this.status,
        startDate = startDate,
        startTime = startTime,
        dueDate = dueDate,
        dueTime = dueTime,
        repeat = this.repeat,
        repeatEndDate = this.repeatEndDate.toDate().toInstant()?.atZone(ZoneId.systemDefault())
            ?.toLocalDate()!!,
        category = this.category,
        tagList = this.tagList,
        historyList = convertHistoryFirebaseListToHistoryList(this.historyList, usersList),
        fileList = convertFileFirebaseListToFileList(this.fileList, usersList),
        linkList = convertLinkFirebaseListToLinkList(this.linkList, usersList),
        commentList = commentsList,
        delegateList = convertMemberInfoTeamFirebaseListToMemberInfoTeamList(this.delegateList),
        created = CreatedInfo(
            createdMember,
            Utils.convertTimestampToLocalDateTime(this.created.timestamp)
        ),
    )
}

fun Task.toTaskFirebase(): TaskFirebase {
    // creazione createdFirebase
    val createdFirebase = CreatedInfoFirebase()
    createdFirebase.member = Firebase.firestore.collection(Utils.CollectionsEnum.users.name)
        .document(this.created.member.id)
    createdFirebase.timestamp = Utils.convertToTimestampLocalDateTime(this.created.timestamp)

    // creazione taskFirebase
    val taskFirebase = TaskFirebase()
    taskFirebase.id = this.id
    taskFirebase.groupID = this.groupID
    taskFirebase.title = this.title
    taskFirebase.description = this.description
    taskFirebase.status = this.status
    taskFirebase.startDate = Utils.convertToTimestampLocalDateTime(this.startDate, this.startTime)
    taskFirebase.dueDate = Utils.convertToTimestampLocalDateTime(this.dueDate, this.dueTime)
    taskFirebase.repeat = this.repeat
    taskFirebase.repeatEndDate = Utils.convertToTimestampLocalDate(this.repeatEndDate)
    taskFirebase.category = this.category
    taskFirebase.tagList = this.tagList
    taskFirebase.historyList = convertHistoryListToHistoryFirebaseList(this.historyList)
    taskFirebase.fileList = convertFileListToFileFirebaseList(this.fileList)
    taskFirebase.linkList = convertLinkListToLinkFirebaseList(this.linkList)
    taskFirebase.commentList =
        emptyList() // quando si salva il task nel db, parte con una lista vuota di commenti
    taskFirebase.delegateList =
        convertMemberInfoTeamListToMemberInfoTeamFirebaseList(this.delegateList)
    taskFirebase.created = createdFirebase

    return taskFirebase
}