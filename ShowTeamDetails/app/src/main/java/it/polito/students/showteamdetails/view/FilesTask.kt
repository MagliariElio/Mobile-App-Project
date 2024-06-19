package it.polito.students.showteamdetails.view

import android.content.Intent
import android.view.SoundEffectConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.FileCard
import it.polito.students.showteamdetails.FileUploadUriSingleton
import it.polito.students.showteamdetails.LinkCard
import it.polito.students.showteamdetails.MainActivity
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.File
import it.polito.students.showteamdetails.viewmodel.TaskViewModel
import java.time.LocalDateTime


@Composable
fun FilesTask(taskVm: TaskViewModel, applicationContext: MainActivity) {
    if (taskVm.fileUploadedField.fileList.isEmpty() && taskVm.linkListField.links.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                stringResource(id = R.string.no_links_or_files),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    //Visualize file list
    taskVm.fileUploadedField.editFileList.sortedBy { it.dateUpload }.map { file ->
        AnimatedContent(
            targetState = file,
            transitionSpec = {
                slideIntoContainer(
                    animationSpec = tween(300, easing = LinearEasing),
                    towards = AnimatedContentTransitionScope.SlideDirection.Up
                ).togetherWith(
                    slideOutOfContainer(
                        animationSpec = tween(300, easing = LinearEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.Down
                    )
                )
            }, label = ""
        ) { targetFile ->
            FileCard(taskVm, file = targetFile, deleteFile = {
                taskVm.fileUploadedField.deleteFileSynch(file = it, taskId = taskVm.id)
            }, deletePermission = taskVm.canEditBy())
        }
        Spacer(modifier = Modifier.height(5.dp))
    }

    //Visualize links list
    taskVm.linkListField.editLinks.forEach { link ->
        AnimatedContent(
            targetState = link,
            transitionSpec = {
                slideIntoContainer(
                    animationSpec = tween(300, easing = LinearEasing),
                    towards = AnimatedContentTransitionScope.SlideDirection.Up
                ).togetherWith(
                    slideOutOfContainer(
                        animationSpec = tween(300, easing = LinearEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.Down
                    )
                )
            }, label = ""
        ) { targetLink ->
            LinkCard(taskVm, link = targetLink, deleteLink = {
                taskVm.linkListField.deleteLinksSynch(link = it, taskId = taskVm.id)
            }, deletePermission = taskVm.canEditBy())
        }
        Spacer(modifier = Modifier.height(5.dp))
    }

    Spacer(modifier = Modifier.height(10.dp))

    if (taskVm.canEditBy())
        AddNewFileOrLinkFloatingButton(taskVm, applicationContext)
}

@Composable
fun AddNewFileOrLinkFloatingButton(taskVm: TaskViewModel, applicationContext: MainActivity) {
    var showButton by remember { mutableStateOf(true) }
    val view = LocalView.current

    DisposableEffect(taskVm) {
        val fileLiveData = FileUploadUriSingleton.getFile()
        val observer = { file: File? ->
            if (file != null) {
                taskVm.fileUploadedField.addFileSynch(file, taskVm.id)
                FileUploadUriSingleton.setFile(null)
            }
        }
        fileLiveData.observeForever(observer)
        onDispose {
            fileLiveData.removeObserver(observer)
        }
    }

    if (!showButton) {
        AttachmentsEditSection(taskVm, applicationContext) { showButton = true }
        Spacer(modifier = Modifier.height(30.dp))
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (showButton) {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.add)) },
                icon = { Icon(Icons.Filled.Add, stringResource(R.string.add)) },
                onClick = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    showButton = false
                },
                modifier = Modifier.padding(bottom = 20.dp)
            )
        } else {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.cancel)) },
                icon = { Icon(Icons.Filled.Close, stringResource(R.string.cancel)) },
                onClick = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    showButton = true
                },
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }

}

@Composable
fun AttachmentsEditSection(
    taskVm: TaskViewModel,
    applicationContext: MainActivity,
    setEditNewFileOrLink: () -> Unit
) {
    var isAddLink by remember { mutableStateOf(false) }

    val view = LocalView.current

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(R.drawable.attachments),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.attachments),
                fontWeight = FontWeight.SemiBold,
                fontSize = 21.sp,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.weight(6f))
            Switch(
                checked = isAddLink,
                onCheckedChange = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    isAddLink = !isAddLink
                },
                modifier = Modifier
                    .size(50.dp)
                    .scale(0.9f),
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    uncheckedTrackColor = Color.White,
                    uncheckedThumbColor = Color.Black
                )
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = stringResource(R.string.attachments_subtitle_description),
            fontSize = 12.sp,
            color = colorResource(R.color.darkGray)
        )
        Spacer(modifier = Modifier.height(13.dp))

        if (isAddLink) {
            AddLinkComponent(taskVm) { setEditNewFileOrLink() }
        } else {
            AddFileComponent(applicationContext, taskVm) { setEditNewFileOrLink() }
        }

    }
}

@Composable
fun AddFileComponent(
    applicationContext: MainActivity,
    taskVm: TaskViewModel,
    setEditNewFileOrLink: () -> Unit
) {
    val view = LocalView.current

    Column {
        IconButton(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    border = BorderStroke(0.2.dp, Color.Black),
                    shape = RoundedCornerShape(15.dp)
                ),
            onClick = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                applicationContext.pickFile.launch(intent)
                taskVm.createNewHistoryLine(LocalDateTime.now(), R.string.uploaded_file)
                setEditNewFileOrLink()
            }) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.upload),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(R.string.upload_file),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun AddLinkComponent(taskVm: TaskViewModel, setEditNewFileOrLink: () -> Unit) {
    var nameLink by remember { mutableStateOf("") }
    val view = LocalView.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, end = 0.dp, top = 5.dp),
            value = nameLink,
            onValueChange = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                nameLink = it
            },
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(id = R.string.link_label),
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                }
            },
            placeholder = {
                Text(
                    stringResource(id = R.string.add_link),
                    color = colorResource(R.color.darkGray),
                    fontSize = 16.sp
                )
            },
            isError = taskVm.linkListField.error != -1,
            maxLines = 1,
            textStyle = TextStyle(
                color = Color.Black,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 30.sp
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.outline_link_24),
                    contentDescription = null,
                )
            },
            trailingIcon = {
                if (nameLink.isNotBlank())
                    IconButton(onClick = {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        taskVm.linkListField.addLink(
                            linkAddress = nameLink,
                            taskId = taskVm.id,
                            isSaveToo = true
                        )

                        if (taskVm.linkListField.error == -1) {
                            nameLink = ""
                            taskVm.createNewHistoryLine(
                                LocalDateTime.now(),
                                R.string.uploaded_link
                            )
                            setEditNewFileOrLink()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.AddCircle,
                            contentDescription = null,
                        )
                    }
            },
        )
        if (taskVm.linkListField.error != -1) {
            Text(
                text = stringResource(taskVm.linkListField.error),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(5.dp, 3.dp),
                color = colorResource(R.color.redError)
            )
        }
    }
}