package it.polito.students.showteamdetails.routers

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import it.polito.students.showteamdetails.DeleteTeamAlertDialog
import it.polito.students.showteamdetails.EditPaneWithCamera
import it.polito.students.showteamdetails.GoogleAuthUiClient
import it.polito.students.showteamdetails.MainActivity
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.RegisterPage
import it.polito.students.showteamdetails.SignInScreen
import it.polito.students.showteamdetails.SignInViewModel
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.Chat
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.RoleEnum
import it.polito.students.showteamdetails.model.UserModel
import it.polito.students.showteamdetails.view.AboutUs
import it.polito.students.showteamdetails.view.AccountSettingsPage
import it.polito.students.showteamdetails.view.AfterQRScanning
import it.polito.students.showteamdetails.view.ChatPage
import it.polito.students.showteamdetails.view.HomeChatsPage
import it.polito.students.showteamdetails.view.HomeTeamsPage
import it.polito.students.showteamdetails.view.LeaveTeam
import it.polito.students.showteamdetails.view.ModifyTaskPage
import it.polito.students.showteamdetails.view.NewChatPage
import it.polito.students.showteamdetails.view.PreviewRequest
import it.polito.students.showteamdetails.view.ProfileScreen
import it.polito.students.showteamdetails.view.RemoveMember
import it.polito.students.showteamdetails.view.Requests
import it.polito.students.showteamdetails.view.RoleAssignation
import it.polito.students.showteamdetails.view.ShareTeamPage
import it.polito.students.showteamdetails.view.StatisticsPage
import it.polito.students.showteamdetails.view.TeamPage
import it.polito.students.showteamdetails.view.ViewTaskPage
import it.polito.students.showteamdetails.viewmodel.ProfileFormViewModel
import it.polito.students.showteamdetails.viewmodel.TeamListViewModel
import it.polito.students.showteamdetails.viewmodel.TeamViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

@Composable
fun HomeRouter(
    googleAuthUiClient: GoogleAuthUiClient,
    navController: NavHostController,
    applicationContext: MainActivity,
    routerActions: RouterActions,
    teamListVm: TeamListViewModel,
    individualChats: List<Chat>,
    groupChats: List<Chat>,
    neighborUsers: List<Member>,
    allUsers: List<Member>,
    resetUsers: (List<Member>) -> Unit,
    i: Number,
    intent: Intent? = null
) {
    println(i)
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val token = stringResource(id = R.string.default_web_client_id)

    val signInViewModel = viewModel<SignInViewModel>()
    val state by signInViewModel.state.collectAsStateWithLifecycle(
        lifecycleOwner = lifecycleOwner
    )
    val loading by signInViewModel.loading.collectAsStateWithLifecycle(
        lifecycleOwner = lifecycleOwner
    )
    val isNeededRegistration by signInViewModel.isNeededRegistration.collectAsStateWithLifecycle(
        lifecycleOwner = lifecycleOwner
    )

    var user by remember { mutableStateOf(Firebase.auth.currentUser) }

    val launcherAuth = rememberFirebaseAuthLauncher(
        onAuthComplete = { result, isNeededRegistrationValue ->
            user = result.user
            signInViewModel.setIsNeededRegistration(isNeededRegistrationValue)
        },
        onAuthError = {
            Log.d("GoogleAuth", "ERRORE")
            user = null
        },
        onBack = { signInViewModel.resetState() },
        teamListVm = teamListVm,
        routerActions = routerActions
    )


    NavHost(navController = navController, startDestination = Utils.RoutesEnum.LOGIN.name) {

        //LOGIN
        composable(Utils.RoutesEnum.LOGIN.name) {
            Log.d("HomeRouter", Utils.RoutesEnum.LOGIN.name)

            val scope = rememberCoroutineScope()

            /*LaunchedEffect(googleAuthUiClient.getSignedInUser()) {
                //se l'utente è già loggato si va alla homepage

                if (googleAuthUiClient.getSignedInUser() != null) {
                    routerActions.navigateToHomeTeam()
                    signInViewModel.resetState()
                }
            }*/

            /*val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        lifecycleOwner.lifecycleScope.launch {
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            signInViewModel.onSignInResult(signInResult)
                        }
                    } else {
                        signInViewModel.setLoading(false)
                        Log.d(
                            "GoogleAuth",
                            "Sign-in was canceled with resultCode: ${result.resultCode}"
                        )
                    }
                }
            )*/

            /*LaunchedEffect(key1 = state.isSignInSuccessful) {
                if (state.isSignInSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Sign in successful",
                        Toast.LENGTH_LONG
                    ).show()

                    signInViewModel.resetState()
                    routerActions.navigateToHomeTeam()
                }
            }*/

            if (googleAuthUiClient.getSignedInUser() == null) {
                SignInScreen(
                    state = state,
                    loading = loading,
                    onBack = {
                        signInViewModel.resetState()
                    },
                    onSignInClick = {
                        val gso =
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(token)
                                .requestEmail()
                                .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        signInViewModel.setLoading(true)
                        launcherAuth.launch(googleSignInClient.signInIntent)
                    }
                )
            } else if (!isNeededRegistration) {
                var isMemberLoaded by remember { mutableStateOf(false) }

                CircularProgressIndicator(modifier = Modifier.padding(50.dp))

                LaunchedEffect(Unit) {
                    scope.launch {
                        val member =
                            UserModel().getUserByEmail(googleAuthUiClient.getSignedInUser()?.email!!)
                                ?: Utils.memberAccessed.value
                        Utils.setMemberAccessed(member)
                        isMemberLoaded = true
                        // routerActions.navigateToHomeTeam() // Moved to the bottom
                    }
                }

                if (isMemberLoaded) {

                    // Handle After QR Code Scanning
                    if (intent?.action == Intent.ACTION_VIEW) {
                        intent.data?.let { uri ->
                            val teamId = uri.lastPathSegment
                            val expiryString = uri.getQueryParameter("expiry")
                            if (teamId != null) {
                                if (expiryString != null) {
                                    val expiryDate = Utils.parseIsoStringDate(expiryString)
                                    if (expiryDate != null && !Utils.isExpired(expiryDate)) {
                                        LaunchedEffect(teamId) {
                                            routerActions.navigateToConfirmJoinTeam(teamId)
                                            return@LaunchedEffect
                                        }
                                    } else {
                                        // Handle expired link (e.g., show an error message)
                                        val linkExpiredText = stringResource(R.string.link_expired)
                                        LaunchedEffect(Unit) {
                                            Toast.makeText(
                                                applicationContext,
                                                linkExpiredText,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        routerActions.navigateToHomeTeam()
                                    }
                                } else {
                                    // Handle link without expiry
                                    LaunchedEffect(teamId) {
                                        routerActions.navigateToConfirmJoinTeam(teamId)
                                        return@LaunchedEffect
                                    }
                                }
                            }
                        }
                    } else {
                        routerActions.navigateToHomeTeam()
                    }

                }

            }
        }

        //LOGOUT
        composable(Utils.RoutesEnum.LOGOUT.name) {
            Log.d("HomeRouter", Utils.RoutesEnum.LOGOUT.name)

            runBlocking {
                googleAuthUiClient.signOut()
                signInViewModel.resetState()
                routerActions.navigateToLogIn()
            }
        }

        //REGISTER
        composable(Utils.RoutesEnum.REGISTER.name) {
            Log.d("HomeRouter", Utils.RoutesEnum.REGISTER.name)

            RegisterPage(routerActions, teamListVm, userData = googleAuthUiClient.getSignedInUser())
        }

        // HOME CHAT
        composable(Utils.RoutesEnum.HOME_CHAT.name) {
            Log.d("HomeRouter", Utils.RoutesEnum.HOME_CHAT.name)

            HomeChatsPage(routerActions, individualChats, groupChats, teamListVm = teamListVm)
        }

        // CHAT PAGE
        composable("${Utils.RoutesEnum.CHAT_PAGE.name}/{chatId}") {
            Log.d("HomeRouter", "${Utils.RoutesEnum.CHAT_PAGE.name}/{chatId}")
            val chatId = it.arguments?.getString("chatId")

            val openChat = (individualChats + groupChats).find { it.id == chatId!! }
            if (openChat != null)
                ChatPage(openChat, teamListVm = teamListVm, routerActions)
        }

        composable(Utils.RoutesEnum.NEW_CHAT_PAGE.name) {
            Log.d("HomeRouter", Utils.RoutesEnum.NEW_CHAT_PAGE.name)

            NewChatPage(
                teamListVm = teamListVm,
                routerActions = routerActions,
                neighborUsers = neighborUsers,
                individualChats = individualChats
            )
        }

        // TEAMS
        composable(Utils.RoutesEnum.HOME_TEAM.name) {
            Log.d("HomeRouter", Utils.RoutesEnum.HOME_TEAM.name)

            HomeTeamsPage(routerActions, teamListVm = teamListVm)
        }

        composable("${Utils.RoutesEnum.TEAM_PAGE.name}/{teamId}") { backStackEntry ->
            Log.d("HomeRouter", Utils.RoutesEnum.TEAM_PAGE.name)

            val teamId = backStackEntry.arguments?.getString("teamId")

            val currentTeams by teamListVm.teamList.collectAsState()
            val teamVm = currentTeams.find { it.teamField.id == teamId }

            if (teamVm != null) {
                TeamPage(
                    teamVm = teamVm,
                    selectTask = routerActions.navigateToModifyTask,
                    backStackEntry,
                    routerActions
                )
            }
        }

        composable(Utils.RoutesEnum.ADD_TEAM.name) {
            Log.d("HomeRouter", Utils.RoutesEnum.ADD_TEAM.name)

            val newTeam = remember { teamListVm.getEmptyTeamViewModel() }

            EditPaneWithCamera(
                applicationContext = applicationContext,
                isTeamPage = true,
                teamVm = newTeam,
                routerActions = routerActions,
                teamListVm = teamListVm,
                user = null
            )
        }

        composable("${Utils.RoutesEnum.MODIFY_TEAM.name}/{teamId}") { backStackEntry ->
            Log.d("HomeRouter", Utils.RoutesEnum.MODIFY_TEAM.name)

            val teamId = backStackEntry.arguments?.getString("teamId")
            val currentTeams by teamListVm.teamList.collectAsState()
            val teamVm = currentTeams.find { it.teamField.id == teamId }

            if (teamVm != null) {
                EditPaneWithCamera(
                    applicationContext,
                    isTeamPage = true,
                    teamVm = teamVm,
                    routerActions = routerActions,
                    teamListVm = teamListVm,
                    user = null
                )
            }
        }

        composable("${Utils.RoutesEnum.TEAM_STATISTICS.name}/{teamId}") { backStackEntry ->
            Log.d("HomeRouter", Utils.RoutesEnum.TEAM_STATISTICS.name)
            val teamId = backStackEntry.arguments?.getString("teamId")
            val currentTeams by teamListVm.teamList.collectAsState()
            val teamVm = currentTeams.find { it.teamField.id == teamId }

            if (teamVm != null) {
                StatisticsPage(
                    teamVm = teamVm,
                    routerActions = routerActions
                )
            }
        }

        composable("${Utils.RoutesEnum.TEAM_REQUESTS.name}/{teamId}") { backStackEntry ->
            Log.d("HomeRouter", Utils.RoutesEnum.TEAM_REQUESTS.name)

            val teamId = backStackEntry.arguments?.getString("teamId")
            val currentTeams by teamListVm.teamList.collectAsState()
            val teamVm = currentTeams.find { it.teamField.id == teamId }

            if (teamVm != null) {
                Requests(teamVm = teamVm, routerActions)
            }

        }

        composable("{teamId}/${Utils.RoutesEnum.TEAM_REQUESTS}/{memberId}") { backStackEntry ->
            Log.d("HomeRouter", "{teamId}/${Utils.RoutesEnum.TEAM_REQUESTS}/{memberId}")

            val teamId = backStackEntry.arguments?.getString("teamId")
            val currentTeams by teamListVm.teamList.collectAsState()
            val teamVm = currentTeams.find { it.teamField.id == teamId }

            val memberId = backStackEntry.arguments?.getString("memberId")

            if (teamVm != null) {
                val member = allUsers.find { it.id == memberId.toString() }

                if (member != null) {
                    PreviewRequest(
                        member = Pair(member, RoleEnum.NO_ROLE_ASSIGNED),
                        teamVm = teamVm,
                        routerActions
                    )
                }
            }
        }

        composable("{teamId}/${Utils.RoutesEnum.ROLE_ASSIGNATION_REQUEST_MEMBER}/{memberId}") { backStackEntry ->
            Log.d(
                "HomeRouter",
                "{teamId}/${Utils.RoutesEnum.ROLE_ASSIGNATION_REQUEST_MEMBER}/{memberId}"
            )

            val teamId = backStackEntry.arguments?.getString("teamId") ?: ""
            val memberId = backStackEntry.arguments?.getString("memberId")

            var teamVm by remember { mutableStateOf<TeamViewModel?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(teamId) {
                teamVm = teamListVm.teamModel.getTeamById(teamId)
                isLoading = false
            }

            val member = memberId?.let { id ->
                allUsers.find { it.id == id }
            }

            if (isLoading) {
                // Show a loading indicator
                CircularProgressIndicator(modifier = Modifier.padding(top = 50.dp))
            } else {
                if (teamVm != null && member != null) {
                    RoleAssignation(
                        teamListVm = teamListVm,
                        member = member,
                        teamVm = teamVm!!,
                        routerActions = routerActions,
                    )
                } else {
                    // Show an error message or handle the null case
                    Text(stringResource(R.string.unable_to_load_data_team_and_member))
                }
            }
        }


        composable("{teamId}/${Utils.RoutesEnum.ROLE_ASSIGNATION_REQUEST_TEAM}/{memberId}") { backStackEntry ->
            Log.d(
                "HomeRouter",
                "{teamId}/${Utils.RoutesEnum.ROLE_ASSIGNATION_REQUEST_TEAM}/{memberId}"
            )

            val teamId = backStackEntry.arguments?.getString("teamId")
            val memberId = backStackEntry.arguments?.getString("memberId")

            if (teamId != null && memberId != null) {
                teamListVm.joinTeamNewMember(teamId, memberId)
                routerActions.navigateToHomeTeam()
            } else {
                // Show an error message or handle the null case
                Text(stringResource(R.string.unable_to_load_data_team_and_member))
            }
        }

        composable("${Utils.RoutesEnum.TEAM_QRCODE_SHARE_PAGE.name}/{teamId}") { backStackEntry ->
            Log.d("HomeRouter", Utils.RoutesEnum.TEAM_QRCODE_SHARE_PAGE.name)

            val teamId = backStackEntry.arguments?.getString("teamId")
            val currentTeams by teamListVm.teamList.collectAsState()
            val teamVm = currentTeams.find { it.teamField.id == teamId }

            if (teamVm != null) {
                ShareTeamPage(teamVm, routerActions)
            }
        }

        composable("${Utils.RoutesEnum.TEAM_CONFIRM_JOIN.name}/{teamId}") { backStackEntry ->
            Log.d("HomeRouter", Utils.RoutesEnum.TEAM_CONFIRM_JOIN.name)

            val teamId = backStackEntry.arguments?.getString("teamId") ?: ""

            var teamVm by remember { mutableStateOf<TeamViewModel?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(teamId) {
                teamVm = teamListVm.teamModel.getTeamById(teamId)
                isLoading = false
            }

            if (isLoading) {
                // Show a loading indicator
                CircularProgressIndicator(modifier = Modifier.padding(top = 50.dp))
            } else {
                if (teamVm != null) {
                    AfterQRScanning(teamVm!!, routerActions)
                } else {
                    // Show an error message or handle the null case
                    Text(stringResource(R.string.unable_to_load_data_team_and_member))
                }
            }
        }

        composable("${Utils.RoutesEnum.TEAM_PAGE.name}/{teamId}/${Utils.RoutesEnum.LEAVE_TEAM.name}") { backStackEntry ->
            Log.d("HomeRouter", Utils.RoutesEnum.LEAVE_TEAM.name)

            val teamId = backStackEntry.arguments?.getString("teamId")
            val currentTeams by teamListVm.teamList.collectAsState()
            val teamVm = currentTeams.find { it.teamField.id == teamId }

            if (teamVm != null) {
                LeaveTeam(
                    teamListVm = teamListVm,
                    teamVm = teamVm,
                    isDeleteTeamPage = false,
                    routerActions = routerActions
                )
            }
        }

        composable("${Utils.RoutesEnum.TEAM_PAGE.name}/{teamId}/${Utils.RoutesEnum.DELETE_TEAM.name}") { backStackEntry ->
            Log.d("HomeRouter", Utils.RoutesEnum.DELETE_TEAM.name)

            val teamId = backStackEntry.arguments?.getString("teamId")
            val currentTeams by teamListVm.teamList.collectAsState()
            val teamVm = currentTeams.find { it.teamField.id == teamId }

            if (teamVm != null) {
                LeaveTeam(
                    teamListVm = teamListVm,
                    teamVm = teamVm,
                    isDeleteTeamPage = true,
                    routerActions = routerActions
                )
            }
        }

        // TASK
        composable("${Utils.RoutesEnum.TEAM_PAGE.name}/{teamId}/${Utils.RoutesEnum.VIEW_TASK.name}/{taskId}") { backStackEntry ->
            Log.d("HomeRouter", Utils.RoutesEnum.VIEW_TASK.name)

            val taskId = backStackEntry.arguments?.getString("taskId")
            val currentTeams by teamListVm.teamList.collectAsState()
            val taskVm = currentTeams.flatMap { it.tasksList }.find { it.id == taskId }
            val teamVm = currentTeams.find { it.tasksList.contains(taskVm) }

            if (teamVm != null && taskVm != null) {
                taskVm.stateTab = 0 // reset to the info tab

                ViewTaskPage(
                    taskVm = taskVm,
                    teamVm = teamVm,
                    applicationContext = applicationContext,
                    routerActions
                )
            }
        }

        composable("${Utils.RoutesEnum.TEAM_PAGE.name}/{teamId}/${Utils.RoutesEnum.MODIFY_TASK.name}/{taskId}") { backStackEntry ->
            Log.d("HomeRouter", Utils.RoutesEnum.MODIFY_TASK.name)

            val teamId = backStackEntry.arguments?.getString("teamId")
            val taskId = backStackEntry.arguments?.getString("taskId")

            val currentTeams by teamListVm.teamList.collectAsState()
            val teamVm = currentTeams.find { it.teamField.id == teamId }
            val taskVm = teamVm?.tasksList?.find { it.id == taskId }

            if (teamVm != null && taskVm != null) {
                ModifyTaskPage(taskVm, teamVm, applicationContext, routerActions)
            }
        }

        composable("${Utils.RoutesEnum.TEAM_PAGE.name}/{teamId}/${Utils.RoutesEnum.CREATE_TASK.name}") { backStackEntry ->
            Log.d(
                "HomeRouter",
                "${Utils.RoutesEnum.TEAM_PAGE.name}/{teamId}/${Utils.RoutesEnum.CREATE_TASK.name}"
            )

            val teamId = backStackEntry.arguments?.getString("teamId")
            val currentTeams by teamListVm.teamList.collectAsState()

            val teamVm = currentTeams.find { it.teamField.id == teamId }
            val taskVm = teamVm?.getEmptyTaskViewModel()

            if (teamVm != null && taskVm != null) {
                ModifyTaskPage(taskVm, teamVm, applicationContext, routerActions)
            }
        }

        // PROFILE
        composable(Utils.RoutesEnum.PROFILE.name) {
            Log.d("HomeRouter", Utils.RoutesEnum.PROFILE.name)
            val user by Utils.memberAccessed.collectAsState()

            ProfileScreen(
                user = user,
                routerActions = routerActions,
                teamListVm = teamListVm,
                resetUsers = resetUsers,
                allUsers = allUsers
            )
        }

        composable("${Utils.RoutesEnum.PROFILE.name}/edit") {
            Log.d("HomeRouter", "${Utils.RoutesEnum.PROFILE.name}/edit")
            val user by Utils.memberAccessed.collectAsState()
            val profileVm = ProfileFormViewModel(user)

            EditPaneWithCamera(
                applicationContext = applicationContext,
                routerActions = routerActions,
                teamListVm = teamListVm,
                profileVm = profileVm,
                user = user
            )
        }

        composable(Utils.RoutesEnum.ACCOUNT_SETTINGS.name) {
            Log.d("HomeRouter", Utils.RoutesEnum.ACCOUNT_SETTINGS.name)
            val profileFormVm = Utils.profileMemberAccessed.collectAsState().value



            AccountSettingsPage(
                profileFormVm = profileFormVm,
                routerActions = routerActions
            )
        }

        composable("${Utils.RoutesEnum.MEMBER_PROFILE.name}/{memberId}") { backStackEntry ->
            Log.d("HomeRouter", Utils.RoutesEnum.MEMBER_PROFILE.name)

            val memberId = backStackEntry.arguments?.getString("memberId")
            val member = allUsers.find { it.id == memberId.toString() }

            if (member != null) {
                ProfileScreen(
                    user = member,
                    routerActions = routerActions,
                    teamListVm = teamListVm,
                    resetUsers,
                    allUsers
                )
            }
        }

        composable(Utils.RoutesEnum.ABOUTUS.name) {
            Log.d("HomeRouter", Utils.RoutesEnum.ABOUTUS.name)

            AboutUs()
        }

        dialog("${Utils.RoutesEnum.DELETE_TEAM.name}/{teamId}") { backStackEntry ->
            Log.d("HomeRouter", "${Utils.RoutesEnum.DELETE_TEAM.name}/{teamId}")

            val teamId = backStackEntry.arguments?.getString("teamId")
            val currentTeams by teamListVm.teamList.collectAsState()
            val teamVm = currentTeams.find { it.teamField.id == teamId }

            if (teamVm != null) {
                DeleteTeamAlertDialog(
                    team = teamVm,
                    onDeleteConfirmed = {
                        teamListVm.deleteTeam(teamVm)
                        routerActions.navigateToHomeTeam()
                    },
                    onDismissRequest = {
                        routerActions.navigateToHomeTeam()
                    }
                )
            }
        }

        composable("${Utils.RoutesEnum.TEAM_PAGE}/{teamId}/${Utils.RoutesEnum.DELETE_TEAM_MEMBER.name}/{memberId}") { backStackEntry ->
            Log.d(
                "HomeRouter",
                "${Utils.RoutesEnum.TEAM_PAGE}/{teamId}/${Utils.RoutesEnum.DELETE_TEAM_MEMBER.name}/{memberId}"
            )

            val teamId = backStackEntry.arguments?.getString("teamId")
            val currentTeams by teamListVm.teamList.collectAsState()
            val teamVm = currentTeams.find { it.teamField.id == teamId }

            if (teamVm != null) {
                val memberId = backStackEntry.arguments?.getString("memberId")
                val member =
                    teamVm.teamField.membersField.members
                        .find { it.profile.id == memberId.toString() }

                if (member != null) {
                    RemoveMember(member = member, teamVm = teamVm, routerActions = routerActions)
                }
            }
        }
    }
}

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult, Boolean) -> Unit,
    onAuthError: (ApiException) -> Unit,
    onBack: () -> Unit,
    teamListVm: TeamListViewModel,
    routerActions: RouterActions
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()

    BackHandler {
        onBack()
    }

    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d("GoogleAuth", "account ${account.email}")

            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            var memberAccessedGoogle: Member? = null

            scope.launch {
                try {
                    val authResult = Firebase.auth.signInWithCredential(credential).await()

                    memberAccessedGoogle =
                        teamListVm.userModel.getUserByEmail(authResult?.user?.email ?: "")

                    Log.d(
                        "GoogleAuth",
                        "mail: ${authResult?.user?.email}, membro : $memberAccessedGoogle"
                    )

                    onAuthComplete(authResult, memberAccessedGoogle == null)

                    if (memberAccessedGoogle == null) {
                        Log.d("GoogleAuth", "NUOVO MEMBRO")

                        routerActions.navigateToRegister()

                    } else {
                        Log.d("GoogleAuth", "ESISTE")

                        Utils.setMemberAccessed(memberAccessedGoogle!!)
                        routerActions.navigateToHomeTeam()
                    }

                } catch (e: Exception) {
                    Log.d("GoogleAuth", e.toString())
                    onAuthError(e as ApiException)
                }
            }
        } catch (e: ApiException) {
            Log.d("GoogleAuth", e.toString())
            onAuthError(e)
        }
    }
}
