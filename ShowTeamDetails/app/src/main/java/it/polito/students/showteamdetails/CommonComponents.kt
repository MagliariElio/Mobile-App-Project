package it.polito.students.showteamdetails

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.MediaPlayer
import android.view.SoundEffectConstants
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import it.polito.students.showteamdetails.entity.Chat
import it.polito.students.showteamdetails.entity.Comment
import it.polito.students.showteamdetails.entity.File
import it.polito.students.showteamdetails.entity.Link
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.MemberInfoTeam
import it.polito.students.showteamdetails.entity.RoleEnum
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.view.AvatarEdit
import it.polito.students.showteamdetails.view.CAMERAX_PERMISSIONS
import it.polito.students.showteamdetails.view.CameraViewComponent
import it.polito.students.showteamdetails.view.ChooseMenuComponent
import it.polito.students.showteamdetails.view.CommentModifyCard
import it.polito.students.showteamdetails.view.EditProfile
import it.polito.students.showteamdetails.view.ImageProfile
import it.polito.students.showteamdetails.view.ModifyTeamPage
import it.polito.students.showteamdetails.view.TeamButtons
import it.polito.students.showteamdetails.view.rotateBitmap
import it.polito.students.showteamdetails.view.takePhotoCustom
import it.polito.students.showteamdetails.viewmodel.ProfileFormViewModel
import it.polito.students.showteamdetails.viewmodel.ProfileImageViewModel
import it.polito.students.showteamdetails.viewmodel.TaskViewModel
import it.polito.students.showteamdetails.viewmodel.TeamListViewModel
import it.polito.students.showteamdetails.viewmodel.TeamViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun isLandscape(): Boolean {
    //To check if we are in portrait or landscape mode
    val configuration = LocalConfiguration.current
    return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarTeam(
    drawerState: DrawerState,
    teamListVm: TeamListViewModel,
    navController: NavHostController,
    routerActions: RouterActions
) {
    val scope = rememberCoroutineScope()

    val imageSize = 50.dp
    val paddingImage = 2.dp
    val borderImage = 0.2.dp

    suspend fun changeDrawer() {
        if (drawerState.isOpen) {
            drawerState.close()
        } else {
            drawerState.open()
        }
    }

    CenterAlignedTopAppBar(
        title = {
            val navBackStackEntry = navController.currentBackStackEntry
            val teamId = navBackStackEntry?.arguments?.getString("teamId")

            val currentTeams by teamListVm.teamList.collectAsState()
            val teamVm = currentTeams.find { it.teamField.id == teamId }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (navController.currentBackStackEntry?.destination?.route?.contains(Utils.RoutesEnum.TEAM_CONFIRM_JOIN.name) == true) {
                    Spacer(modifier = Modifier.weight(3f))
                    Text(
                        text = stringResource(id = R.string.join),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else if (navController.currentBackStackEntry?.destination?.route?.contains(Utils.RoutesEnum.LEAVE_TEAM.name) == true) {
                    Spacer(modifier = Modifier.weight(3f))
                    Text(
                        text = stringResource(id = R.string.leave_team),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else if (navController.currentBackStackEntry?.destination?.route?.contains(Utils.RoutesEnum.DELETE_TEAM.name) == true) {
                    Spacer(modifier = Modifier.weight(3f))
                    Text(
                        text = stringResource(id = R.string.delete_team),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else if (teamVm != null) {
                    Spacer(modifier = Modifier.weight(3f))
                    Text(
                        text = teamVm.teamField.nameField.value,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    if (teamVm.teamField.picture.bitmapView.value != null) {
                        Image(
                            bitmap = teamVm.teamField.picture.bitmapView.value!!,
                            contentDescription = stringResource(R.string.image_profile),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(imageSize)
                                .border(
                                    BorderStroke(borderImage, Color.Black),
                                    CircleShape
                                )
                                .padding(paddingImage)
                                .clip(CircleShape)
                        )
                    } else if (teamVm.teamField.picture.bitmapEdit.value != null) {
                        Image(
                            bitmap = teamVm.teamField.picture.bitmapEdit.value!!,
                            contentDescription = stringResource(R.string.image_profile),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(imageSize)
                                .border(
                                    BorderStroke(borderImage, Color.Black),
                                    CircleShape
                                )
                                .padding(paddingImage)
                                .clip(CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.company),
                            contentDescription = stringResource(R.string.image_profile),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(imageSize)
                                .border(
                                    BorderStroke(borderImage, Color.Black),
                                    CircleShape
                                )
                                .padding(paddingImage)
                                .clip(CircleShape)
                        )
                    }
                } else if (navController.currentBackStackEntry?.destination?.route in listOf(
                        Utils.RoutesEnum.HOME_TEAM.name,
                        Utils.RoutesEnum.ADD_TEAM.name
                    )
                ) {
                    Spacer(modifier = Modifier.weight(3f))
                    Text(
                        text = stringResource(id = R.string.teams_title),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else if (navController.currentBackStackEntry?.destination?.route?.split("/")
                        ?.get(0) in listOf(
                        Utils.RoutesEnum.HOME_CHAT.name,
                        Utils.RoutesEnum.CHAT_PAGE.name,
                        Utils.RoutesEnum.NEW_CHAT_PAGE.name
                    )
                ) {
                    Spacer(modifier = Modifier.weight(3f))
                    Text(
                        text = stringResource(id = R.string.chat),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else if (navController.currentBackStackEntry?.destination?.route?.contains(Utils.RoutesEnum.MEMBER_PROFILE.name) == true) {
                    Spacer(modifier = Modifier.weight(3f))
                    Text(
                        text = stringResource(id = R.string.profile),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else if (navController.currentBackStackEntry?.destination?.route?.contains(Utils.RoutesEnum.PROFILE.name) == true) {
                    Spacer(modifier = Modifier.weight(3f))
                    Text(
                        text = stringResource(id = R.string.profile),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (navController.currentBackStackEntry?.destination?.route?.endsWith("edit") == false) {
                        IconButton(onClick = { routerActions.enterProfileEditMode() }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(id = R.string.edit_profile_information),
                            )
                        }
                    }
                } else if (navController.currentBackStackEntry?.destination?.route?.contains(Utils.RoutesEnum.ACCOUNT_SETTINGS.name) == true) {
                    Spacer(modifier = Modifier.weight(3f))
                    Text(
                        text = stringResource(id = R.string.settings),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else if (navController.currentBackStackEntry?.destination?.route == Utils.RoutesEnum.ABOUTUS.name) {
                    Spacer(modifier = Modifier.weight(3f))
                    Text(
                        text = stringResource(id = R.string.about_us),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.blue),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = {
                scope.launch { changeDrawer() }
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.open_drawer),
                )
            }
        },
        actions = {}
    )
}

@Composable
fun ChatTeamBottomBar(
    teamListVm: TeamListViewModel,
    routerActions: RouterActions,
    individualChats: List<Chat>,
    groupChats: List<Chat>,
    actualRoute: String?
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(colorResource(id = R.color.secondary_color))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .background(
                    if (actualRoute == Utils.RoutesEnum.HOME_CHAT.name) colorResource(
                        id = R.color.primary_color
                    ) else colorResource(
                        id = R.color.secondary_color
                    )
                )
                .height(60.dp)
                .clickable {
                    teamListVm.bottomAppButtonSelected = Utils.TypeOfPage.TEAMS
                    routerActions.navigateToHomeChat()
                },
            contentAlignment = Alignment.Center
        ) {

            val unreadMessages =
                (individualChats.flatMap { it.messages } + groupChats.flatMap { it.messages }).filter { !it.read }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Chat button",
                    tint = if (actualRoute == Utils.RoutesEnum.HOME_CHAT.name) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                BadgedBox(
                    badge = {
                        if (unreadMessages.isNotEmpty()) {
                            Badge(
                                modifier = Modifier.offset(10.dp, 0.dp),
                                containerColor = colorResource(id = R.color.attention_color)
                            ) {
                                Text("${unreadMessages.size}")
                            }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.chat),
                        color = if (actualRoute == Utils.RoutesEnum.HOME_CHAT.name
                        ) Color.White else Color.Black,
                        fontSize = 13.sp
                    )
                }
            }

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .background(
                    if (actualRoute == Utils.RoutesEnum.HOME_TEAM.name) colorResource(
                        id = R.color.primary_color
                    ) else colorResource(
                        id = R.color.secondary_color
                    )
                )
                .height(60.dp)
                .clickable {
                    teamListVm.bottomAppButtonSelected =
                        Utils.TypeOfPage.TEAMS; routerActions.navigateToHomeTeam()
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "Teams button",
                    tint = if (actualRoute == Utils.RoutesEnum.HOME_TEAM.name) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.teams_title),
                    color = if (actualRoute == Utils.RoutesEnum.HOME_TEAM.name) Color.White else Color.Black,
                    fontSize = 13.sp
                )
            }
        }
    }

}

@Composable
fun MemberProfileCircle(member: Member, size: Int) {
    if (member.photo != null) {
        ImageView(
            painterBitmap = member.photo,
            size = size.dp
        )
    } else {
        AvatarView(
            name = member.name,
            surname = member.surname,
            size = size
        )
    }
}

@Composable
fun TeamProfileCircle(teamVm: TeamViewModel, size: Int) {
    if (teamVm.teamField.picture.bitmapView.value != null) {
        ImageView(
            painterBitmap = teamVm.teamField.picture.bitmapView.value!!,
            size = size.dp
        )
    } else {
        ImageView(painterId = R.drawable.company, size = size.dp)
    }
}

@Composable
fun AvatarView(name: String, surname: String, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(colorResource(R.color.gray), shape = CircleShape)
            .border(1.dp, Color.Black, CircleShape)
            .padding(2.dp)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name[0].uppercaseChar() + "" + surname[0].uppercaseChar(),
            fontSize = (size - size / 2).sp,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.black)
        )
    }
}

@Composable
fun ImageView(painterBitmap: ImageBitmap? = null, painterId: Int? = null, size: Dp) {
    // Visualization of the image
    if (painterBitmap != null) {
        Image(
            bitmap = painterBitmap,
            contentDescription = stringResource(id = R.string.image_profile),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .border(
                    BorderStroke(0.5.dp, Color.Black),
                    CircleShape
                )
                .padding(2.dp)
                .clip(CircleShape)
        )
    } else if (painterId != null) {
        Image(
            painter = painterResource(id = painterId),
            contentDescription = stringResource(id = R.string.image_profile),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .border(
                    BorderStroke(0.5.dp, Color.Black),
                    CircleShape
                )
                .padding(2.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun SemiBoldText(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 25.sp,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun HorizontalLine() {
    val color = colorResource(R.color.darkGray)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
    ) {
        val halfWidth = size.width / 2

        drawLine(
            color = color,
            start = Offset(halfWidth * 0.25f, size.height),
            end = Offset(halfWidth * 1.75f, size.height),
            strokeWidth = 2f,
        )
    }
}

@Composable
fun ShowToast(message: String) {
    val context = LocalContext.current
    val updatedMessage by rememberUpdatedState(message)

    Toast.makeText(context, updatedMessage, Toast.LENGTH_SHORT).show()
}

@Composable
fun ButtonsSection(
    taskVm: TaskViewModel,
    teamVm: TeamViewModel,
    routerActions: RouterActions
) {
    val view = LocalView.current
    val context = LocalContext.current
    val mediaPlayerDialog: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.dialog)
    val mediaPlayerSave: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.save)
    val mediaPlayerError: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.error)

    val scope = rememberCoroutineScope()

    var openDialog by remember {
        mutableStateOf(false)
    }

    var openRepeatDialog by remember { mutableStateOf(false) }

    var showToast by remember {
        mutableStateOf(false)
    }

    if (showToast) {
        ShowToast(message = stringResource(R.string.resolve_error))
        showToast = false
    }

    BackHandler(onBack = {
        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
        if (routerActions.getCurrentRoute().contains(Utils.RoutesEnum.CREATE_TASK.name)) {
            routerActions.goBack()
            return@BackHandler
        }

        if (routerActions.getCurrentRoute().contains(Utils.RoutesEnum.MODIFY_TASK.name)) {
            if (taskVm.areThereUnsavedChanges()) {
                openDialog = true
            } else {
                taskVm.cancelButtonClicked()
                routerActions.goBack()
            }
        }
    })

    //Cancel and Save buttons
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = {
                //Sound and vibration
                Utils.useSafeMediaPlayer(mediaPlayerDialog)
                Utils.vibrate(context)

                if (routerActions.getCurrentRoute()
                        .contains(Utils.RoutesEnum.CREATE_TASK.name)
                ) {
                    routerActions.goBack()
                    return@OutlinedButton
                }

                if (routerActions.getCurrentRoute()
                        .contains(Utils.RoutesEnum.MODIFY_TASK.name)
                ) {
                    if (taskVm.areThereUnsavedChanges()) {
                        openDialog = true
                    } else {
                        taskVm.cancelButtonClicked()
                        routerActions.goBack()
                    }
                }
            },
            border = BorderStroke(1.dp, Color(0xFF283D8F)),
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                colorResource(R.color.white),
                colorResource(R.color.black)
            )
        ) {
            Text(text = stringResource(id = R.string.cancel))
        }

        Button(
            onClick = {
                // If there are not unsaved changes, it exits from edit or create mode
                if (routerActions.getCurrentRoute()
                        .contains(Utils.RoutesEnum.MODIFY_TASK.name) && !taskVm.areThereUnsavedChanges()
                ) {
                    // Sound and vibration
                    Utils.useSafeMediaPlayer(mediaPlayerSave)
                    Utils.vibrate(context)
                    routerActions.goBack()
                    return@Button
                }

                taskVm.validateForm()
                val areThereErrors = taskVm.areThereErrors()

                if (areThereErrors) {
                    // Sound and vibration
                    Utils.useSafeMediaPlayer(mediaPlayerError)
                    Utils.vibrateTickEffect(context)

                    showToast = true
                }

                if (routerActions.getCurrentRoute()
                        .contains(Utils.RoutesEnum.MODIFY_TASK.name) && !areThereErrors
                ) {
                    if (taskVm.repeatField.editValue != Utils.RepeatEnum.NO_REPEAT) {
                        Utils.useSafeMediaPlayer(mediaPlayerDialog)
                        openRepeatDialog = true
                    } else {
                        // Sound and vibration
                        Utils.useSafeMediaPlayer(mediaPlayerSave)
                        Utils.vibrate(context)
                    }

                    // aggiorna il task nel db
                    taskVm.updateTask(taskVm)

                    routerActions.goBack()
                }

                if (routerActions.getCurrentRoute()
                        .contains(Utils.RoutesEnum.CREATE_TASK.name) && !areThereErrors
                ) {

                    // Sound and vibration
                    Utils.useSafeMediaPlayer(mediaPlayerSave)
                    Utils.vibrate(context)

                    if (taskVm.repeatField.editValue != Utils.RepeatEnum.NO_REPEAT) {
                        taskVm.createRepeatedTasks(taskVm, teamVm)
                    } else {
                        scope.launch {
                            teamVm.addTask(taskVm)  // aggiunge il task al db
                        }
                    }
                    routerActions.goBack()
                }

            },
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                colorResource(R.color.blue),
                colorResource(R.color.white)
            )
        ) {
            Text(text = stringResource(id = R.string.save))
        }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    openDialog = false
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.unsaved_changes))
                    }
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.confirm_discard_changes)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            openDialog = false
                            openRepeatDialog = false
                            taskVm.cancelButtonClicked()
                            routerActions.goBack()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.discard_changes))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            openDialog = false
                            taskVm.editRepeatedTasks(taskVm, teamVm)
                        },
                        border = BorderStroke(1.dp, colorResource(R.color.blue)),
                    ) {
                        Text(text = stringResource(R.string.keep_editing))
                    }
                }
            )
        }
    }

    if (openRepeatDialog) {
        AlertDialog(
            onDismissRequest = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                openRepeatDialog = false
            },
            title = {
                Text(text = stringResource(id = R.string.recurring_task))
            },
            text = {
                Text(stringResource(id = R.string.change_recurring_tasks))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        Utils.useSafeMediaPlayer(mediaPlayerSave)
                        openRepeatDialog = false
                        taskVm.editRepeatedTasks(taskVm, teamVm) //Edit all recurring tasks
                    }
                ) {
                    Text(stringResource(id = R.string.all_recurring_tasks))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        //Edit only this recurring task
                        Utils.useSafeMediaPlayer(mediaPlayerSave)
                        openRepeatDialog = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.only_this_task))
                }
            }
        )
    }
}

@Composable
fun ButtonsTeamSection(
    teamVm: TeamViewModel,
    teamListVm: TeamListViewModel,
    routerActions: RouterActions
) {
    val view = LocalView.current
    val context = LocalContext.current
    val mediaPlayerDialog: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.dialog)
    val mediaPlayerSave: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.save)
    val mediaPlayerError: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.error)

    var openDialog by remember { mutableStateOf(false) }
    var openRepeatDialog by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }

    if (showToast) {
        ShowToast(message = stringResource(R.string.resolve_error))
        showToast = false
    }

    BackHandler(onBack = {
        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)

        if (routerActions.getCurrentRoute().contains(Utils.RoutesEnum.CREATE_TASK.name)) {
            routerActions.goBack()
            return@BackHandler
        }

        if (routerActions.getCurrentRoute().contains(Utils.RoutesEnum.MODIFY_TEAM.name)) {
            if (teamVm.areThereUnsavedChanges()) {
                openDialog = true
            } else {
                teamVm.cancelButtonClicked()
                routerActions.goBack()
            }
        }

        if (routerActions.getCurrentRoute().contains(Utils.RoutesEnum.ADD_TEAM.name)) {
            if (teamVm.areThereUnsavedChanges()) {
                openDialog = true
            } else {
                teamVm.cancelButtonClicked()
                routerActions.goBack()
            }
        }
    })

    //Cancel and Save buttons
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = {
                //Sound and vibration
                Utils.useSafeMediaPlayer(mediaPlayerDialog)
                Utils.vibrate(context)

                if (routerActions.getCurrentRoute()
                        .contains(Utils.RoutesEnum.CREATE_TASK.name)
                ) {
                    routerActions.goBack()
                }

                if (routerActions.getCurrentRoute()
                        .contains(Utils.RoutesEnum.MODIFY_TEAM.name)
                ) {
                    if (teamVm.areThereUnsavedChanges()) {
                        openDialog = true
                    } else {
                        teamVm.cancelButtonClicked()
                        routerActions.goBack()
                    }
                }

                if (routerActions.getCurrentRoute().contains(Utils.RoutesEnum.ADD_TEAM.name)) {
                    if (teamVm.areThereUnsavedChanges()) {
                        openDialog = true
                    } else {
                        teamVm.cancelButtonClicked()
                        routerActions.goBack()
                    }
                }
            },
            border = BorderStroke(1.dp, Color(0xFF283D8F)),
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                colorResource(R.color.white),
                colorResource(R.color.black)
            )
        ) {
            Text(text = stringResource(id = R.string.cancel))
        }

        Button(
            onClick = {

                // If there are not unsaved changes, it exits from edit or create mode
                if (routerActions.getCurrentRoute()
                        .contains(Utils.RoutesEnum.MODIFY_TEAM.name) && !teamVm.areThereUnsavedChanges()
                ) {
                    // Sound and vibration
                    Utils.useSafeMediaPlayer(mediaPlayerSave)
                    Utils.vibrate(context)

                    return@Button
                }

                teamVm.validateForm()
                val areThereErrors = teamVm.areThereErrors()

                if (areThereErrors) {
                    // Sound and vibration
                    Utils.useSafeMediaPlayer(mediaPlayerError)
                    Utils.vibrateTickEffect(context)

                    showToast = true
                }

                if (routerActions.getCurrentRoute()
                        .contains(Utils.RoutesEnum.MODIFY_TEAM.name) && !areThereErrors
                ) {

                    // Aggiorna il team nel db
                    teamListVm.updateTeam(teamVm)

                    // Sound and vibration
                    Utils.useSafeMediaPlayer(mediaPlayerSave)
                    Utils.vibrate(context)
                }

                if (routerActions.getCurrentRoute()
                        .contains(Utils.RoutesEnum.ADD_TEAM.name) && !areThereErrors
                ) {
                    // Sound and vibration
                    Utils.useSafeMediaPlayer(mediaPlayerSave)
                    Utils.vibrate(context)

                    // Aggiunge il team al db
                    teamListVm.addTeam(teamVm)

                    routerActions.navigateToHomeTeam()
                }
            },
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                colorResource(R.color.blue),
                colorResource(R.color.white)
            )
        ) {
            Text(text = stringResource(id = R.string.save))
        }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    openDialog = false
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.unsaved_changes))
                    }
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.confirm_discard_changes)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            openDialog = false
                            openRepeatDialog = false
                            teamVm.cancelButtonClicked()
                            routerActions.goBack()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.discard_changes))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            openDialog = false
                        },
                        border = BorderStroke(1.dp, colorResource(R.color.blue)),
                    ) {
                        Text(text = stringResource(R.string.keep_editing))
                    }
                }
            )
        }
    }

}

@Composable
fun ButtonsProfileSection(
    routerActions: RouterActions,
    profileFormVm: ProfileFormViewModel,
    user: Member,
    isRegister: Boolean,
    teamListVm: TeamListViewModel
) {
    val view = LocalView.current
    val context = LocalContext.current
    val mediaPlayerDialog: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.dialog)
    val mediaPlayerSave: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.save)
    val mediaPlayerError: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.error)

    var openDialog by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }

    if (showToast) {
        ShowToast(message = stringResource(R.string.resolve_error))
        showToast = false
    }

    BackHandler(onBack = {
        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)

        if (profileFormVm.areThereUnsavedChanges()) {
            openDialog = true
        } else {
            profileFormVm.cancelButtonClicked()
        }
    })

    //Cancel and Save buttons
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = {
                //Sound and vibration
                Utils.useSafeMediaPlayer(mediaPlayerDialog)
                Utils.vibrate(context)

                if (profileFormVm.areThereUnsavedChanges()) {
                    openDialog = true
                } else {
                    profileFormVm.cancelButtonClicked()
                    routerActions.navigateToProfile()
                }

            },
            border = BorderStroke(1.dp, Color(0xFF283D8F)),
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                colorResource(R.color.white),
                colorResource(R.color.black)
            )
        ) {
            Text(text = stringResource(id = R.string.cancel))
        }

        Button(
            onClick = {

                // If there are not unsaved changes, it exits from edit or create mode
                if (!profileFormVm.areThereUnsavedChanges()) {
                    // Sound and vibration
                    Utils.useSafeMediaPlayer(mediaPlayerSave)
                    Utils.vibrate(context)
                    routerActions.navigateToProfile()
                    return@Button
                }

                if (!isRegister)
                    profileFormVm.validateForm()
                else
                    profileFormVm.validateFormRegister()

                val areThereErrors = if (isRegister) {
                    profileFormVm.areThereErrorsInRegister()
                } else {
                    profileFormVm.areThereErrors()
                }

                if (areThereErrors) {
                    // Sound and vibration
                    Utils.useSafeMediaPlayer(mediaPlayerError)
                    Utils.vibrateTickEffect(context)

                    showToast = true
                } else {
                    if (!isRegister) {
                        val updatedUser = Member(
                            id = user.id,
                            photo = profileFormVm.imageField.bitmapView.value,
                            name = profileFormVm.nameField.value,
                            surname = profileFormVm.surnameField.value,
                            email = profileFormVm.emailField.value,
                            online = profileFormVm.onlineSwitchField.value,
                            password = profileFormVm.newPasswordField.editValue,
                            gender = profileFormVm.genderField.value,
                            birthdate = user.birthdate,
                            nickname = user.nickname,
                            bio = profileFormVm.bioField.value,
                            address = profileFormVm.addressField.value,
                            phoneNumber = profileFormVm.phoneNumberField.value,
                            technicalSkills = profileFormVm.technicalSkillsField.value,
                            languages = profileFormVm.languageSkillsField.value,
                            reviews = profileFormVm.reviewsField.reviews,
                            privateEmail = profileFormVm.emailField.privateValueField,
                            privateGender = profileFormVm.genderField.privateValueField,
                            privateBirthdate = profileFormVm.birthdateField.privateValueField,
                            privatePhone = profileFormVm.phoneNumberField.privateValueField,
                            sound = user.sound,
                            vibration = user.vibration,
                            language = teamListVm.lang.value
                        )
                        Utils.setMemberAccessed(updatedUser)
                        profileFormVm.editUser(updatedUser)
                        routerActions.navigateToProfile()
                    } else {
                        //Registration page
                        val newUser = Member(
                            id = user.id,
                            photo = null,
                            name = profileFormVm.nameField.editValue,
                            surname = profileFormVm.surnameField.editValue,
                            email = user.email,
                            online = profileFormVm.onlineSwitchField.editValue,
                            password = "",
                            gender = profileFormVm.genderField.editGender,
                            birthdate = profileFormVm.birthdateField.editDate,
                            nickname = profileFormVm.nicknameField.editValue,
                            bio = "",
                            address = profileFormVm.addressField.editValue,
                            phoneNumber = profileFormVm.phoneNumberField.editValue,
                            technicalSkills = emptyList(),
                            languages = emptyList(),
                            reviews = emptyList(),
                            privateEmail = false,
                            privateGender = false,
                            privateBirthdate = false,
                            privatePhone = false,
                            sound = false,
                            vibration = false,
                            language = Locale("en")
                        )
                        profileFormVm.createUser(newUser)
                        routerActions.navigateToHomeTeam()
                    }
                }
            },
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                colorResource(R.color.blue),
                colorResource(R.color.white)
            )
        ) {
            Text(text = stringResource(id = R.string.save))
        }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    openDialog = false
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.unsaved_changes))
                    }
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.confirm_discard_changes)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            profileFormVm.cancelButtonClicked()
                            if (isRegister) {
                                routerActions.navigateToLogOut()
                            } else
                                routerActions.navigateToProfile()

                            openDialog = false
                        }
                    ) {
                        Text(text = stringResource(id = R.string.discard_changes))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            openDialog = false
                        },
                        border = BorderStroke(1.dp, colorResource(R.color.blue)),
                    ) {
                        Text(text = stringResource(R.string.keep_editing))
                    }
                }
            )
        }
    }
}

@Composable
fun MemberDetailRow(
    taskVm: TaskViewModel?,
    memberInfoTeam: MemberInfoTeam,
    isDeleteActive: Boolean,
    onRoleChange: ((RoleEnum) -> Unit)? = null,
    onTimeParticipationChange: ((Utils.TimePartecipationTeamTypes) -> Unit)? = null,
    onDelete: ((MemberInfoTeam) -> Unit)? = null,
    isMemberTeamSettingsPage: Boolean = false,
    routerActions: RouterActions,
    teamId: String
) {
    val view = LocalView.current
    val mediaPlayerDisappear: MediaPlayer? =
        Utils.loadMediaPlayer(LocalContext.current, R.raw.disappear)
    val isMenuRoleExpanded = remember { mutableStateOf(false) }
    val isMenuTimeParticipationExpanded = remember { mutableStateOf(false) }

    val greenBackground = colorResource(R.color.green)
    val darkGrayBackground = colorResource(R.color.darkGray)
    val grayBackground = colorResource(R.color.gray)

    val memberAccessedValue by remember { mutableStateOf(Utils.memberAccessed.value) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { routerActions.navigateToMemberProfile(memberInfoTeam.profile.id) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val member = memberInfoTeam.profile
                val role = memberInfoTeam.role
                val timeParticipation = memberInfoTeam.participationType

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(grayBackground, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (member.photo != null) {
                            ImageView(
                                painterBitmap = member.photo!!,
                                size = 45.dp
                            )
                        } else {
                            AvatarView(
                                name = member.name,
                                surname = member.surname,
                                45
                            )
                        }

                        // Online/Offline status
                        Box(
                            modifier = Modifier
                                .padding(start = 5.dp, bottom = 0.dp)
                                .size(11.dp)
                                .clip(CircleShape)
                                .align(Alignment.BottomStart)
                                .background(color = if (member.online) greenBackground else darkGrayBackground)
                        )
                    }

                    Text(
                        text = stringResource(
                            Utils.convertTimePartecipationToString(
                                timeParticipation
                            )
                        ),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(7.dp))
                Column {
                    val widthModifier = if (isLandscape()) {
                        Modifier.width(350.dp)
                    } else {
                        if (routerActions.getCurrentRoute()
                                .contains(Utils.RoutesEnum.MODIFY_TEAM.name) || routerActions.getCurrentRoute()
                                .contains(Utils.RoutesEnum.MODIFY_TASK.name)
                        ) {
                            Modifier.width(170.dp)
                        } else {
                            Modifier.fillMaxWidth()
                        }

                    }

                    // Name and Surname
                    Text(
                        text = "${member.name} ${member.surname}",
                        fontWeight = FontWeight.SemiBold,
                        modifier = widthModifier,
                        fontSize = 19.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = stringResource(Utils.convertRoleToString(role)),
                        fontWeight = FontWeight.Light,
                        fontSize = 15.sp,
                        modifier = widthModifier,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isMemberTeamSettingsPage && memberAccessedValue.id == member.id) {
                        IconButton(
                            onClick = {
                                Utils.playSoundEffectWithSoundCheck(view,SoundEffectConstants.CLICK)
                                isMenuTimeParticipationExpanded.value = true
                            },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.icon_time_partecipation),
                                contentDescription = stringResource(R.string.time_participation),
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (isMemberTeamSettingsPage) {
                        IconButton(
                            onClick = {
                                Utils.playSoundEffectWithSoundCheck(
                                    view,
                                    SoundEffectConstants.CLICK
                                )
                                isMenuRoleExpanded.value = true
                            },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.icon_change_role),
                                contentDescription = stringResource(R.string.assign_role),
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (isDeleteActive) {
                        IconButton(
                            onClick = {
                                Utils.useSafeMediaPlayer(mediaPlayerDisappear)
                                if (isMemberTeamSettingsPage) {
                                    routerActions.navigateToDeleteTeamMember(teamId, member.id)
                                }
                                if (!isMemberTeamSettingsPage && onDelete != null) {
                                    taskVm?.createNewHistoryLine(
                                        LocalDateTime.now(),
                                        R.string.delete_delegate,
                                        memberInfoTeam
                                    )
                                    onDelete(memberInfoTeam)
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }


        DropdownMenu(
            expanded = isMenuTimeParticipationExpanded.value,
            onDismissRequest = { isMenuTimeParticipationExpanded.value = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            Utils.TimePartecipationTeamTypes.entries.map { timePartecipation ->
                DropdownMenuItem(
                    onClick = {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        if (onTimeParticipationChange != null) {
                            onTimeParticipationChange(timePartecipation)
                        }
                        isMenuTimeParticipationExpanded.value = false
                    },
                ) {
                    Text(
                        text = stringResource(Utils.convertTimePartecipationToString(timePartecipation))
                    )
                }
            }
        }

        DropdownMenu(
            expanded = isMenuRoleExpanded.value,
            onDismissRequest = { isMenuRoleExpanded.value = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            RoleEnum.entries.filter { it != RoleEnum.NO_ROLE_ASSIGNED }.map { role ->
                DropdownMenuItem(
                    onClick = {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        if (onRoleChange != null) {
                            onRoleChange(role)
                        }
                        isMenuRoleExpanded.value = false
                    },
                ) {
                    Text(text = stringResource(Utils.convertRoleToString(role)))
                }
            }
        }
    }
}

@Composable
fun FileCard(
    taskVm: TaskViewModel,
    file: File,
    deleteFile: ((File) -> Unit)?,
    deletePermission: Boolean
) {
    val view = LocalView.current
    val context = LocalContext.current

    val mediaPlayerDisappear: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.disappear)
    val mediaPlayerDialog: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.dialog)

    var delete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        // elevation
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = painterResource(
                when (file.contentType) {
                    Utils.ContentTypeEnum.PDF -> R.drawable.pdf_file
                    Utils.ContentTypeEnum.IMAGE_GIF -> R.drawable.gif_file
                    Utils.ContentTypeEnum.IMAGE_PNG -> R.drawable.png_file
                    Utils.ContentTypeEnum.IMAGE_JPEG -> R.drawable.jpeg_file
                    Utils.ContentTypeEnum.TEXT -> R.drawable.text_file
                    Utils.ContentTypeEnum.OTHER -> R.drawable.other_file
                }
            )

            val contentType =
                Utils.convertContentTypeToString(file.contentType)
                    ?: stringResource(R.string.other)

            Column {
                Row {
                    Icon(
                        painter = icon,
                        contentDescription = stringResource(R.string.file_icon),
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = file.name,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(0.8f)
                            .widthIn(max = 200.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${stringResource(R.string.file_size)}: ${file.size}",
                    fontSize = 12.sp
                )
                Text(
                    text = "${stringResource(R.string.upload_date)}: ${
                        Utils.formatLocalDateTime(
                            file.dateUpload
                        )
                    }",
                    fontSize = 12.sp
                )
                Text(
                    text = "${stringResource(R.string.content_type_file)}: $contentType",
                    fontSize = 12.sp
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 12.sp)) {
                            append(stringResource(R.string.uploaded_by_file))
                        }
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        ) {
                            append(" ${file.uploadedBy.surname} ${file.uploadedBy.name}")
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            if (deletePermission) {
                Column(horizontalAlignment = Alignment.End) {
                    IconButton(
                        onClick = {
                            delete = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.trash_icon),
                            contentDescription = stringResource(id = R.string.delete_file),
                            modifier = Modifier.size(20.dp),
                            tint = colorResource(R.color.redError)
                        )
                    }

                    if (delete) {
                        Utils.useSafeMediaPlayer(mediaPlayerDialog)
                        AlertDialog(
                            onDismissRequest = { delete = false },
                            title = { Text(stringResource(R.string.delete_file)) },
                            text = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(stringResource(R.string.delete_file_confirm))
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text(
                                        text = file.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = TextUnit(20f, TextUnitType.Sp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    Utils.useSafeMediaPlayer(mediaPlayerDisappear)

                                    if (deleteFile != null) {
                                        deleteFile(file)
                                        taskVm.createNewHistoryLine(
                                            LocalDateTime.now(),
                                            R.string.deleted_file
                                        )
                                        delete = false
                                    }
                                }) {
                                    Text(
                                        stringResource(R.string.delete_file),
                                        color = colorResource(id = R.color.red)
                                    )
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    //Sound and vibration
                                    Utils.playSoundEffectWithSoundCheck(
                                        view,
                                        SoundEffectConstants.CLICK
                                    )
                                    Utils.vibrate(context)

                                    delete = false
                                }) {
                                    Text(stringResource(R.string.cancel))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LinkCard(
    taskVm: TaskViewModel,
    link: Link,
    deleteLink: ((Link) -> Unit)?,
    deletePermission: Boolean
) {
    val view = LocalView.current
    val context = LocalContext.current
    val mediaPlayerDisappear: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.disappear)
    val mediaPlayerDialog: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.dialog)

    var delete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = painterResource(R.drawable.outline_link_24)

            Column {
                Row {
                    Icon(
                        painter = icon,
                        contentDescription = stringResource(R.string.link_icon),
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    Text(text = link.link, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${stringResource(R.string.upload_date)}: ${
                        Utils.formatLocalDateTime(
                            link.dateUpload
                        )
                    }",
                    fontSize = 12.sp
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 12.sp)) {
                            append(stringResource(R.string.uploaded_by_file))
                        }
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        ) {
                            append(" ${link.uploadedBy.surname} ${link.uploadedBy.name}")
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            if (deletePermission) {
                Column(horizontalAlignment = Alignment.End) {
                    IconButton(
                        onClick = {
                            delete = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.trash_icon),
                            contentDescription = stringResource(id = R.string.delete_link),
                            modifier = Modifier.size(20.dp),
                            tint = colorResource(R.color.redError)
                        )
                    }
                }
            }

            if (delete) {
                Utils.useSafeMediaPlayer(mediaPlayerDialog)
                AlertDialog(
                    onDismissRequest = { delete = false },
                    title = { Text(stringResource(R.string.delete_link)) },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(R.string.delete_link_confirm))
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = link.link,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = TextUnit(20f, TextUnitType.Sp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            Utils.useSafeMediaPlayer(mediaPlayerDisappear)

                            if (deleteLink != null) {
                                deleteLink(link)
                                taskVm.createNewHistoryLine(
                                    LocalDateTime.now(),
                                    R.string.deleted_link
                                )
                                delete = false
                            }
                        }) {
                            Text(
                                stringResource(R.string.delete_link),
                                color = colorResource(id = R.color.red)
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            //Sound and vibration
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            Utils.vibrate(context)

                            delete = false
                        }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DeleteTaskAlertDialog(
    team: TeamViewModel,
    task: TaskViewModel,
    onDeleteConfirmed: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val view = LocalView.current
    val context = LocalContext.current
    var deleteRecurringTask by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.delete_task)) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.delete_task_confirm))
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = task.titleField.value,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

        },
        confirmButton = {
            TextButton(onClick = {
                //Sound and vibration
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                Utils.vibrate(context)

                deleteRecurringTask =
                    if (task.repeatField.editValue != Utils.RepeatEnum.NO_REPEAT)
                        true
                    else {
                        onDeleteConfirmed()
                        false
                    }
            }) {
                Text(
                    stringResource(R.string.delete_task),
                    color = colorResource(id = R.color.red)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                //Sound and vibration
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                Utils.vibrate(context)

                onDismissRequest()
            }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    if (deleteRecurringTask)
        DeletedRecurringTask(
            team = team,
            task = task,
            onDeleteConfirmed = {
                deleteRecurringTask = false
            },
            onDismissRequest = {
                deleteRecurringTask = false
            }
        )
}

@Composable
fun DeletedRecurringTask(
    team: TeamViewModel,
    task: TaskViewModel,
    onDeleteConfirmed: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val view = LocalView.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.recurring_task)) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.delete_recurring_tasks))
            }

        },
        confirmButton = {
            TextButton(onClick = {
                //Sound and vibration
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                Utils.vibrate(context)

                //Delete all recurring tasks with same group id
                team.deleteRecurringTask(task)

            }) {
                Text(
                    stringResource(R.string.all_recurring_tasks),
                    color = colorResource(id = R.color.red)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                //Delete only this task
                team.deleteTask(task)
                onDeleteConfirmed()
            }) {
                Text(stringResource(R.string.only_this_task))
            }
        }
    )
}

@Composable
fun DeleteTeamAlertDialog(
    team: TeamViewModel,
    onDeleteConfirmed: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val view = LocalView.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.delete_team)) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.delete_team_confirm))
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = team.teamField.nameField.value,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

        },
        confirmButton = {
            TextButton(onClick = {
                //Sound and vibration
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                Utils.vibrate(context)

                onDeleteConfirmed()
            }) {
                Text(
                    stringResource(R.string.delete_team),
                    color = colorResource(id = R.color.red)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                //Sound and vibration
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                Utils.vibrate(context)

                onDismissRequest()
            }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun CommentCard(taskVm: TaskViewModel, comment: Comment, createdByMe: Boolean) {
    val view = LocalView.current
    val context = LocalContext.current
    val mediaPlayerSave: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.save)

    var showAll by remember { mutableStateOf(false) }
    var delete by remember { mutableStateOf(false) }

    val maxLengthMessage = 70 //Comment showed only until 50 characters

    var modifyComment by remember { mutableStateOf(false) }
    fun setModifyComment() {
        modifyComment = !modifyComment
    }

    val mediaPlayerDialog: MediaPlayer? =
        Utils.loadMediaPlayer(LocalContext.current, R.raw.dialog)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp, 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (comment.createdBy.photo != null) {
                    ImageView(painterBitmap = comment.createdBy.photo, size = 45.dp)
                } else {
                    AvatarView(
                        name = comment.createdBy.name,
                        surname = comment.createdBy.surname,
                        45
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    //Member
                    Text(
                        text = comment.createdBy.name + " " + comment.createdBy.surname,
                        fontWeight = FontWeight.Medium,
                        fontSize = 19.sp,
                    )
                    //Date and time
                    val date =
                        comment.dateCreation.toLocalDate()
                            .format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                    val time =
                        comment.dateCreation.toLocalTime()
                            .format(DateTimeFormatter.ofPattern("HH:mm"))
                    Row {
                        Text(
                            text = "$date, $time",
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }

                if (createdByMe) {
                    IconButton(
                        onClick = {
                            modifyComment = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit_icon),
                            contentDescription = stringResource(id = R.string.edit_icon),
                            modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    IconButton(
                        onClick = {
                            //Sound and vibration
                            Utils.useSafeMediaPlayer(mediaPlayerDialog)
                            Utils.vibrate(context)

                            delete = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.trash_icon),
                            contentDescription = stringResource(id = R.string.delete_comment),
                            modifier = Modifier.size(20.dp),
                            tint = colorResource(R.color.redError)
                        )
                    }
                }
            }
        }
        if (!modifyComment) {
            val messageToShow = comment.comment

            Text(
                text = messageToShow,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 30.sp,
                    color = Color.Black
                ),
                modifier = Modifier
                    .padding(10.dp, 5.dp)
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                    .clickable {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        showAll = !showAll //Added animation
                    },
                maxLines = if (!showAll) 2 else 30, //lines to show
            )

            //Comment showed only until 50 characters
            if (!showAll && comment.comment.length > maxLengthMessage) {
                Text(
                    text = stringResource(id = R.string.see_more),
                    color = colorResource(R.color.darkGray),
                    modifier = Modifier
                        .padding(5.dp, 5.dp)
                        .clickable {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            showAll = true
                        }
                )
            }
        }
        if (delete) {
            val textMessageToDelete = if (comment.comment.length > maxLengthMessage)
                comment.comment.substring(0, 42) + "..."
            else
                comment.comment
            AlertDialogComponent(
                textToDelete = textMessageToDelete,
                textMessage = stringResource(R.string.are_you_sure_delete_message),
                dismissButtonText = stringResource(R.string.cancel),
                confirmButtonText = stringResource(R.string.delete_message),
                onConfirmedRequest = {
                    Utils.useSafeMediaPlayer(mediaPlayerSave)
                    Utils.vibrate(context)

                    taskVm.deleteComment(comment)

                    delete = false
                }) {
                delete = false
            }
        }
        if (modifyComment) {
            taskVm.commentListField.editComment.value = comment.comment
            CommentModifyCard(taskVm, comment) { setModifyComment() }
            showAll = false
        }
    }
}

@Composable
fun AlertDialogComponent(
    textToDelete: String,
    textMessage: String,
    dismissButtonText: String,
    confirmButtonText: String,
    onConfirmedRequest: () -> Unit,
    onDismissRequest: () -> Unit

) {
    val view = LocalView.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(textMessage) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = textToDelete,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                onConfirmedRequest()
            }) {
                Text(confirmButtonText, color = colorResource(id = R.color.red))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                onDismissRequest()
            }) {
                Text(dismissButtonText)
            }
        }
    )
}

@Composable
fun TeamRow(
    teamVm: TeamViewModel,
    isTeamsListHomePage: Boolean,
    routerActions: RouterActions,
    onTeamClicked: () -> Unit,
    teamListVm: TeamListViewModel
) {
    /*val teamPictureProfile = Icon(
        painter = painterResource(id = R.drawable.company),
        contentDescription = stringResource(id = R.string.team_picture_profile),
        modifier = Modifier.size(35.dp),
        tint = Color.Unspecified
    )*/

    val taskIcon = painterResource(id = R.drawable.task)

    val yellowBackground = colorResource(R.color.yellow)
    val greenBackground = colorResource(R.color.green)
    val grayBackground = colorResource(R.color.gray)
    val redBackground = colorResource(R.color.red)

    var backgroundGrade = greenBackground
    if (teamVm.grade in 5..7) {
        backgroundGrade = yellowBackground
    } else if (teamVm.grade < 5) {
        backgroundGrade = redBackground
    }

    val imageSize = 55.dp
    val paddingImage = 2.dp
    val borderImage = BorderStroke(0.5.dp, Color.Black)

    Row(
        modifier = Modifier
            .clickable {
                onTeamClicked()
            }
            .border(
                width = if (isTeamsListHomePage) 1.dp else 0.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(15.dp),
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 5.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp, top = 8.dp, bottom = 5.dp)
                    .size(50.dp)
                    .background(grayBackground, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (teamVm.teamField.picture.bitmapView.value == null) {
                    Image(
                        painter = painterResource(R.drawable.company),
                        contentDescription = stringResource(id = R.string.team_picture_profile),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(imageSize)
                            .border(
                                borderImage,
                                CircleShape
                            )
                            .padding(paddingImage)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        bitmap = teamVm.teamField.picture.bitmapView.value!!,
                        contentDescription = stringResource(id = R.string.team_picture_profile),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(imageSize)
                            .border(
                                borderImage,
                                CircleShape
                            )
                            .padding(paddingImage)
                            .clip(CircleShape)
                    )
                }
            }

            Text(
                text = stringResource(teamVm.teamField.categoryField.category),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
        ) {
            Text(
                text = teamVm.teamField.nameField.value,
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = taskIcon,
                    contentDescription = stringResource(id = R.string.task_icon),
                    modifier = Modifier
                        .size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = stringResource(
                        id = R.string.task_status,
                        teamVm.numberCompletedTask,
                        teamVm.numberAssignedTask
                    ),
                    fontSize = 10.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Light
                )
            }
        }

        if (isTeamsListHomePage && teamVm.canEditBy()) {
            TeamButtons(
                routerActions = routerActions,
                teamVm = teamVm,
                teamListVm = teamListVm
            )
        }

        if (!isTeamsListHomePage) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    ) {
                        append(teamVm.grade.toString())
                    }
                    withStyle(style = SpanStyle(fontSize = 10.sp)) {
                        append("/10")
                    }
                },
                color = Color.White,
                modifier = Modifier
                    .background(backgroundGrade, shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 6.dp, vertical = 14.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPaneWithCamera(
    applicationContext: Context,
    profileVm: ProfileFormViewModel? = null,
    isTeamPage: Boolean = false,
    teamVm: TeamViewModel? = null,
    routerActions: RouterActions,
    teamListVm: TeamListViewModel,
    user: Member?
) {
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    val imageVm = profileVm?.imageField ?: teamVm?.teamField?.picture

    BottomSheetScaffold(
        modifier = Modifier.fillMaxWidth(),
        sheetContainerColor = colorResource(R.color.secondary_color),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            if (imageVm != null) {
                ChooseMenuComponent(applicationContext, scaffoldState, imageVm)
            }
        }
    ) { padding ->
        if (imageVm != null && imageVm.isOpenedCamera) {
            CameraComponent(
                applicationContext = applicationContext,
                pageWidth = padding,
                vm = imageVm
            )
        } else {
            if (!isTeamPage) {
                EditProfile(profileVm!!, scaffoldState, routerActions, user!!, teamListVm)
            } else {
                if (teamVm != null) {
                    ModifyTeamPage(
                        teamVm = teamVm,
                        scaffoldState = scaffoldState,
                        routerActions = routerActions,
                        teamListVm = teamListVm
                    )
                }
            }
        }
    }
}

@Composable
fun CameraComponent(
    applicationContext: Context,
    pageWidth: PaddingValues,
    vm: ProfileImageViewModel
) {
    val controller = remember {
        LifecycleCameraController(applicationContext).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }

    // Request camera permissions
    if (!Utils.hasRequiredPermissions(applicationContext)) {
        ActivityCompat.requestPermissions(
            LocalContext.current as Activity,
            CAMERAX_PERMISSIONS,
            0
        )
    }

    val transparenceBanner = 0.5f
    val transparenceIcons = 0.5f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(pageWidth)
    ) {
        CameraViewComponent(
            controller = controller,
            modifier = Modifier.fillMaxSize()
        )

        if (!isLandscape()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .height(100.dp)
                    .background(Color.Black.copy(alpha = transparenceBanner)) // Semi-transparent black background
            )
        }

        // Transparent black banner at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(100.dp)
                .background(Color.Black.copy(alpha = transparenceBanner)) // Semi-transparent black background
        ) {
            // Back button at the top left
            IconButton(
                onClick = {
                    vm.exitCameraMode()
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(90.dp)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_back),
                    contentDescription = stringResource(id = R.string.come_back),
                    tint = Color.White,
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            Color.Black.copy(alpha = transparenceIcons),
                            CircleShape
                        ) // Semi-transparent background
                        .padding(8.dp)
                )
            }

            // Shutter button centered at the bottom
            IconButton(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(90.dp)
                    .padding(16.dp),
                onClick = {
                    takePhotoCustom(
                        controller = controller,
                        onPhotoTaken = vm::onTakePhoto,
                        applicationContext
                    )
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_lens_24),
                    contentDescription = stringResource(id = R.string.take_photo),
                    tint = Color.White,
                    modifier = Modifier
                        .size(64.dp)
                        .border(1.dp, Color.White, CircleShape)
                        .padding(4.dp)
                )
            }

            // Check if the device has a front camera, and if not, hide the switch camera button
            if (applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
                IconButton(
                    onClick = {
                        controller.cameraSelector =
                            if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(90.dp)
                        .padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_switch_camera),
                        contentDescription = stringResource(id = R.string.switch_camera),
                        tint = Color.White,
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                Color.Black.copy(alpha = transparenceIcons),
                                CircleShape
                            ) // Semi-transparent background
                            .padding(8.dp)
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPhotoSection(
    vm: ProfileImageViewModel,
    scaffoldState: BottomSheetScaffoldState,
    name: String,
    surname: String?,
    canEditBy: Boolean
) {
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(156.dp),
        //horizontalAlignment = Alignment.CenterHorizontally
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {

        Box(
            modifier = Modifier,
            contentAlignment = Alignment.CenterEnd

        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFFD9D9D9), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (vm.bitmapEdit.value == null) {
                    AvatarEdit(
                        name,
                        surname
                    ) // Visualization of the avatar with name and surname initials
                } else {
                    ImageProfile(vm) // Visualization of the image
                }
            }

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .height(120.dp)
                    .offset(x = 35.dp)
            ) {
                if (canEditBy) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    )
                    {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.edit_photo),
                            tint = colorResource(R.color.black),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                if (vm.bitmapEdit.value != null
                ) {
                    IconButton(
                        onClick = {
                            vm.bitmapEdit.value = rotateBitmap(
                                vm.bitmapEdit.value!!,
                                90f
                            )
                        }
                    )
                    {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = stringResource(id = R.string.rotate_photo),
                            tint = colorResource(R.color.black),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }

    }
}