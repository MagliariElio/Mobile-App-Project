package it.polito.students.showteamdetails.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.students.showteamdetails.ErrorsPage
import it.polito.students.showteamdetails.MessagesPage
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.Comment
import it.polito.students.showteamdetails.entity.CreatedInfo
import it.polito.students.showteamdetails.entity.File
import it.polito.students.showteamdetails.entity.History
import it.polito.students.showteamdetails.entity.Link
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.MemberInfoTeam
import it.polito.students.showteamdetails.entity.RoleEnum
import it.polito.students.showteamdetails.entity.Task
import it.polito.students.showteamdetails.model.TaskModel
import it.polito.students.showteamdetails.routers.RouterActions
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class TaskViewModel(
    task: Task,
    private val routerActions: RouterActions,
    val taskModel: TaskModel = TaskModel()
) : ViewModel() {
    val id = task.id
    val groupID = task.groupID
    val titleField = TitleFieldViewModel(task.title)
    val startDateField = StartDateFieldViewModel(task.startDate, task.startTime)
    val dueDateField = DueDateFieldViewModel(task.dueDate, task.dueTime)
    val repeatField = RepeatFieldViewModel(task.repeat, task.repeatEndDate)
    val descriptionField = DescriptionFieldViewModel(task.description)
    val categoryField = CategoryFieldViewModel(task.category)
    val assignTagsField = TagFieldViewModel(task.tagList)
    val fileUploadedField = FileUploadedFieldViewModel(task.fileList)
    val linkListField = LinkListViewModel(task.linkList)
    val statusField = StatusFieldViewModel(task.status)
    val creationField = CreationFieldViewModel(task.created.member, task.created.timestamp)
    val historyListField = HistoryViewModel(task.historyList)
    val commentListField = CommentListViewModel(task.commentList)
    val delegateTasksField = MembersParticipationViewModel(task.delegateList)

    var stateTab by mutableIntStateOf(0)

    fun addComment(commentText: String) {
        val createdDate = LocalDateTime.now()
        val comment = Comment(
            comment = commentText,
            createdBy = Utils.memberAccessed.value,
            dateCreation = createdDate,
            dateModification = createdDate
        )
        createNewHistoryLine(createdDate, MessagesPage.ADDED_COMMENT)

        viewModelScope.launch {
            val commentAdded = taskModel.commentModel.addComment(comment = comment, taskId = id)
            if (commentAdded != null) {
                commentListField.addComment(commentAdded)
            }
        }
    }

    fun modifyComment(comment: Comment) {
        val modifiedDate = LocalDateTime.now()
        val modifiedComment = Comment(
            id = comment.id,
            comment = commentListField.editComment.value,
            createdBy = Utils.memberAccessed.value,
            dateCreation = comment.dateCreation,
            dateModification = modifiedDate
        )
        createNewHistoryLine(modifiedDate, MessagesPage.MODIFY_MESSAGE)

        viewModelScope.launch {
            val commentUpdated =
                taskModel.commentModel.updateComment(comment = modifiedComment, taskId = id)
            if (commentUpdated != null) {
                commentListField.addComment(commentUpdated)
                commentListField.deleteComment(comment)
                commentListField.editComment.value = ""
            }
        }
    }

    fun deleteComment(comment: Comment) {
        viewModelScope.launch {
            val isDeleted = taskModel.commentModel.deleteCommentById(comment.id)
            if (isDeleted) {
                commentListField.deleteComment(comment)
                createNewHistoryLine(
                    LocalDateTime.now(),
                    MessagesPage.DELETED_MESSAGE
                )
            }
        }
    }

    fun createNewHistoryLine(
        createdDate: LocalDateTime,
        message: Int,
        delegatedMember: MemberInfoTeam? = null
    ) {
        val history = History(
            member = Utils.memberAccessed.value,
            date = createdDate,
            value = message,
            delegatedMember = delegatedMember
        )

        viewModelScope.launch {
            val isAdded = taskModel.addHistory(history = history, taskId = id)
            if (isAdded) {
                historyListField.addHistory(history)
            }
        }
    }

    fun updateTask(taskVm: TaskViewModel) {
        val title = taskVm.titleField.value
        val description = taskVm.descriptionField.value
        val category = taskVm.categoryField.category
        val startDate = taskVm.startDateField.date
        val dueDate = taskVm.dueDateField.date
        val startDateTime = taskVm.startDateField.time
        val dueDateTime = taskVm.dueDateField.time
        val repeat = taskVm.repeatField.value
        val fileList = taskVm.fileUploadedField.getVal()

        if (taskVm.titleField.editValue != title)
            taskVm.createNewHistoryLine(
                LocalDateTime.now(),
                R.string.modify_title
            )

        if (taskVm.descriptionField.editValue != description)
            taskVm.createNewHistoryLine(
                LocalDateTime.now(),
                R.string.modify_description
            )

        if (taskVm.categoryField.editCategory != category)
            taskVm.createNewHistoryLine(
                LocalDateTime.now(),
                R.string.modify_category
            )

        if (taskVm.startDateField.editDate != startDate)
            taskVm.createNewHistoryLine(
                LocalDateTime.now(),
                R.string.modify_start_date
            )

        if (taskVm.startDateField.editTime != startDateTime)
            taskVm.createNewHistoryLine(
                LocalDateTime.now(),
                R.string.modify_start_time
            )

        if (taskVm.dueDateField.editDate != dueDate)
            taskVm.createNewHistoryLine(
                LocalDateTime.now(),
                R.string.modify_due_date
            )

        if (taskVm.dueDateField.editTime != dueDateTime)
            taskVm.createNewHistoryLine(
                LocalDateTime.now(),
                R.string.modify_due_time
            )

        if (taskVm.repeatField.editValue != repeat)
            taskVm.createNewHistoryLine(
                LocalDateTime.now(),
                R.string.modified_recurrence
            )

        if (taskVm.fileUploadedField.editFileList != fileList)
            taskVm.createNewHistoryLine(
                LocalDateTime.now(),
                R.string.uploaded_file
            )

        viewModelScope.launch {
            taskModel.updateTask(taskVm.getTask())
        }
    }


    fun getTask(): Task {
        return Task(
            id = this.id,
            groupID = this.groupID,
            title = this.titleField.value,
            status = this.statusField.status,
            startDate = this.startDateField.date,
            startTime = this.startDateField.time,
            dueDate = this.dueDateField.date,
            dueTime = this.dueDateField.time,
            repeat = this.repeatField.value,
            repeatEndDate = this.repeatField.repeatEndDate,
            description = this.descriptionField.value,
            category = this.categoryField.category!!,
            tagList = this.assignTagsField.value,
            historyList = this.historyListField.histories,
            fileList = this.fileUploadedField.fileList,
            linkList = this.linkListField.links,
            commentList = this.commentListField.comments,
            delegateList = this.delegateTasksField.members,
            created = CreatedInfo(this.creationField.createdBy, this.creationField.dateCreation)
        )
    }

    private fun copyTask(
        id: String = this.id,
        groupID: String = this.groupID,
        title: String = this.titleField.value,
        startDate: LocalDate = this.startDateField.date,
        startTime: LocalTime = this.startDateField.time,
        dueDate: LocalDate = this.dueDateField.date,
        dueTime: LocalTime = this.dueDateField.time,
        repeat: Utils.RepeatEnum = this.repeatField.value,
        repeatEndDate: LocalDate = this.repeatField.repeatEndDate,
        description: String = this.descriptionField.value,
        category: Utils.CategoryEnum? = this.categoryField.category,
        tagList: List<String> = this.assignTagsField.value,
        historyList: List<History> = this.historyListField.histories,
        fileList: List<File> = this.fileUploadedField.editFileList,
        linkList: List<Link> = this.linkListField.links,
        commentList: List<Comment> = this.commentListField.comments,
        delegateList: List<MemberInfoTeam> = this.delegateTasksField.members,
        created: CreatedInfo = CreatedInfo(
            this.creationField.createdBy,
            this.creationField.dateCreation
        ),
        routerActions: RouterActions
    ): TaskViewModel {
        val newTask = Task(
            id = id.toString(),
            groupID = groupID.toString(),
            title = title,
            status = Utils.StatusEnum.PENDING,
            startDate = startDate,
            startTime = startTime,
            dueDate = dueDate,
            dueTime = dueTime,
            repeat = repeat,
            repeatEndDate = repeatEndDate,
            description = description,
            category = category!!,
            tagList = tagList,
            historyList = historyList,
            fileList = fileList,
            linkList = linkList,
            commentList = commentList,
            delegateList = delegateList,
            created = CreatedInfo(created.member, created.timestamp)
        )

        return TaskViewModel(newTask, routerActions)
    }


    fun canEditBy(): Boolean {
        val memberAccessed: Member = Utils.memberAccessed.value

        return delegateTasksField.members.any {
            it.profile.id == memberAccessed.id || creationField.createdBy.id == memberAccessed.id
        }
    }

    fun cancelButtonClicked() {  //Come back to the view Task page
        titleField.editValue = titleField.value
        startDateField.editDate = startDateField.date
        startDateField.editTime = startDateField.time
        dueDateField.editDate = dueDateField.date
        dueDateField.editTime = dueDateField.time
        repeatField.editValue = repeatField.value
        descriptionField.editValue = descriptionField.value
        categoryField.editCategory = categoryField.category
        assignTagsField.setEditVal(assignTagsField.value.toList())
        delegateTasksField.setEditVal(delegateTasksField.members.toList())
        fileUploadedField.setEditVal(fileUploadedField.getVal())
        linkListField.setEditVal(linkListField.links)

        // Reset errors to an empty string
        titleField.error = -1
        startDateField.errorDate = -1
        startDateField.errorTime = -1
        dueDateField.errorDate = -1
        dueDateField.errorTime = -1
        repeatField.errorRepeat = -1
        repeatField.errorDate = -1
        descriptionField.error = -1
        fileUploadedField.error = -1
        linkListField.error = -1
        categoryField.error = -1
        commentListField.errorNewComment = -1
    }


    fun validateForm() {
        titleField.validate()
        startDateField.validateDate(dueDateField.editDate)
        startDateField.validateTime(dueDateField.editDate, dueDateField.editTime)
        dueDateField.validateDate(startDateField.editDate)
        dueDateField.validateTime(startDateField.editDate, startDateField.editTime)
        repeatField.validate()
        descriptionField.validate()
        delegateTasksField.validate()
        assignTagsField.validate()
        categoryField.validate()

        val willEdit =
            listOf(
                titleField.error,
                startDateField.errorDate,
                startDateField.errorTime,
                dueDateField.errorDate,
                dueDateField.errorTime,
                repeatField.errorRepeat,
                repeatField.errorDate,
                descriptionField.error,
                delegateTasksField.error,
                assignTagsField.error,
                fileUploadedField.error,
                linkListField.error,
                categoryField.error
            ).none { it != -1 }

        // Verifying if at least one is not empty
        if (willEdit) {
            titleField.setVal(titleField.editValue)
            startDateField.updateDate(startDateField.editDate)
            startDateField.updateTime(startDateField.editTime)
            dueDateField.updateDate(dueDateField.editDate)
            dueDateField.updateTime(dueDateField.editTime)
            repeatField.setVal(repeatField.editValue)
            repeatField.setEndDate(repeatField.editRepeatEndDate)
            descriptionField.setVal(descriptionField.editValue)
            categoryField.setVal(categoryField.editCategory!!)
            delegateTasksField.setVal(delegateTasksField.editMembers.toList())
            assignTagsField.setVal(assignTagsField.editValue)
            fileUploadedField.setVal(fileUploadedField.getEditVal())
            linkListField.setVal(linkListField.editLinks)

            if (routerActions.getCurrentRoute().contains(Utils.RoutesEnum.CREATE_TASK.name)) {
                creationField.setCreatedByVal(Utils.memberAccessed.value)
                creationField.setDateCreationVal(LocalDateTime.now())
                historyListField.deleteHistorySynchro()
                createNewHistoryLine(LocalDateTime.now(), R.string.created_task)
            }
        }
    }

    fun areThereUnsavedChanges(): Boolean {
        return !(
                titleField.value == titleField.editValue &&
                        startDateField.date == startDateField.editDate &&
                        startDateField.time == startDateField.editTime &&
                        dueDateField.date == dueDateField.editDate &&
                        dueDateField.time == dueDateField.editTime &&
                        repeatField.value == repeatField.editValue &&
                        descriptionField.value == descriptionField.editValue &&
                        categoryField.category == categoryField.editCategory &&
                        delegateTasksField.areListsEqual() &&
                        assignTagsField.areListsEqual() &&
                        fileUploadedField.getVal() == fileUploadedField.getEditVal() &&
                        linkListField.areListsEqual()
                )
    }

    fun areThereErrors(): Boolean = (
            titleField.error != -1 ||
                    startDateField.errorDate != -1 ||
                    startDateField.errorTime != -1 ||
                    dueDateField.errorDate != -1 ||
                    dueDateField.errorTime != -1 ||
                    repeatField.errorRepeat != -1 ||
                    repeatField.errorDate != -1 ||
                    descriptionField.error != -1 ||
                    assignTagsField.error != -1 ||
                    delegateTasksField.error != -1 ||
                    fileUploadedField.error != -1 ||
                    linkListField.error != -1 ||
                    categoryField.error != -1
            )

    /* REPEAT LOGIC */
    private fun calculateNextDate(
        currentDate: LocalDate,
        repeatType: Utils.RepeatEnum
    ): LocalDate? {
        val repeatInterval = 1L
        return when (repeatType) {
            Utils.RepeatEnum.NO_REPEAT -> null
            Utils.RepeatEnum.DAILY -> currentDate.plusDays(repeatInterval)
            Utils.RepeatEnum.WEEKLY -> currentDate.plusWeeks(repeatInterval)
            Utils.RepeatEnum.MONTHLY -> currentDate.plusMonths(repeatInterval)
            Utils.RepeatEnum.YEARLY -> currentDate.plusYears(repeatInterval)
        }
    }

    private fun createRepeatedTasksLogic(
        taskVm: TaskViewModel,
        groupID: String
    ): MutableList<TaskViewModel> {
        if (taskVm.repeatField.editValue == Utils.RepeatEnum.NO_REPEAT) {
            return mutableListOf(taskVm)
        }

        val durationTask =
            ChronoUnit.DAYS.between(taskVm.startDateField.editDate, taskVm.dueDateField.editDate)
        var nextDate = taskVm.startDateField.editDate
        val endDate = taskVm.repeatField.editRepeatEndDate

        val list = mutableListOf<TaskViewModel>()
        while (nextDate <= endDate) {
            val newTask = taskVm.copyTask(
                groupID = groupID,
                startDate = nextDate,
                dueDate = nextDate.plusDays(durationTask),
                repeatEndDate = taskVm.repeatField.repeatEndDate,
                historyList = taskVm.historyListField.editHistories,
                commentList = taskVm.commentListField.editComments,
                fileList = taskVm.fileUploadedField.editFileList,
                linkList = taskVm.linkListField.editLinks,
                routerActions = routerActions
            )
            list.add(newTask)

            nextDate = calculateNextDate(
                nextDate,
                taskVm.repeatField.editValue
            )!!
        }

        return list
    }

    fun createRepeatedTasks(taskVm: TaskViewModel, teamVm: TeamViewModel) {
        val list = createRepeatedTasksLogic(taskVm, taskVm.groupID)
        list.forEach { taskVm ->
            viewModelScope.launch {
                teamVm.addTask(taskVm)
            }
        }
    }

    fun editRepeatedTasks(taskVm: TaskViewModel, teamVm: TeamViewModel) {
        // Remove all tasks with same id
        teamVm.tasksList.removeIf {
            it.groupID == taskVm.groupID &&
                    taskVm.startDateField.editDate.isBefore(it.startDateField.editDate)
        }
        teamVm.tasksList.remove(taskVm) // remove itself

        val list = createRepeatedTasksLogic(taskVm, taskVm.groupID)

        list.forEach { task ->
            viewModelScope.launch {
                teamVm.addTask(task)
            }
        }
    }
    /* REPEAT LOGIC */
}

class TitleFieldViewModel(title: String) {
    var value by mutableStateOf(title)
        private set

    var error by mutableIntStateOf(-1)

    fun setVal(valueParam: String) {
        value = valueParam
    }

    var editValue by mutableStateOf(title)

    fun setEditVal(editValueParam: String) {
        editValue = editValueParam
    }

    fun validate() {
        error = when {
            editValue.isBlank() -> ErrorsPage.TITLE_BLANK
            editValue.length < 2 -> ErrorsPage.TITLE_TOO_SHORT
            editValue.length > 200 -> ErrorsPage.TITLE_TOO_LONG
            !editValue.matches(Regex("^[a-zA-Z0-9 ]+\$")) -> ErrorsPage.TITLE_INVALID
            else -> -1
        }
    }
}

class StartDateFieldViewModel(date: LocalDate, time: LocalTime) {

    var date by mutableStateOf(date)
        private set

    var time by mutableStateOf(time)
        private set

    var errorDate by mutableIntStateOf(-1)

    var errorTime by mutableIntStateOf(-1)

    var editDate by mutableStateOf(date)

    var editTime by mutableStateOf(time)

    fun updateDate(date: LocalDate) {
        editDate = date
    }

    fun updateTime(time: LocalTime) {
        this.time = time
    }

    fun validateDate(dueDate: LocalDate) {
        val currentDate = LocalDate.now()

        errorDate = when {
            editDate < currentDate -> ErrorsPage.DUE_DATE_PAST
            editDate > dueDate -> ErrorsPage.DUE_DATE_FUTURE
            else -> -1
        }
    }

    fun resetErrorStartDate() {
        val currentDate = LocalDate.now()
        if (editDate > currentDate)
            errorDate = -1
    }

    fun validateTime(dueDate: LocalDate, dueTime: LocalTime) {
        errorTime = when {
            editDate == dueDate && editTime > dueTime -> ErrorsPage.START_DATE_FUTURE
            else -> -1
        }
    }

    fun dateToString(): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return editDate.format(dateFormatter)
    }

    fun timeToString(): String {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        return editTime.format(timeFormatter)
    }
}

class DueDateFieldViewModel(dueDate: LocalDate, dueTime: LocalTime) {

    var date by mutableStateOf<LocalDate>(dueDate)
        private set

    var time by mutableStateOf<LocalTime>(dueTime)
        private set

    var errorDate by mutableIntStateOf(-1)

    var errorTime by mutableIntStateOf(-1)

    var editDate by mutableStateOf<LocalDate>(dueDate)

    var editTime by mutableStateOf<LocalTime>(dueTime)

    fun updateDate(date: LocalDate) {
        editDate = date
    }

    fun updateTime(time: LocalTime) {
        this.time = time
    }

    fun validateDate(startDate: LocalDate) {
        val currentDate = LocalDate.now()

        errorDate = when {
            editDate < currentDate -> ErrorsPage.DUE_DATE_PAST
            editDate < startDate -> ErrorsPage.DUE_DATE_INVALID
            else -> -1
        }
    }

    fun resetErrorDueDate() {
        val currentDate = LocalDate.now()
        if (editDate > currentDate)
            errorDate = -1
    }

    fun validateTime(startDate: LocalDate, startTime: LocalTime) {
        errorTime = when {
            editDate == startDate && editTime <= startTime -> ErrorsPage.DUE_TIME_INVALID
            else -> -1
        }
    }

    fun dateToString(): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return editDate.format(dateFormatter)
    }

    fun timeToString(): String {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        return editTime.format(timeFormatter)
    }
}

class RepeatFieldViewModel(value: Utils.RepeatEnum, repeatEndDate: LocalDate) {
    var value by mutableStateOf(value)
        private set

    var editValue by mutableStateOf(value)

    fun setVal(repeatParam: Utils.RepeatEnum) {
        value = repeatParam
    }

    var repeatEndDate by mutableStateOf(repeatEndDate)
    var editRepeatEndDate by mutableStateOf(repeatEndDate)


    fun setEndDate(date: LocalDate) {
        repeatEndDate = date
    }

    var errorRepeat by mutableIntStateOf(-1)
    var errorDate by mutableIntStateOf(-1)

    fun validate() {
        errorDate = when {
            repeatEndDate.isBefore(LocalDate.now()) -> ErrorsPage.END_DATE_INVALID
            else -> -1
        }

    }
}

class DescriptionFieldViewModel(description: String) {
    var value by mutableStateOf(description)
        private set

    var error by mutableIntStateOf(-1)

    fun setVal(s: String) {
        value = s
    }

    var editValue by mutableStateOf(description)
    fun validate() {
        error = when {
            editValue.isBlank() -> ErrorsPage.DESCRIPTION_BLANK
            editValue.length > 1000 -> ErrorsPage.DESCRIPTION_NOT_TOO_LONG
            else -> -1
        }
    }
}

class CategoryFieldViewModel(category: Utils.CategoryEnum?) {
    var category by mutableStateOf(category)
        private set

    var editCategory by mutableStateOf(category)

    fun setVal(categoryParam: Utils.CategoryEnum) {
        category = categoryParam
    }

    var error by mutableIntStateOf(-1)

    fun validate() {
        if (editCategory == null) {
            error = ErrorsPage.CATEGORY_BLANK
        }
    }
}

class TagFieldViewModel(tagList: List<String>) {
    private val tagColorsMap = mutableMapOf<String, Int>() // Map associating tags with colors

    var value = mutableStateListOf<String>().apply { addAll(tagList) }

    init {
        tagList.forEachIndexed { index, tag ->
            tagColorsMap[tag] = Utils.getTagColor(index)
        }
    }

    var error by mutableIntStateOf(-1)

    fun setVal(list: MutableList<String>) {
        value.clear()
        value.addAll(list)
    }

    var editValue = mutableStateListOf<String>().apply { addAll(tagList) }

    fun setEditVal(valueParam: List<String>) {
        this.error = -1
        editValue.clear()
        editValue.addAll(valueParam)
    }

    fun validate() {
        val duplicateTags = value.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
        error = if (duplicateTags.isNotEmpty()) {
            ErrorsPage.DUPLICATE_TAGS // duplicateTags.joinToString()
        } else {
            -1
        }
    }

    fun addTag(tag: String): Boolean {
        val lowercaseTag =
            tag.trim().lowercase() // Remove whitespace and convert the tag to lowercase

        // Check if the tag contains only alphanumeric characters and spaces, and not just numbers
        val isValidTag = lowercaseTag.matches(Regex("^(?=.*[a-zA-Z])[a-zA-Z0-9\\s]{1,50}\$"))

        return if (!isValidTag) {
            error = ErrorsPage.INVALID_TAG
            false
        } else if (editValue.any { it.equals(lowercaseTag, ignoreCase = true) }) {
            error = ErrorsPage.DUPLICATE_TAG
            false
        } else if (lowercaseTag.split("\\s+".toRegex()).size > 3) {
            error = ErrorsPage.TOO_MANY_TAGS
            false
        } else {
            value.add(lowercaseTag)
            editValue.add(lowercaseTag)
            if (!tagColorsMap.containsKey(lowercaseTag)) {
                val newColor =
                    Utils.getTagColor(tagColorsMap.size) // Generates a new color for the tag
                tagColorsMap[lowercaseTag] =
                    newColor // Associates the new color with the tag in the map
            }
            error = -1
            true
        }
    }

    fun getTagColor(tag: String): Int {
        val color = tagColorsMap[tag]
        return color ?: Color.Gray.toArgb()
    }

    fun updateTagColor(tag: String, color: Int) {
        tagColorsMap[tag] = color
    }

    fun removeTag(tag: String) {
        value.remove(tag)
        editValue.remove(tag)
        tagColorsMap.remove(tag)
    }

    fun areListsEqual(): Boolean {
        if (value.size != editValue.size) return false
        val sortedList1 = value.sorted()
        val sortedList2 = editValue.sorted()

        for (i in sortedList1.indices) {
            if (sortedList1[i] != sortedList2[i]) {
                return false
            }
        }
        return true
    }
}

class FileUploadedFieldViewModel(listFileParam: List<File>) {
    val fileList = mutableStateListOf<File>()
        .apply { addAll(listFileParam) }

    var error by mutableIntStateOf(-1)

    val editFileList = mutableStateListOf<File>()
        .apply { addAll(listFileParam) }

    fun getVal(): List<File> {
        return this.fileList
    }

    fun getEditVal(): List<File> {
        return this.editFileList
    }

    fun setVal(value: List<File>) {
        this.fileList.clear()
        this.fileList.addAll(value)
    }

    fun setEditVal(value: List<File>) {
        this.editFileList.clear()
        this.editFileList.addAll(value)
    }

    fun deleteFileSynch(file: File, taskId: String) {
        runBlocking {
            val isDeleted = TaskModel().deleteFile(file, taskId)
            if (isDeleted) {
                fileList.remove(file)
                editFileList.remove(file)
            }
        }
    }

    fun addFileSynch(file: File, taskId: String) {
        runBlocking {
            val isAdded = TaskModel().addFile(file, taskId)
            if (isAdded) {
                fileList.add(file)
                editFileList.add(file)
            }
        }
    }

    fun addFile(file: File) {
        editFileList.add(file)
    }

    fun deleteFile(file: File) {
        editFileList.remove(file)
    }
}

class StatusFieldViewModel(status: Utils.StatusEnum) {
    var status by mutableStateOf(status)
        private set

    var editStatus by mutableStateOf(status)

    fun setVal(statusParam: Utils.StatusEnum, taskId: String) {
        status = statusParam
        editStatus = statusParam

        runBlocking {
            TaskModel().updateStatus(statusParam, taskId)
        }
    }
}

class CreationFieldViewModel(member: Member, dateCreation: LocalDateTime) {
    var createdBy by mutableStateOf<Member>(member)
        private set

    var dateCreation by mutableStateOf(dateCreation)
        private set

    fun setCreatedByVal(value: Member) {
        createdBy = value
    }

    fun setDateCreationVal(value: LocalDateTime) {
        dateCreation = value
    }
}

class HistoryViewModel(historyList: List<History>) {
    var histories = mutableStateListOf<History>().apply { addAll(historyList) }
        private set

    var editHistories = mutableStateListOf<History>().apply { addAll(historyList) }
        private set

    var error by mutableIntStateOf(-1)

    fun setVal(historyList: List<History>) {
        histories.clear()
        histories.addAll(historyList)
    }

    fun setEditVal(historyList: List<History>) {
        this.error = -1
        editHistories.clear()
        editHistories.addAll(historyList)
    }

    fun addHistory(history: History) {
        editHistories.add(history)
    }

    fun deleteHistorySynchro() {
        histories.clear()
        editHistories.clear()
    }

}

class MembersParticipationViewModel(membersList: List<MemberInfoTeam>) {
    var members =
        mutableStateListOf<MemberInfoTeam>().apply {
            addAll(membersList)
        }
        private set

    var editMembers =
        mutableStateListOf<MemberInfoTeam>().apply {
            addAll(membersList)
        }
        private set

    var error by mutableIntStateOf(-1)

    fun setVal(membersList: List<MemberInfoTeam>) {
        members.clear()
        members.addAll(membersList)
    }

    fun setEditVal(membersList: List<MemberInfoTeam>) {
        this.error = -1
        editMembers.clear()
        editMembers.addAll(membersList)
    }

    fun changeRole(user: Member, role: RoleEnum) {
        val indexMembers = members.indexOfFirst { it.profile == user }
        val indexEditMembers = editMembers.indexOfFirst { it.profile == user }

        if (role == RoleEnum.NO_ROLE_ASSIGNED) {
            this.error = ErrorsPage.ROLE_BLANK
        } else if (indexMembers != -1 && indexEditMembers != -1) {
            members[indexMembers].role = role
            editMembers[indexEditMembers].role = role
            error = -1
        }
    }

    fun changeTimeParticipation(
        user: Member,
        timeParticipation: Utils.TimePartecipationTeamTypes
    ) {
        val indexMembers = members.indexOfFirst { it.profile == user }
        val indexEditMembers = editMembers.indexOfFirst { it.profile == user }

        if (indexMembers != -1 && indexEditMembers != -1) {
            members[indexMembers].participationType = timeParticipation
            editMembers[indexEditMembers].participationType = timeParticipation
        }
    }

    fun addMember(
        member: MemberInfoTeam,
        isRequestsPage: Boolean = false
    ) {
        /*        if (!editMembers.contains(member)) {
                    this.error = -1
                    editMembers.add(member)
                } else {
                    this.error = ErrorsPage.MEMBER_ALREADY_IN_LIST
                }*/

        val existingMember = editMembers.find { user ->
            user.profile.name == member.profile.name &&
                    user.profile.surname == member.profile.surname
        }

        if (existingMember != null)
            this.error = ErrorsPage.MEMBER_ALREADY_IN_LIST
        else if (member.role == RoleEnum.NO_ROLE_ASSIGNED)
            this.error = ErrorsPage.ROLE_BLANK

        if (existingMember == null && member.role != RoleEnum.NO_ROLE_ASSIGNED) {
            this.error = -1
            editMembers.add(member)

            if (isRequestsPage) {
                members.add(member)
            }
        }
    }

    fun deleteMemberSynch(member: MemberInfoTeam) {
        members.remove(member)
        editMembers.remove(member)
    }

    fun deleteMember(member: MemberInfoTeam) {
        editMembers.remove(member)
        validate()
    }

    fun validate() {
        if (editMembers.size == 0) {
            this.error = ErrorsPage.NO_MEMBER
        }
    }

    fun areListsEqual(): Boolean {
        if (members.size != editMembers.size) return false
        val sortedList1 = members.sortedBy { it.profile.nickname }
        val sortedList2 = editMembers.sortedBy { it.profile.nickname }

        for (i in sortedList1.indices) {
            val member1 = sortedList1[i]
            val member2 = sortedList2[i]
            if (member1 != member2) {
                return false
            }
        }
        return true
    }
}

class LinkListViewModel(linkList: List<Link>) {
    var links = mutableStateListOf<Link>().apply { addAll(linkList) }
        private set

    var editLinks = mutableStateListOf<Link>().apply { addAll(linkList) }
        private set

    var error by mutableIntStateOf(-1)

    fun setVal(linkList: List<Link>) {
        links.clear()
        links.addAll(linkList)
    }

    fun setEditVal(linkList: List<Link>) {
        this.error = -1
        editLinks.clear()
        editLinks.addAll(linkList)
    }

    fun addLink(linkAddress: String, taskId: String, isSaveToo: Boolean = false) {
        var url = if (!linkAddress.startsWith("http://") && !linkAddress.startsWith("https://")) {
            "http://$linkAddress"
        } else {
            linkAddress
        }

        url = url.replace(" ", "")
        url = url.replace("\n", "")
        val address = Uri.parse(url)

        if (address == null
            || address.scheme == null
            || address.host == null
            || !address.host!!.contains(".")
        ) {
            this.error = ErrorsPage.INVALID_URL
            return
        }

        if (!editLinks.any { it.link == url }) {
            this.error = -1

            val link = Link(url, Utils.memberAccessed.value, LocalDateTime.now())
            editLinks.add(link)
            if (isSaveToo) {
                runBlocking {
                    val isAdded = TaskModel().addLink(link, taskId)
                    if (isAdded) {
                        links.add(link)
                    }
                }
            }
        } else {
            this.error = ErrorsPage.LINK_ALREADY_IN_LIST
        }
    }

    fun deleteLinksSynch(link: Link, taskId: String) {
        runBlocking {
            val isDeleted = TaskModel().deleteLink(link, taskId)
            if (isDeleted) {
                links.remove(link)
                editLinks.remove(link)
            }
        }
    }

    fun deleteLink(link: Link) {
        editLinks.remove(link)
    }

    fun areListsEqual(): Boolean {
        if (links.size != editLinks.size) {
            return false
        }

        val sortedList1 = links.sortedBy { it.link }
        val sortedList2 = editLinks.sortedBy { it.link }

        for (i in sortedList1.indices) {
            val link1 = sortedList1[i]
            val link2 = sortedList2[i]
            if (link1 != link2) {
                return false
            }
        }

        return true
    }

}

class CommentListViewModel(commentList: List<Comment>) {
    var comments = mutableStateListOf<Comment>().apply { addAll(commentList) }
        private set

    var editComments = mutableStateListOf<Comment>().apply { addAll(commentList) }
        private set

    var editComment = mutableStateOf("")

    var errorNewComment by mutableIntStateOf(-1)

    fun setVal(commentList: List<Comment>) {
        comments.clear()
        comments.addAll(commentList)
    }

    fun setEditVal(commentList: List<Comment>) {
        this.errorNewComment = -1
        editComments.clear()
        editComments.addAll(commentList)
    }

    fun addComment(comment: Comment) {
        if (comment.comment.isNotBlank()) {
            editComments.add(comment)
            errorNewComment = -1
        } else {
            errorNewComment = R.string.comment_blank
        }
    }

    fun deleteComment(comment: Comment) {
        resetCommentError()
        editComments.remove(comment)
    }

    fun resetCommentError() {
        errorNewComment = -1
    }
}