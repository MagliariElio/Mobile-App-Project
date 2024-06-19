package it.polito.students.showteamdetails.view

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.SoundEffectConstants
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.polito.students.showteamdetails.AppBarTeam
import it.polito.students.showteamdetails.AvatarView
import it.polito.students.showteamdetails.ChatTeamBottomBar
import it.polito.students.showteamdetails.Fixture
import it.polito.students.showteamdetails.GoogleAuthUiClient
import it.polito.students.showteamdetails.HorizontalLine
import it.polito.students.showteamdetails.ImageView
import it.polito.students.showteamdetails.MainActivity
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.factories.ChatModelFactory
import it.polito.students.showteamdetails.factories.UserModelFactory
import it.polito.students.showteamdetails.routers.HomeRouter
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.ChatViewModel
import it.polito.students.showteamdetails.viewmodel.ProfileFormViewModel
import it.polito.students.showteamdetails.viewmodel.TeamListViewModel
import it.polito.students.showteamdetails.viewmodel.TeamViewModel
import it.polito.students.showteamdetails.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomePage(
    applicationContext: MainActivity,
    intent: Intent? = null,
    googleAuthUiClient: GoogleAuthUiClient,
    chatViewModel: ChatViewModel = viewModel(factory = ChatModelFactory(LocalContext.current)),
    userViewModel: UserViewModel = viewModel(factory = UserModelFactory(LocalContext.current)),
) {
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
    // Navigation Parameters
    val navController = rememberNavController()
    val routerActions = remember(navController) { RouterActions(navController) }

    Fixture.RouterActionsProvider.initialize(routerActions)

    val individualChats by chatViewModel.individualChats.collectAsState()
    val groupChats by chatViewModel.groupChats.collectAsState()
    val neighborUsers by userViewModel.neighborUsers.collectAsState()
    val allUsers by userViewModel.allUsers.collectAsState()
    var i by remember { mutableStateOf(1) }


    val resetUsers = fun(newUsers: List<Member>) {
        newUsers.forEach { newUser ->
            val index = neighborUsers.indexOfFirst { u -> u.id == newUser.id }
            if (index > 0) {
                neighborUsers[index].reviews = newUser.reviews
            }
        }
        newUsers.forEach { newUser ->
            val index = allUsers.indexOfFirst { u -> u.id == newUser.id }
            if (index > 0) {
                allUsers[index].reviews = newUser.reviews
            }
        }
        i++
    }

    val teamListVm: TeamListViewModel = viewModel<TeamListViewModel>(viewModelStoreOwner).apply {
        this.routerActions = routerActions
    }

    // ho provato ad eliminare queste due righe ma poi non funziona la top app bar mostrando le label, quindi stare attenti se si tolgono queste due righe
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    Log.d("HomeRouter", "Current route : ${navBackStackEntry?.destination?.route}")


    // Handle Language Change
    Utils.changeLanguage(applicationContext, teamListVm.lang.collectAsState().value)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    /*
    val view = LocalView.current

    val scopeCoroutine = CoroutineScope(Dispatchers.Default)

    // TODO: testing task db
    val taskModel = TaskModel()

    val commentModel = CommentModel()

    val userModel = UserModel()
    val users = userModel.getAllUsers()

    val teamModel = TeamModel()

    val chat = ChatModel()

    LaunchedEffect(Unit) {
        scopeCoroutine.launch {
            taskModel.deleteAllTasks()
            teamModel.deleteAllTeams()


            //val teamAdded = teamModel.addTeam(Fixture.team1Entity)

            //val teamGet = teamModel.getTeamById(teamAdded?.id!!)
            //Log.d("SUCCESS", "$teamGet")

            //val task = taskModel.getTaskById("0PCX6Mfbh6CN3nvqtap5")
            //Log.i("SUCCESS", "$task")


            //val task = Fixture.task1Entity
            //teamModel.addTask(task, teamAdded?.id!!)

            /*teamModel.getAllTeamsByMemberAccessed().collect {
                Log.i("SUCCESS", it.toString())
            }*/

            /*
            commentModel.deleteAllComments()

            taskModel.deleteAllTasksByTeamId(Fixture.task1Entity.teamId)

            val taskFirebaseAdded = taskModel.addTask(Fixture.task1Entity)

            val comment = Fixture.comment1
            comment.taskId = taskFirebaseAdded?.id!!
            val comment2 = Fixture.comment2
            comment2.taskId = taskFirebaseAdded?.id!!
            commentModel.addComment(comment)
            commentModel.addComment(comment2)

            val link = Fixture.link3
            taskModel.addLink(link, taskFirebaseAdded?.id!!)
            taskModel.deleteLink(link, taskFirebaseAdded?.id!!)

            taskModel.updateStatus(Utils.StatusEnum.DONE, taskFirebaseAdded?.id!!)
            */
            /*commentModel.getAllCommentsByTaskId(taskFirebaseAdded?.id!!).collect {
                println(it)
            }*/

            /*
            taskModel.getTaskById(taskFirebaseAdded?.id!!).collect {
                it?.linkList?.forEach { document ->
                    Log.i("SUCCESS", document.link)
                }
            }*/

            /*users.collect {list ->
                println(list)
                list.forEach {
                    println(userModel.getUserByDocumentId(it.id))
                }
            }*/
        }

    }
    // TODO
     */

    BackHandler(onBack = {
        //Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
        val navBackStackEntry = navController.previousBackStackEntry
        val teamId = navBackStackEntry?.arguments?.getString("teamId")
        val teamVm = teamListVm.teamList.value.find { it.teamField.id == teamId }

        // Exit camera mode if necessary
        if (teamVm != null && teamVm.isOpenedCamera) {
            teamVm.exitCameraMode()
        }

        val currentRoute = navController.currentBackStackEntry?.destination?.route
        val profileRoutes =
            setOf("${Utils.RoutesEnum.PROFILE.name}/edit", Utils.RoutesEnum.PROFILE.name)

        if (currentRoute in profileRoutes) {
            routerActions.goBackUntilRouteDifferentFrom(profileRoutes)

        } else {
            routerActions.goBack()
        }
    })

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                LazyColumn {
                    item {
                        val navBackStackEntry = navController.currentBackStackEntry
                        val teamId =
                            navBackStackEntry?.arguments?.getString("teamId")
                        val currentTeams by teamListVm.teamList.collectAsState()
                        val teamVm = currentTeams.find { it.teamField.id == teamId }
                        if (teamVm != null) {
                            ModelDrawerContentTeam(teamVm, routerActions, drawerState)
                            HorizontalLine()
                        }

                        ModelDrawerContentUser(routerActions, drawerState, teamListVm)
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 15.dp, vertical = 26.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.version) + ": ${Fixture.NUMBER_VERSION}",
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }) {
        Scaffold(
            floatingActionButton = {
                if (navController.currentBackStackEntry?.destination?.route == Utils.RoutesEnum.HOME_TEAM.name
                ) {
                    ExtendedFloatingActionButton(
                        text = { Text(stringResource(R.string.add_team)) },
                        icon = { Icon(Icons.Filled.Add, null) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            routerActions.navigateToAddTeam()
                        },
                        containerColor = colorResource(id = R.color.primary_color),
                        contentColor = colorResource(id = R.color.white)
                    )
                } else if (navController.currentBackStackEntry?.destination?.route == Utils.RoutesEnum.HOME_CHAT.name) {
                    ExtendedFloatingActionButton(
                        text = { Text(stringResource(R.string.add_chat)) },
                        icon = { Icon(Icons.Filled.Add, null) },
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            teamListVm.searchedChat = ""
                            teamListVm.searchedChatTextField = ""

                            routerActions.navigateToNewChatPage()
                        },
                        containerColor = colorResource(id = R.color.primary_color),
                        contentColor = colorResource(id = R.color.white)
                    )
                }
            },
            topBar = {
                if (googleAuthUiClient.getSignedInUser() != null &&
                    googleAuthUiClient.getSignedInUser()?.email == Utils.getmemberAccessed().value.email
                ) {
                    AppBarTeam(drawerState, teamListVm = teamListVm, navController, routerActions)
                }
            },
            bottomBar = {
                if (navController.currentBackStackEntry?.destination?.route == Utils.RoutesEnum.HOME_CHAT.name || navController.currentBackStackEntry?.destination?.route == Utils.RoutesEnum.HOME_TEAM.name) {
                    ChatTeamBottomBar(
                        teamListVm = teamListVm,
                        routerActions = routerActions,
                        individualChats = individualChats,
                        groupChats = groupChats,
                        actualRoute = navController.currentBackStackEntry?.destination?.route
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                HomeRouter(
                    googleAuthUiClient,
                    navController,
                    applicationContext,
                    routerActions,
                    teamListVm,
                    individualChats,
                    groupChats,
                    neighborUsers,
                    allUsers,
                    resetUsers,
                    i,
                    intent = intent
                )

            }
        }
    }
}

@Composable
fun ModelDrawerContentTeam(
    teamVm: TeamViewModel,
    routerActions: RouterActions,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope()
    val closeDrawer = { scope.launch { drawerState.close() } }

    Row(
        modifier = Modifier
            .padding(start = 16.dp, top = 20.dp, end = 16.dp)
            .fillMaxWidth()
            .clickable {
                routerActions.navigateToTeam(
                    teamVm.teamField.id
                ); closeDrawer()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (teamVm.teamField.picture.bitmapView.value == null) {
            Icon(
                painter = painterResource(id = R.drawable.company),
                contentDescription = stringResource(id = R.string.team_picture_profile),
                modifier = Modifier.size(50.dp),
                tint = Color.Unspecified
            )
        } else {
            ImageView(
                painterBitmap = teamVm.teamField.picture.bitmapView.value,
                size = 50.dp
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = teamVm.teamField.nameField.value,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
    Spacer(modifier = Modifier.height(15.dp))
    DrawerIcon(icon = R.drawable.icon_task, text = R.string.tasks) {
        routerActions.navigateToTeam(
            teamVm.teamField.id
        ); closeDrawer()
    }
    DrawerIcon(
        icon = R.drawable.icon_statistics,
        text = R.string.statistics
    ) { closeDrawer(); routerActions.navigateToStatistics(teamVm.teamField.id) }
    DrawerIcon(
        icon = R.drawable.icon_settings,
        text = R.string.team_settings
    ) { closeDrawer(); routerActions.navigateToEditTeam(teamVm.teamField.id) }
    DrawerIcon(
        icon = R.drawable.icon_qr_code,
        text = R.string.share_qr_code
    ) { closeDrawer(); routerActions.navigateToQrCodeSharePage(teamVm.teamField.id) }
    DrawerIcon(
        icon = R.drawable.icon_requests,
        text = R.string.requests
    ) { closeDrawer(); routerActions.navigateToRequests(teamVm.teamField.id) }
}

@Composable
fun ModelDrawerContentUser(
    routerActions: RouterActions,
    drawerState: DrawerState,
    teamListVm: TeamListViewModel
) {
    val scope = rememberCoroutineScope()
    val closeDrawer = { scope.launch { drawerState.close() } }

    val greenBackground = colorResource(R.color.green)
    val darkGrayBackground = colorResource(R.color.darkGray)
    val grayBackground = colorResource(R.color.gray)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 20.dp, end = 16.dp)
            .clickable {
                closeDrawer(); routerActions.navigateToProfile()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val memberAccessedValue = ProfileFormViewModel(Utils.memberAccessed.collectAsState().value)

        Box(
            modifier = Modifier
                .size(45.dp)
                .background(grayBackground, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (memberAccessedValue.imageField.bitmapView.value == null) {
                AvatarView(
                    name = memberAccessedValue.nameField.value,
                    surname = memberAccessedValue.surnameField.value,
                    size = 50
                )
            } else {
                ImageView(
                    painterBitmap = memberAccessedValue.imageField.bitmapView.value,
                    size = 50.dp
                )
            }
            // Online/Offline status
            Box(
                modifier = Modifier
                    .padding(start = 5.dp, bottom = 0.dp)
                    .size(9.dp)
                    .clip(CircleShape)
                    .align(Alignment.BottomStart)
                    .background(color = if (memberAccessedValue.onlineSwitchField.value) greenBackground else darkGrayBackground)
            )
        }

        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = "${memberAccessedValue.nameField.value} ${memberAccessedValue.surnameField.value}",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
    Spacer(modifier = Modifier.height(15.dp))
    DrawerIcon(icon = R.drawable.icon_team, text = R.string.teams_title) {
        teamListVm.bottomAppButtonSelected = Utils.TypeOfPage.TEAMS
        closeDrawer(); routerActions.navigateToHomeTeam()
    }
    DrawerIcon(icon = R.drawable.icon_chat, text = R.string.chat) {
        teamListVm.bottomAppButtonSelected = Utils.TypeOfPage.CHAT
        closeDrawer(); routerActions.navigateToHomeChat()
    }
    DrawerIcon(
        icon = R.drawable.icon_user,
        text = R.string.profile
    ) { closeDrawer(); routerActions.navigateToProfile() }
    DrawerIcon(
        icon = R.drawable.icon_settings,
        text = R.string.account_settings
    ) { closeDrawer(); routerActions.navigateToAccountSettings() }
    DrawerIcon(
        icon = R.drawable.icon_about_us,
        text = R.string.about_us
    ) { closeDrawer(); routerActions.navigateToAboutUs() }
    ChangeLanguageDrawer(teamListVm = teamListVm)

    DrawerIcon(
        icon = R.drawable.icon_logout, text = R.string.log_out
    ) { closeDrawer(); routerActions.navigateToLogOut() }

}

@Composable
fun DrawerIcon(icon: Int, text: Int, navigateOnClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp)
            .fillMaxWidth()
            .clickable { navigateOnClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            tint = Color.Unspecified,
            contentDescription = null,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            stringResource(text),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeLanguageDrawer(teamListVm: TeamListViewModel) {
    val context = LocalContext.current
    val view = LocalView.current
    var isLanguageMenuExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isLanguageMenuExpanded,
        onExpandedChange = {
            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
            isLanguageMenuExpanded = it
        }) {

        Row(
            modifier = Modifier
                .padding(20.dp)
                .width(150.dp)
                .menuAnchor(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Utils.getFlagResourceId(teamListVm.lang.value)),
                tint = Color.Unspecified,
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
            Spacer(modifier = Modifier.width(27.dp))
            Text(
                text = teamListVm.lang.value.displayLanguage.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                },
                fontWeight = FontWeight.SemiBold
            )
        }

        ExposedDropdownMenu(
            expanded = isLanguageMenuExpanded,
            onDismissRequest = { isLanguageMenuExpanded = false },
        ) {
            Utils.languageList/*.filter { it != teamListVm.lang.value }*/.map { language ->
                DropdownMenuItem(
                    modifier = Modifier.background(
                        if (language == teamListVm.lang.value) {
                            colorResource(id = R.color.primary_color).copy(alpha = 0.6f)
                        } else Color.Transparent
                    ),
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(
                                    id = Utils.getFlagResourceId(
                                        language
                                    )
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = language.displayLanguage.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.getDefault()
                                    ) else it.toString()
                                }
                            )
                        }
                    },
                    onClick = {
                        isLanguageMenuExpanded = false
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        Utils.changeLanguage(context, language)
                        teamListVm.editLanguage(language)
                        val activity = context as? android.app.Activity
                        activity?.recreate()
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
