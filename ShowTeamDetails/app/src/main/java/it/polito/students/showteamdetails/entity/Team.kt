package it.polito.students.showteamdetails.entity

import androidx.compose.ui.graphics.ImageBitmap
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import it.polito.students.showteamdetails.Fixture
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.model.TaskModel
import it.polito.students.showteamdetails.model.UserModel
import it.polito.students.showteamdetails.viewmodel.TaskViewModel
import java.time.LocalDateTime

data class Team(
    var id: String = "",
    val name: String = "",
    val picture: ImageBitmap? = null,
    var membersList: List<MemberInfoTeam> = emptyList(),
    val category: Int = R.string.category_work,
    val created: CreatedInfo = CreatedInfo(Utils.memberAccessed.value, LocalDateTime.now()),
    val description: String = "",
    val requestsList: List<Member> = emptyList(),
    var tasksList: List<TaskViewModel> = emptyList(),
)

class TeamFirebase {
    lateinit var id: String
    lateinit var name: String
    var pictureUrl: String? = null
    lateinit var membersList: List<DocumentReference>
    var category: Int = R.string.category_work
    lateinit var created: CreatedInfoFirebase
    lateinit var description: String
    lateinit var requestsList: List<DocumentReference>
    lateinit var tasksList: List<DocumentReference>
}

fun Team.toFirebase(): TeamFirebase {
    val team = TeamFirebase()
    team.id = id
    team.name = name
    team.pictureUrl = if (picture != null) "team_${id}.jpg" else null
    team.membersList = convertMemberInfoTeamListToMemberInfoTeamFirebaseList(membersList)
    team.category = category
    team.created = created.toFirebase()
    team.description = description
    team.requestsList = requestsList.map { profile ->
        Firebase.firestore.collection(Utils.CollectionsEnum.users.name).document(profile.id)
    }
    team.tasksList = tasksList.map {
        Firebase.firestore.collection(Utils.CollectionsEnum.tasks.name).document(it.id)
    }

    return team
}

suspend fun TeamFirebase.toTeam(usersList: List<Member>): Team {
    val createdBy =
        usersList.find { user -> user.id == this.created.member.id } ?: Utils.memberAccessed.value

    val tasksListFlow =
        this.tasksList.mapNotNull { task -> TaskModel().getTaskById(task.id, usersList) }
    val tasksList = tasksListFlow.map { task ->
        //convertFlowListToTaskList(tasksListFlow).map { task ->
        TaskViewModel(
            task = task,
            routerActions = Fixture.RouterActionsProvider.provideRouterActions()
        )
    }

    val requestsListMember =
        this.requestsList.map { request ->
            usersList.find { user -> user.id == request.id } ?: Utils.memberAccessed.value
        }

    return Team(
        id = id,
        name = name,
        picture = if (pictureUrl != null) UserModel().loadPhotoFromStorage(pictureUrl!!) else null,
        membersList = convertMemberInfoTeamFirebaseListToMemberInfoTeamList(membersList),
        category = category,
        created = created.toCreatedInfo(createdBy),
        description = description,
        requestsList = requestsListMember,
        tasksList = tasksList
    )
}