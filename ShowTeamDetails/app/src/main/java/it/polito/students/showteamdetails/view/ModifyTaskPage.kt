package it.polito.students.showteamdetails.view

import android.content.Intent
import android.media.MediaPlayer
import android.view.SoundEffectConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import it.polito.students.showteamdetails.ButtonsSection
import it.polito.students.showteamdetails.FileCard
import it.polito.students.showteamdetails.FileUploadUriSingleton
import it.polito.students.showteamdetails.HorizontalLine
import it.polito.students.showteamdetails.LinkCard
import it.polito.students.showteamdetails.MainActivity
import it.polito.students.showteamdetails.MemberDetailRow
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.SemiBoldText
import it.polito.students.showteamdetails.ShowToast
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.File
import it.polito.students.showteamdetails.entity.MemberInfoTeam
import it.polito.students.showteamdetails.isLandscape
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.TaskViewModel
import it.polito.students.showteamdetails.viewmodel.TeamViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun ModifyTaskPage(
    taskVm: TaskViewModel,
    teamVm: TeamViewModel,
    applicationContext: MainActivity,
    routerActions: RouterActions
) {
    BackHandler {
        routerActions.navigateToViewTask(teamVm.teamField.id, taskVm.id)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        val paddingValue: Dp = if (this.maxWidth < this.maxHeight) {
            //Portrait mode
            25.dp
        } else {
            //Landscape mode
            30.dp
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(start = paddingValue, end = paddingValue),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    val pageTitle =
                        if (routerActions.getCurrentRoute()
                                .contains(Utils.RoutesEnum.CREATE_TASK.name)
                        ) stringResource(R.string.create_task) else stringResource(
                            R.string.modify_task
                        )

                    Spacer(modifier = Modifier.height(30.dp))
                    SemiBoldText(pageTitle)
                    Spacer(modifier = Modifier.height(30.dp))
                    TitleEditSection(taskVm)
                    Spacer(modifier = Modifier.height(10.dp))

                    //Star date and start time
                    DateTimeField(
                        taskVm = taskVm,
                        labelDate = stringResource(R.string.start_date),
                        labelTime = stringResource(id = R.string.start_time),
                        editDate = taskVm.startDateField.editDate,
                        editTime = taskVm.startDateField.editTime,
                        onDateChange = { selectedDate, _ ->
                            taskVm.startDateField.editDate = selectedDate
                            taskVm.startDateField.validateDate(taskVm.dueDateField.editDate)
                        },
                        onTimeChange = { selectedTime ->
                            taskVm.startDateField.editTime = selectedTime
                            taskVm.startDateField.validateTime(
                                taskVm.dueDateField.editDate,
                                taskVm.dueDateField.editTime
                            )
                        },
                        onErrorDate = taskVm.startDateField.errorDate,
                        onErrorTime = taskVm.startDateField.errorTime
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    //Due date and due time
                    DateTimeField(
                        taskVm = taskVm,
                        labelDate = stringResource(R.string.due_date),
                        labelTime = stringResource(id = R.string.due_time),
                        editDate = taskVm.dueDateField.editDate,
                        editTime = taskVm.dueDateField.editTime,
                        onDateChange = { selectedDate, _ ->
                            taskVm.dueDateField.editDate = selectedDate
                            taskVm.dueDateField.validateDate(taskVm.startDateField.editDate)
                        },
                        onTimeChange = { selectedTime ->
                            taskVm.dueDateField.editTime = selectedTime
                            taskVm.dueDateField.validateTime(
                                taskVm.startDateField.editDate,
                                taskVm.startDateField.editTime
                            )
                        },
                        onErrorDate = taskVm.dueDateField.errorDate,
                        onErrorTime = taskVm.dueDateField.errorTime
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Repeat(taskVm)
                    Spacer(modifier = Modifier.height(10.dp))
                    DescriptionEditSection(taskVm)
                    Spacer(modifier = Modifier.height(10.dp))
                    CategoryEditSection(taskVm)
                    Spacer(modifier = Modifier.height(10.dp))
                    AssignTags(taskVm)
                    HorizontalLine()
                    Spacer(modifier = Modifier.height(30.dp))
                    DelegateTasksEditSection(
                        members = teamVm.teamField.membersField.editMembers,
                        taskVm = taskVm,
                        routerActions = routerActions,
                        teamId = teamVm.teamField.id
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalLine()
                    Spacer(modifier = Modifier.height(10.dp))
                    AttachmentsEditSection(taskVm, applicationContext)
                    Spacer(modifier = Modifier.height(40.dp))
                    ButtonsSection(taskVm, teamVm, routerActions)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun TitleEditSection(vm: TaskViewModel) {
    val view = LocalView.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 5.dp),
                value = vm.titleField.editValue,
                onValueChange = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    vm.titleField.editValue = it
                },
                label = {
                    Text(
                        stringResource(
                            id = R.string.title
                        ),
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                isError = vm.titleField.error != -1,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 30.sp
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = true,
                    capitalization = KeyboardCapitalization.Words
                )
            )

            if (vm.titleField.error != -1)
                Text(
                    text = stringResource(vm.titleField.error),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeField(
    taskVm: TaskViewModel,
    labelDate: String,
    labelTime: String,
    editDate: LocalDate,
    editTime: LocalTime,
    onDateChange: (LocalDate, Boolean) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onErrorDate: Int,
    onErrorTime: Int
) {
    val isOpen = remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val state = rememberTimePickerState()
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val view = LocalView.current
    val dateFormat = stringResource(R.string.date_format)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Date
            OutlinedTextField(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(0.dp, 5.dp),
                value = editDate.format(DateTimeFormatter.ofPattern(dateFormat)),
                onValueChange = { newText ->
                    if (isValidDateFormat(newText)) {
                        try {
                            LocalDate.parse(newText, DateTimeFormatter.ofPattern(dateFormat))
                        } catch (_: DateTimeParseException) {
                        }
                    }
                },
                label = {
                    Text(
                        labelDate,
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 30.sp
                ),
                isError = onErrorDate != -1,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            isOpen.value = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.width(10.dp))

            // Time
            OutlinedTextField(
                modifier = Modifier
                    .weight(0.3f)
                    .padding(0.dp, 5.dp),
                value = editTime.format(formatter),
                onValueChange = {},
                label = {
                    Text(
                        text = labelTime,
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 30.sp
                ),
                isError = onErrorTime != -1,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            showTimePicker = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null
                        )
                    }
                }
            )
        }

        // Error messages
        if (onErrorDate != -1) {
            Text(
                text = stringResource(onErrorDate),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(start = 5.dp, top = 3.dp),
                color = colorResource(R.color.redError)
            )
        }

        if (onErrorTime != -1) {
            Text(
                text = stringResource(onErrorTime),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp
                ),
                modifier = Modifier
                    .padding(top = 3.dp, end = 5.dp)
                    .align(Alignment.End),
                color = colorResource(R.color.redError)
            )
        }

        // Date picker dialog
        if (isOpen.value) {
            DatePickerDialog(
                onAccept = { selectedDateMillis ->
                    if (selectedDateMillis != null) {
                        val selectedDate = Instant
                            .ofEpochMilli(selectedDateMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        val isPastDate = selectedDate.isBefore(LocalDate.now())

                        onDateChange(selectedDate, isPastDate)
                    }
                    taskVm.startDateField.resetErrorStartDate()
                    taskVm.dueDateField.resetErrorDueDate()

                    isOpen.value = false
                },
                onCancel = {
                    isOpen.value = false
                }
            )
        }

        // Time picker dialog
        if (showTimePicker) {
            TimePickerDialog(
                onCancel = {
                    showTimePicker = false
                },
                onConfirm = {
                    val selectedTime = LocalTime.of(state.hour, state.minute)
                    onTimeChange(selectedTime)
                    showTimePicker = false
                },
            ) {
                val modifier = if (isLandscape()) {
                    Modifier.size(width = 550.dp, height = 262.dp)
                } else {
                    Modifier.fillMaxWidth()
                }

                TimePicker(
                    state = state,
                    modifier = modifier,
                    colors = TimePickerDefaults.colors(
                        selectorColor = colorResource(R.color.primary_color),
                        periodSelectorSelectedContainerColor = colorResource(R.color.secondary_color),
                        timeSelectorSelectedContainerColor = colorResource(R.color.secondary_color)
                    )
                )
            }
        }
    }
}

// Function to validate the date format
fun isValidDateFormat(dateString: String): Boolean {
    return try {
        LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        true
    } catch (e: DateTimeParseException) {
        false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onAccept: (Long?) -> Unit,
    onCancel: () -> Unit
) {
    val color = colorResource(R.color.primary_color)
    val state = rememberDatePickerState()
    val view = LocalView.current

    DatePickerDialog(
        onDismissRequest = {
            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
        },
        confirmButton = {
            TextButton(onClick = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                onAccept(state.selectedDateMillis)
            }) {
                Text(
                    stringResource(id = R.string.ok),
                    fontSize = 18.sp,
                    color = color
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                onCancel()
            }) {
                Text(
                    stringResource(id = R.string.cancel),
                    fontSize = 18.sp,
                    color = color
                )
            }
        }
    ) {
        if (isLandscape()) {
            DatePicker(
                state = state,
                modifier = Modifier.size(width = 400.dp, height = 270.dp),
                title = null,
                headline = null,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    titleContentColor = color,
                    subheadContentColor = color,
                    selectedDayContainerColor = color,
                    selectedYearContainerColor = color,
                    todayDateBorderColor = color,
                    weekdayContentColor = color,
                    selectedYearContentColor = colorResource(R.color.white),
                    selectedDayContentColor = colorResource(R.color.white),
                )
            )
        } else {
            DatePicker(
                state = state,
                colors = DatePickerDefaults.colors(
                    titleContentColor = color,
                    subheadContentColor = color,
                    selectedDayContainerColor = color,
                    selectedYearContainerColor = color,
                    todayDateBorderColor = color,
                    weekdayContentColor = color,
                    selectedYearContentColor = colorResource(R.color.white),
                    selectedDayContentColor = colorResource(R.color.white),
                )
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = stringResource(id = R.string.select_time),
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val color = colorResource(R.color.primary_color)
    val view = LocalView.current

    Dialog(
        onDismissRequest = {
            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
            onCancel()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isLandscape()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        text = title,
                        fontSize = 18.sp,
                        color = color,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            onCancel()
                        }
                    ) {
                        Text(
                            stringResource(id = R.string.cancel),
                            fontSize = 18.sp,
                            color = color
                        )
                    }
                    TextButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            onConfirm()
                        }
                    ) {
                        Text(
                            stringResource(id = R.string.ok),
                            fontSize = 18.sp,
                            color = color
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Repeat(vm: TaskViewModel) {
    val repeatIcon = painterResource(id = R.drawable.repeat)
    val isOpen = remember { mutableStateOf(false) }
    var showRepeatDate by remember { mutableStateOf(false) }
    val view = LocalView.current

    var showToast by remember {
        mutableStateOf(false)
    }

    if (showToast) {
        ShowToast(message = stringResource(id = R.string.resolve_error))
        showToast = false
    }

    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = isOpen.value,
        onExpandedChange = {
            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
            isOpen.value = it
        }
    ) {
        Column {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = stringResource(Utils.convertRepeatToString(vm.repeatField.editValue)),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = {
                    Text(
                        stringResource(id = R.string.repeat),
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            isOpen.value = true
                        }
                    ) {
                        Icon(
                            painter = repeatIcon,
                            contentDescription = null,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                },
            )
            ExposedDropdownMenu(
                expanded = isOpen.value,
                onDismissRequest = { isOpen.value = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Utils.RepeatEnum.entries.map { repeat ->
                    DropdownMenuItem(
                        text = { Text(stringResource(Utils.convertRepeatToString(repeat))) },
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            when (repeat) {
                                Utils.RepeatEnum.NO_REPEAT -> {
                                    vm.repeatField.editValue = repeat
                                    isOpen.value = false
                                }

                                Utils.RepeatEnum.DAILY -> {
                                    vm.repeatField.editValue = repeat
                                    isOpen.value = false
                                    showRepeatDate = true
                                }

                                Utils.RepeatEnum.WEEKLY -> {
                                    vm.repeatField.editValue = repeat
                                    isOpen.value = false
                                    showRepeatDate = true
                                }

                                Utils.RepeatEnum.MONTHLY -> {
                                    vm.repeatField.editValue = repeat
                                    isOpen.value = false
                                    showRepeatDate = true
                                }

                                Utils.RepeatEnum.YEARLY -> {
                                    vm.repeatField.editValue = repeat
                                    isOpen.value = false
                                    showRepeatDate = true
                                }
                            }
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }

            // Error messages
            if (vm.repeatField.errorDate != -1) {
                Text(
                    text = stringResource(vm.repeatField.errorDate),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(start = 5.dp, top = 3.dp),
                    color = colorResource(R.color.redError)
                )
            }
        }
    }

    if (showRepeatDate) {
        UntilRepeatDialog(
            vm = vm,
            onCloseDialog = {
                showRepeatDate = false
            }
        )
    }

}

@Composable
fun UntilRepeatDialog(
    vm: TaskViewModel,
    onCloseDialog: () -> Unit
) {
    val color = colorResource(R.color.primary_color)
    val isOpen = remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(vm.repeatField.repeatEndDate) }
    val view = LocalView.current

    AlertDialog(
        onDismissRequest = {
            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
            onCloseDialog()
        },
        title = {
            Text(
                stringResource(id = R.string.until_repeat),
                fontSize = 18.sp,
                color = color
            )
        },
        text = {
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(end = 4.dp),
                        value = selectedDate.format(DateTimeFormatter.ofPattern(stringResource(id = R.string.date_format))),
                        onValueChange = { },
                        label = {
                            Text(
                                stringResource(R.string.until_repeat),
                                color = colorResource(R.color.darkGray),
                                fontSize = 16.sp
                            )
                        },
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp,
                            lineHeight = 30.sp
                        ),
                        isError = vm.repeatField.errorDate != -1,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    Utils.playSoundEffectWithSoundCheck(
                                        view,
                                        SoundEffectConstants.CLICK
                                    )
                                    isOpen.value = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                    // Error messages
                    if (vm.repeatField.errorDate != -1) {
                        Text(
                            text = stringResource(vm.repeatField.errorDate),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(start = 5.dp, top = 3.dp),
                            color = colorResource(R.color.redError)
                        )
                    }
                }

                // Date picker dialog
                if (isOpen.value) {
                    DatePickerDialog(
                        onAccept = { selectedDateMillis ->
                            isOpen.value = false

                            //Convert millis timestamp in a Instant type
                            if (selectedDateMillis != null) {
                                selectedDate = Instant.ofEpochMilli(selectedDateMillis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                vm.repeatField.editRepeatEndDate = selectedDate
                            }
                        },
                        onCancel = {
                            isOpen.value = false
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                vm.repeatField.setEndDate(selectedDate)
                onCloseDialog() // Close dialog
            }) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                onCloseDialog()
            }) {
                Text(stringResource(id = R.string.close))
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AssignTags(vm: TaskViewModel) {
    var newTag by remember { mutableStateOf("") }
    val view = LocalView.current
    val mediaPlayerDisappear: MediaPlayer? =
        Utils.loadMediaPlayer(LocalContext.current, R.raw.disappear)

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 5.dp),
                value = newTag,
                onValueChange = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    newTag = it
                },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(id = R.string.assign_tags),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                },
                placeholder = { Text(stringResource(id = R.string.assign_here_tags)) },
                isError = vm.assignTagsField.error != -1,
                trailingIcon = {
                    if (newTag.isNotBlank()) {
                        IconButton(onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            if (vm.assignTagsField.addTag(newTag)) { // Add the new tag only if it does not already exist
                                newTag = ""
                                vm.createNewHistoryLine(LocalDateTime.now(), R.string.modify_tag)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.AddCircle,
                                contentDescription = null,
                            )
                        }
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                    )
                }
            )
            if (vm.assignTagsField.error != -1) {
                val duplicateTags = vm.assignTagsField.value.groupingBy { it }.eachCount()
                    .filter { it.value > 1 }.keys
                Text(
                    text = stringResource(vm.assignTagsField.error, duplicateTags),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(5.dp, 3.dp),
                    color = colorResource(R.color.redError)
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                vm.assignTagsField.editValue.forEach { tag ->
                    val tagColor = vm.assignTagsField.getTagColor(tag)
                    val chipColor =
                        colorResource(tagColor) // Converts the color ID to a Color object
                    AssistChip(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .align(alignment = Alignment.CenterVertically),
                        onClick = { },
                        label = {
                            Text(
                                text = tag,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = chipColor),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    Utils.useSafeMediaPlayer(mediaPlayerDisappear)
                                    vm.assignTagsField.editValue =
                                        vm.assignTagsField.editValue.apply {
                                            remove(tag)
                                        }
                                    vm.createNewHistoryLine(
                                        LocalDateTime.now(),
                                        R.string.modify_tag
                                    )
                                },
                                modifier = Modifier.height(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.Black,
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DescriptionEditSection(vm: TaskViewModel) {
    val view = LocalView.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 0.dp, end = 0.dp, top = 5.dp),
                value = vm.descriptionField.editValue,
                onValueChange = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    vm.descriptionField.editValue = it
                },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(id = R.string.description_task),
                            color = colorResource(R.color.darkGray),
                            fontSize = 16.sp
                        )
                    }
                },
                isError = vm.descriptionField.error != -1,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 30.sp
                )

            )
            if (vm.descriptionField.error != -1) {
                Text(
                    text = stringResource(vm.descriptionField.error),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditSection(taskVm: TaskViewModel) {
    val isMenuExpanded = remember { mutableStateOf(false) }
    val view = LocalView.current

    Column {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = isMenuExpanded.value,
            onExpandedChange = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                isMenuExpanded.value = it
            }) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = stringResource(Utils.convertCategoryToString(taskVm.categoryField.editCategory)),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = {
                    Text(
                        stringResource(
                            id = R.string.category
                        ),
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMenuExpanded.value) },
            )
            ExposedDropdownMenu(
                expanded = isMenuExpanded.value,
                onDismissRequest = { isMenuExpanded.value = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Utils.CategoryEnum.entries.map { category ->
                    DropdownMenuItem(
                        text = { Text(stringResource(Utils.convertCategoryToString(category))) },
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            taskVm.categoryField.editCategory = category
                            taskVm.categoryField.error = -1
                            isMenuExpanded.value = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
        if (taskVm.categoryField.error != -1) {
            Text(
                text = stringResource(taskVm.categoryField.error),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelegateTasksEditSection(
    members: List<MemberInfoTeam>,
    taskVm: TaskViewModel,
    routerActions: RouterActions,
    teamId: String
) {
    val isMenuExpanded = remember { mutableStateOf(false) }
    val view = LocalView.current

    Column {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = isMenuExpanded.value,
            onExpandedChange = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                isMenuExpanded.value = it
            }) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = stringResource(R.string.select_member),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = {
                    Text(
                        stringResource(
                            id = R.string.delegate_tasks
                        ),
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMenuExpanded.value) },
            )
            ExposedDropdownMenu(
                expanded = isMenuExpanded.value,
                onDismissRequest = { isMenuExpanded.value = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                members.filter { member ->
                    !taskVm.delegateTasksField.editMembers.any { it.profile.id == member.profile.id }
                }.map { member ->
                    DropdownMenuItem(
                        text = { Text("${member.profile.surname} ${member.profile.name}") },
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            taskVm.delegateTasksField.addMember(
                                member,
                                isRequestsPage = false
                            )
                            taskVm.createNewHistoryLine(
                                LocalDateTime.now(),
                                R.string.modify_delegate
                            )
                            isMenuExpanded.value = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        if (taskVm.delegateTasksField.error != -1) {
            Text(
                text = stringResource(taskVm.delegateTasksField.error),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(5.dp, 3.dp),
                color = colorResource(R.color.redError)
            )
        }

        taskVm.delegateTasksField.editMembers.forEach { member ->
            Spacer(modifier = Modifier.height(6.dp))
            AnimatedContent(
                targetState = member,
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
            ) { targetMember ->
                MemberDetailRow(
                    taskVm = taskVm,
                    memberInfoTeam = targetMember,
                    isDeleteActive = true,
                    onDelete = taskVm.delegateTasksField::deleteMember,
                    routerActions = routerActions,
                    teamId = teamId
                )
            }
        }
    }
}

@Composable
fun AttachmentsEditSection(taskVm: TaskViewModel, applicationContext: MainActivity) {
    var isAddLink by remember { mutableStateOf(false) }

    val view = LocalView.current

    DisposableEffect(taskVm) {
        val fileLiveData = FileUploadUriSingleton.getFile()
        val observer = { file: File? ->
            if (file != null) {
                taskVm.fileUploadedField.addFile(file)
                FileUploadUriSingleton.setFile(null)
            }
        }
        fileLiveData.observeForever(observer)
        onDispose {
            fileLiveData.removeObserver(observer)
        }
    }

    //Utils.hasRequiredPermissions(applicationContext)

    /*SideEffect {
        permissionState.launchPermissionRequest()
    }*/

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
            LinkListComponent(taskVm)
        } else {
            FileListComponent(applicationContext, taskVm)
        }

    }
}

@Composable
fun FileListComponent(applicationContext: MainActivity, taskVm: TaskViewModel) {
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

        taskVm.fileUploadedField.editFileList.sortedBy { it.dateUpload }.map { file ->
            Spacer(modifier = Modifier.height(10.dp))
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
                FileCard(
                    taskVm,
                    targetFile,
                    taskVm.fileUploadedField::deleteFile,
                    taskVm.canEditBy()
                )
            }
        }

        /*vm.linkListField.editLinks.sortedBy { it.dateUpload }.map { link ->
            Spacer(modifier = Modifier.height(10.dp))
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
                LinkCard(link = targetLink, vm.linkListField::deleteLink)
            }
        }*/
    }
}

@Composable
fun LinkListComponent(taskVm: TaskViewModel) {
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
                        taskVm.linkListField.addLink(nameLink, taskId = taskVm.id)
                        taskVm.createNewHistoryLine(LocalDateTime.now(), R.string.uploaded_link)
                        nameLink = ""
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


        taskVm.linkListField.editLinks.forEach { link ->
            Spacer(modifier = Modifier.height(10.dp))
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
                LinkCard(taskVm, targetLink, taskVm.linkListField::deleteLink, taskVm.canEditBy())
            }
        }
    }
}