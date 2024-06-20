package it.polito.students.showteamdetails.viewmodel

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.students.showteamdetails.Fixture
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.CreatedInfo
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.MemberInfoTeam
import it.polito.students.showteamdetails.entity.RoleEnum
import it.polito.students.showteamdetails.entity.Task
import it.polito.students.showteamdetails.entity.Team
import it.polito.students.showteamdetails.model.TeamModel
import it.polito.students.showteamdetails.routers.RouterActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TeamViewModel(
    team: Team,
    val routerActions: RouterActions,
    val teamModel: TeamModel = TeamModel()
) : ViewModel() {

    private var _isTasksListLoading = MutableStateFlow(true)
    val isTasksListLoading: StateFlow<Boolean> = _isTasksListLoading.asStateFlow()

    private var taskCache: MutableList<TaskViewModel>? = null
    private val _tasksList = MutableStateFlow<MutableList<TaskViewModel>>(mutableListOf())
    val tasksList: StateFlow<MutableList<TaskViewModel>> = _tasksList.asStateFlow()

    fun loadTasks() {
        viewModelScope.launch {
            _isTasksListLoading.value = true
            if (taskCache != null) {
                _tasksList.value = taskCache!!
                _isTasksListLoading.value = false
                Log.d("SUCCESS", "Loaded tasks from cache: ${taskCache!!.size}")
            } else {
                try {
                    Log.d("SUCCESS", "teamId: ${teamField.id}")
                    teamModel.taskModel.getAllTasksByTeamId(teamId = teamField.id)
                        .collect { tasks ->
                            taskCache = tasks // Update the cache
                            _tasksList.value = tasks
                            _isTasksListLoading.value = false
                            Log.d("SUCCESS", "Loaded tasks from DB: ${tasks.size}")
                        }
                } finally {
                    _isTasksListLoading.value = false
                }
            }
        }
    }

    /*val numberCompletedTask by derivedStateOf {
        tasksList.value.count {
            it.statusField.status == Utils.StatusEnum.DONE && (it.creationField.createdBy.id == Utils.memberAccessed.value.id ||
                    it.delegateTasksField.members.any { p -> p.profile.id == Utils.memberAccessed.value.id })
        }
    }

    val numberAssignedTask by derivedStateOf {
        tasksList.value.count {
            it.creationField.createdBy.id == Utils.memberAccessed.value.id ||
                    it.delegateTasksField.members.any { p -> p.profile.id == Utils.memberAccessed.value.id }
        }
    }

    val grade by derivedStateOf {
        val numberAssignedTask = if (numberAssignedTask == 0) 1 else numberAssignedTask
        (numberCompletedTask * 10) / numberAssignedTask
    }*/

    private val _numberCompletedTasks = MutableStateFlow(0)
    val numberCompletedTask: StateFlow<Int> = _numberCompletedTasks.asStateFlow()

    private val _numberAssignedTasks = MutableStateFlow(0)
    val numberAssignedTask: StateFlow<Int> = _numberAssignedTasks.asStateFlow()

    val grade by derivedStateOf {
        val numberAssignedTask = if (numberAssignedTask.value == 0) 1 else numberAssignedTask.value
        (numberCompletedTask.value * 10) / numberAssignedTask
    }

    fun loadTaskCounts() {
        viewModelScope.launch {
            try {
                val userId = Utils.memberAccessed.value.id
                _numberCompletedTasks.value =
                    teamModel.taskModel.getNumberCompletedTasksByUser(userId)
                _numberAssignedTasks.value =
                    teamModel.taskModel.getNumberAssignedTasksByUser(userId)
                Log.d(
                    "SUCCESS",
                    "Completed tasks: ${numberCompletedTask.value}, Assigned tasks: ${numberAssignedTask.value}"
                )
            } catch (e: Exception) {
                Log.e("ERROR", "Error loading task counts", e)
            }
        }
    }

    var teamField = TeamFieldViewModel(
        team.id,
        team.name,
        team.picture,
        team.membersList,
        team.category,
        team.created,
        team.requestsList,
    )

    fun addTask(task: TaskViewModel) {
        viewModelScope.launch {
            val taskInserted = teamModel.addTask(task.getTask(), teamField.id)
            if (taskInserted != null) {
                val taskInsertedVm = TaskViewModel(
                    task = taskInserted,
                    routerActions = Fixture.RouterActionsProvider.routerActions
                )
                val history = taskInsertedVm.createNewHistoryLine(
                    taskInsertedVm.creationField.dateCreation,
                    R.string.created_task
                )
                taskInsertedVm.historyListField.addHistory(history)
                _tasksList.value.add(taskInsertedVm)
                taskCache?.add(taskInsertedVm)
            }

        }
    }

    fun addTeamMember(member: MemberInfoTeam): Boolean {
        var isAdded = true

        viewModelScope.launch {
            runCatching {
                teamModel.db.runTransaction { transaction ->
                    val memberAdded = teamModel.memberInfoTeamModel.addMemberInfoTeamTransaction(
                        transaction,
                        member
                    )
                    if (memberAdded != null) {
                        teamField.membersField.addMember(memberAdded, isRequestsPage = true)

                        if (teamField.membersField.error != -1) {
                            teamModel.memberInfoTeamModel.deleteMemberInfoTeamById(
                                transaction,
                                memberAdded.id
                            )
                            isAdded = false
                        } else {
                            deleteMemberRequest(member.profile)
                        }
                    } else {
                        isAdded = false
                    }
                }
            }
        }

        return isAdded
    }

    // TODO: qui funziona, l'interfaccia non si aggiorna
    fun deleteTask(task: TaskViewModel) {
        viewModelScope.launch {
            openDeleteAlertDialog = null
            teamModel.taskModel.deleteTask(task.getTask())
            _tasksList.value.removeIf { it.id == task.id }
            taskCache?.removeIf { it.id == task.id }
            val newList = currentPageTasks?.toMutableList()
            newList?.removeIf { it.id == task.id }
            currentPageTasks = newList
        }
    }

    fun deleteRecurringTask(task: TaskViewModel) {
        _tasksList.value.forEach {
            if (it.groupID == task.groupID) {
                deleteTask(it)
            }
        }
        _tasksList.value.removeAll { it.groupID == task.groupID }
        taskCache?.removeAll { it.groupID == task.groupID }
    }

    fun deleteMemberRequest(profile: Member) {
        viewModelScope.launch {
            teamField.requestsTeamField.deleteRequest(profile)
            teamModel.updateTeam(getTeam())
        }
    }

    fun deleteMemberTeam(member: MemberInfoTeam) {
        viewModelScope.launch {
            runCatching {
                teamModel.db.runTransaction { transaction ->
                    teamField.membersField.deleteMemberSynch(member)
                    teamModel.memberInfoTeamModel.deleteMemberInfoTeamById(transaction, member.id)
                }
                teamModel.updateTeam(getTeam())
            }
        }
    }

    fun changeRole(member: MemberInfoTeam, role: RoleEnum) {
        teamField.membersField.changeRole(member.profile, role)
        viewModelScope.launch {
            teamModel.memberInfoTeamModel.updateMemberInfoTeamRole(member.id, role)
        }
    }

    fun changeTimePartecipation(
        member: MemberInfoTeam,
        timePartecipation: Utils.TimePartecipationTeamTypes
    ) {
        teamField.membersField.changeTimeParticipation(member.profile, timePartecipation)
        viewModelScope.launch {
            teamModel.memberInfoTeamModel.updateMemberInfoTeamTimePartecipation(
                member.id,
                timePartecipation
            )
        }
    }

    var isOpenedCamera by mutableStateOf(false)
    fun exitCameraMode() {
        isOpenedCamera = false
    }

    val descriptionField = DescriptionFieldViewModel(team.description)

    var selectedTabIndex by mutableIntStateOf(0)
    var searchByNameField by mutableStateOf("")
    var sortField by mutableStateOf(Comparator<TaskViewModel> { _, _ -> 0 })
    var sortingDropdownMenuOpened by mutableStateOf(false)
    var filteringDropdownMenuOpened by mutableStateOf(false)
    var searchByNameTextField by mutableStateOf("")
    var filterTextField by mutableStateOf(Utils.filteringMap.keys.first())
    var sortTextField by mutableStateOf(Utils.orderingMap.keys.first())
    var filterField by mutableStateOf<(TaskViewModel) -> Boolean>({ true })
    var openDeleteAlertDialog: TaskViewModel? by mutableStateOf(null) //TODO("portare nella navigation")
    var changeStatusMenuOpened: TaskViewModel? by mutableStateOf(null) //TODO("portare nella navigation")
    var filterAreaOpened by mutableStateOf(false)
    var sortAreaOpened by mutableStateOf(false)

    var fromStartDateFilter: LocalDate by mutableStateOf(LocalDate.now().minusDays(31))
    var toStartDateFilter: LocalDate by mutableStateOf(LocalDate.now().plusDays(30))

    var fromDueDateFilter: LocalDate by mutableStateOf(LocalDate.now().minusDays(31))
    var toDueDateFilter: LocalDate by mutableStateOf(LocalDate.now().plusDays(30))
    var fromInputError by mutableStateOf(false)
    var toInputError by mutableStateOf(false)

    var draggedTask: TaskViewModel? by mutableStateOf(null)
    var offsetX by mutableFloatStateOf(0f)

    var currentPageTasks by mutableStateOf<List<TaskViewModel>?>(null)


    fun isModifiedSomething(): Boolean {
        return teamField.nameField.value != teamField.nameField.editValue ||
                teamField.categoryField.category != teamField.categoryField.editCategory ||
                descriptionField.value != descriptionField.editValue ||
                teamField.picture.bitmapView.value != teamField.picture.bitmapEdit.value
    }

    fun validateForm() {
        teamField.nameField.validate()
        descriptionField.validate()
        teamField.picture.validate()

        val willEdit =
            listOf(
                teamField.nameField.error,
                descriptionField.error,
                teamField.categoryField.error,
                teamField.picture.error
            ).none { it != -1 }

        // Verifying if at least one is not empty
        if (willEdit) {
            teamField.nameField.setVal(teamField.nameField.editValue)
            descriptionField.setVal(descriptionField.editValue)
            teamField.categoryField.setVal(teamField.categoryField.editCategory)
            teamField.picture.bitmapView.value = teamField.picture.bitmapEdit.value

            if (routerActions.getCurrentRoute().contains(Utils.RoutesEnum.ADD_TEAM.name)) {
                val memberInfo = MemberInfoTeam(
                    Utils.memberAccessed.value,
                    RoleEnum.EXECUTIVE_LEADER,
                    Utils.TimePartecipationTeamTypes.FULL_TIME
                )
                teamField.membersField.addMember(memberInfo)
                teamField.membersField.members.add(memberInfo)

                teamField.creationField.setCreatedByVal(Utils.memberAccessed.value)
                teamField.creationField.setDateCreationVal(LocalDateTime.now())
            }
        }
    }

    fun areThereUnsavedChanges(): Boolean {
        return !(
                teamField.nameField.value == teamField.nameField.editValue &&
                        descriptionField.value == descriptionField.editValue &&
                        teamField.categoryField.category == teamField.categoryField.editCategory &&
                        teamField.picture.bitmapView.value == teamField.picture.bitmapEdit.value &&
                        teamField.picture.bitmapView.value == teamField.picture.bitmapEdit.value
                )
    }

    fun areThereErrors(): Boolean = (
            teamField.nameField.error != -1 ||
                    descriptionField.error != -1 ||
                    teamField.nameField.error != -1 ||
                    teamField.picture.error != -1
            )

    fun cancelButtonClicked() {  //Come back to the view Task page
        teamField.nameField.editValue = teamField.nameField.value
        descriptionField.editValue = descriptionField.value
        teamField.categoryField.editCategory = teamField.categoryField.category
        teamField.picture.bitmapEdit.value = teamField.picture.bitmapEdit.value

        // Reset errors to an empty string
        teamField.nameField.error = -1
        descriptionField.error = -1
        teamField.categoryField.error = -1

    }

    fun canEditBy(): Boolean {
        return teamField.membersField.members.any {
            it.profile.id == Utils.memberAccessed.value.id
                    && it.role == RoleEnum.EXECUTIVE_LEADER
        }
    }

    fun getTeam(): Team {
        return Team(
            id = teamField.id,
            name = teamField.nameField.value,
            picture = teamField.picture.bitmapView.value,
            membersList = teamField.membersField.members,
            category = teamField.categoryField.category,
            created = CreatedInfo(
                teamField.creationField.createdBy,
                teamField.creationField.dateCreation
            ),
            description = descriptionField.value,
            requestsList = teamField.requestsTeamField.requests,
        )
    }

    fun getEmptyTaskViewModel(): TaskViewModel {
        return TaskViewModel(
            Task(
                id = Utils.generateUniqueId(),
                groupID = Utils.generateUniqueId(),
                title = "",
                status = Utils.StatusEnum.PENDING,
                startDate = LocalDate.now(),
                startTime = LocalTime.now(),
                dueDate = LocalDate.now().plusDays(1),
                dueTime = LocalTime.now(),
                repeat = Utils.RepeatEnum.NO_REPEAT,
                repeatEndDate = LocalDate.now(),
                description = "",
                category = Utils.CategoryEnum.ADMINISTRATIVE,
                tagList = emptyList(),
                fileList = emptyList(),
                linkList = emptyList(),
                commentList = emptyList(),
                delegateList = emptyList(),
                historyList = emptyList(),
                created = CreatedInfo(Utils.memberAccessed.value, LocalDateTime.now())
            ),
            routerActions = routerActions
        )
    }

    class TeamFieldViewModel(
        id: String,
        nameParam: String,
        pictureParam: ImageBitmap?,
        membersList: List<MemberInfoTeam>,
        categoryParam: Int,
        created: CreatedInfo,
        requestsList: List<Member>,
    ) {
        val id = id
        var nameField = TitleFieldViewModel(nameParam)
        var categoryField = CategoryTeamFieldViewModel(categoryParam)

        /*var picture by mutableStateOf(pictureParam)
            private set*/
        var picture = ProfileImageViewModel(pictureParam)
        var membersField = MembersParticipationViewModel(membersList)
        val creationField = CreationFieldViewModel(created.member, created.timestamp)
        val requestsTeamField = NewRequestsTeamViewModel(requestsList)
    }
}

class CategoryTeamFieldViewModel(category: Int) {
    var category by mutableIntStateOf(category)
        private set

    var editCategory by mutableIntStateOf(category)

    fun setVal(categoryParam: Int) {
        category = categoryParam
    }

    var error by mutableIntStateOf(-1)
}

class NewRequestsTeamViewModel(requestsList: List<Member>) {

    var requests = mutableStateListOf<Member>().apply { addAll(requestsList) }
        private set

    var editRequests = mutableStateListOf<Member>().apply { addAll(requestsList) }
        private set

    fun deleteRequest(member: Member) {
        requests.remove(member)
        editRequests.remove(member)
    }
}
