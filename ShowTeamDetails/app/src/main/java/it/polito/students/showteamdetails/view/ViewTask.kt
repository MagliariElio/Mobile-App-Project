package it.polito.students.showteamdetails.view

import android.media.MediaPlayer
import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.DeleteTaskAlertDialog
import it.polito.students.showteamdetails.MainActivity
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.TaskViewModel
import it.polito.students.showteamdetails.viewmodel.TeamViewModel

@Composable
fun ViewTaskPage(
    taskVm: TaskViewModel,
    teamVm: TeamViewModel,
    applicationContext: MainActivity,
    routerActions: RouterActions
) {
    //Info, comments, history, files bar
    val view = LocalView.current

    BoxWithConstraints(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        val pageWidth: Float = if (this.maxWidth < this.maxHeight) {
            //Portrait mode
            //this.maxWidth.value * .85f
            30f
        } else {
            //Landscape mode
            //this.maxWidth.value * .8f
            35f
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ViewTab(
                pageWidth,
                teamVm,
                taskVm,
                applicationContext,
                routerActions
            )
        }
    }

    //TODO("Controllare che non ci siano creazioni o modifiche in corso") - ??
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewTab(
    pageWidth: Float,
    teamVm: TeamViewModel,
    taskVm: TaskViewModel,
    applicationContext: MainActivity,
    routerActions: RouterActions
) {
    val view = LocalView.current
    val titles = listOf(
        stringResource(id = R.string.info_page),
        stringResource(id = R.string.comments_page),
        stringResource(id = R.string.history_page),
        stringResource(id = R.string.files_page)
    )

    Column {
        PrimaryTabRow(selectedTabIndex = taskVm.stateTab) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = taskVm.stateTab == index,
                    onClick = {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        taskVm.stateTab = index
                    },
                    text = {
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    icon = {
                        when (index) {
                            0 -> Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                            )

                            1 -> Icon(
                                imageVector = Icons.AutoMirrored.Filled.Comment,
                                contentDescription = null,
                            )

                            2 -> Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                            )

                            3 -> Icon(
                                imageVector = Icons.Default.FileCopy,
                                contentDescription = null,
                            )
                        }
                    }
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dp(pageWidth))
            ) {
                item {
                    Spacer(modifier = Modifier.height(25.dp))
                    ViewTitle(teamVm, taskVm, routerActions)
                    Spacer(modifier = Modifier.height(25.dp))

                    when (taskVm.stateTab) {
                        0 -> InfoTaskPage(taskVm, routerActions, teamVm.teamField.id)
                        1 -> {
                            taskVm.commentListField.resetCommentError()
                            CommentsTask(taskVm)
                        }

                        2 -> HistoryTaskPage(taskVm)
                        3 -> FilesTask(taskVm, applicationContext)
                    }
                }
            }
            if (taskVm.stateTab == 1) {
                AddNewComment(taskVm)
            }
        }
    }
}

@Composable
fun ViewTitle(team: TeamViewModel, taskVm: TaskViewModel, routerActions: RouterActions) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                text = taskVm.titleField.value,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    lineHeight = 35.sp,
                )
            )
            if (taskVm.canEditBy()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EditDeleteTask(team, taskVm, routerActions)
                }
            }
        }
    }
}

@Composable
fun EditDeleteTask(
    teamVm: TeamViewModel,
    taskVm: TaskViewModel,
    routerActions: RouterActions
) {

    val view = LocalView.current
    var openAlertDialog by remember { mutableStateOf(false) }
    var openMenu by remember { mutableStateOf(false) }
    val mediaPlayerDialog: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.dialog)

    Column {
        // Menu to edit or delete task
        Box(
            modifier = Modifier
                .width(IntrinsicSize.Min)
        ) {
            IconButton(
                onClick = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    openMenu = !openMenu
                }
            ) {
                Icon(
                    Icons.Outlined.MoreHoriz,
                    stringResource(R.string.edit_delete_menu)
                )
            }

            DropdownMenu(
                expanded = openMenu,
                onDismissRequest = { openMenu = false },
                modifier = Modifier.padding(4.dp)
            ) {
                DropdownMenuItem(
                    onClick = {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        routerActions.navigateToModifyTask(teamVm.teamField.id, taskVm.id)
                        openMenu = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.modify_task),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        Utils.useSafeMediaPlayer(mediaPlayerDialog)

                        openAlertDialog = true
                        openMenu = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete_task),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
    if (openAlertDialog)
        DeleteTaskAlertDialog(
            team = teamVm,
            task = taskVm,
            onDeleteConfirmed = {
                teamVm.deleteTask(taskVm)
                routerActions.goBack()
                openAlertDialog = false
            },
            onDismissRequest = {
                openAlertDialog = false
            }
        )
}
