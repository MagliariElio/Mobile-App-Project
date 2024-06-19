package it.polito.students.showteamdetails.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.CreatedInfo
import it.polito.students.showteamdetails.entity.MemberInfoTeam
import it.polito.students.showteamdetails.entity.Message
import it.polito.students.showteamdetails.entity.Team
import it.polito.students.showteamdetails.model.TeamModel
import it.polito.students.showteamdetails.model.UserModel
import it.polito.students.showteamdetails.routers.RouterActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Locale

class TeamListViewModel(
    val teamModel: TeamModel = TeamModel(),
    val userModel: UserModel = UserModel()
) : ViewModel() {
    lateinit var routerActions: RouterActions

    private val _teamList = MutableStateFlow<MutableList<TeamViewModel>>(mutableListOf())
    val teamList: StateFlow<MutableList<TeamViewModel>> = _teamList.asStateFlow()

    // This avoid if the default language does not exist in the language list
    private val _lang = MutableStateFlow<Locale>(Locale.getDefault())
    val lang: StateFlow<Locale> = _lang.asStateFlow()

    init {
        viewModelScope.launch {
            Utils.getmemberAccessed().collect { member ->
                _lang.value = member.language
            }
        }
    }

    fun editLanguage(newLang: Locale) {
        viewModelScope.launch {
            _lang.value = newLang
            userModel.editLanguageOption(newLang)
        }
    }

    var searchedChat by mutableStateOf("")
    var searchedChatTextField by mutableStateOf("")

    init {
        loadTeams()
    }

    private fun loadTeams() {
        viewModelScope.launch {
            try {
                Log.d("SUCCESS", "chiamata")
                teamModel.getAllTeamsByMemberAccessed().collect { teams ->
                    Log.d("SUCCESS", "loadTeams: ${teams.size}")
                    _teamList.value = teams
                    isTeamsListLoading = false
                }
            } finally {
                isTeamsListLoading = false
            }
        }
    }

    fun getEmptyTeamViewModel(): TeamViewModel {
        return TeamViewModel(
            Team(
                id = Utils.generateUniqueId(),
                name = "",
                picture = null,
                membersList = listOf(),
                category = R.string.category_work,
                created = CreatedInfo(Utils.memberAccessed.value, LocalDateTime.now()),
                description = "",
                requestsList = listOf(),
                tasksList = emptyList()
            ),
            routerActions = routerActions
        )
    }

    var isTeamsListLoading by mutableStateOf(true)

    var bottomAppButtonSelected by mutableStateOf(Utils.TypeOfPage.TEAMS)

    var newTextMessage by mutableStateOf("")
    var messageMenuOpen by mutableStateOf<Message?>(null)
    var editingMessage by mutableStateOf<Message?>(null)
    var recomposition by mutableIntStateOf(0) //Do NOT remove, just to update the view after message editing
    var deleteMessageModalOpen by mutableStateOf<Message?>(null)

    fun deleteTeam(teamVm: TeamViewModel) {
        teamList.value.remove(teamVm)
        viewModelScope.launch {
            teamModel.deleteTeam(teamVm.getTeam())
        }
    }

    fun addTeam(team: TeamViewModel) {
        teamList.value.add(team)
        viewModelScope.launch {
            teamModel.addTeam(team.getTeam())
        }
    }

    fun updateTeam(teamVm: TeamViewModel) {
        viewModelScope.launch {
            teamModel.updateTeam(teamVm.getTeam())
        }
    }

    // TODO: controllare se funziona, ci potrebbero essere dei problemi nei delegati del task del team
    fun leaveMemberByTeam(member: MemberInfoTeam, team: TeamViewModel) {
        viewModelScope.launch {
            val isRemoved = teamModel.leaveTeam(member, team.getTeam())
            if (isRemoved) {
                teamList.value.remove(team)
            }
        }
    }

    fun joinTeamNewMember(teamId: String, memberId: String) {
        viewModelScope.launch {
            teamModel.addTeamJoinRequests(teamId, memberId)
        }
    }

}