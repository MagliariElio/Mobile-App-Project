package it.polito.students.showteamdetails.view

import android.media.MediaPlayer
import android.view.SoundEffectConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import it.polito.students.showteamdetails.AvatarView
import it.polito.students.showteamdetails.DeleteTaskAlertDialog
import it.polito.students.showteamdetails.ImageView
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.TaskViewModel
import it.polito.students.showteamdetails.viewmodel.TeamListViewModel
import it.polito.students.showteamdetails.viewmodel.TeamViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


@Composable
fun TeamPage(
    teamVm: TeamViewModel,
    selectTask: (String, String) -> Unit,
    backStackEntry: NavBackStackEntry,
    routerActions: RouterActions
) {
    val teamListVm: TeamListViewModel = viewModel(backStackEntry)

    val tabTitles = listOf(
        R.string.pending_status,
        R.string.in_progress_status,
        R.string.on_hold_status,
        R.string.overdue_status,
        R.string.done_status
    )

    val view = LocalView.current
    BackHandler(onBack = {
        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
        routerActions.goBack()

    })

    BoxWithConstraints(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        val pageWidth: Float = if (this.maxWidth < this.maxHeight) {
            30f
        } else {
            100f
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(pageWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 5.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.tasks_list),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 25.sp
                    )
                }

                SearchSortFilterArea(
                    teamVm = teamVm,
                    setSearchByNameField = { teamVm.searchByNameField = it },
                    setSortField = { teamVm.sortField = it },
                    setFilterField = { teamVm.filterField = it }
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    TabsBar(
                        selectedTabIndex = teamVm.selectedTabIndex,
                        tabTitles = tabTitles.map { stringResource(id = it) },
                        onTabSelected = { newIndex -> teamVm.selectedTabIndex = newIndex })

                    PageContent(
                        teamVm = teamVm,
                        tabTitles = tabTitles,
                        selectedTabIndex = teamVm.selectedTabIndex,
                        selectTask = selectTask,
                        searchByNameField = teamVm.searchByNameField,
                        sortField = teamVm.sortField,
                        routerActions = routerActions
                    )
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        AddTaskFloatingButton(teamVm = teamVm, routerActions)
    }
}

@Composable
fun SearchSortFilterArea(
    teamVm: TeamViewModel,
    setSearchByNameField: (String) -> Unit,
    setSortField: (Comparator<TaskViewModel>) -> Unit,
    setFilterField: ((TaskViewModel) -> Boolean) -> Unit
) {
    val view = LocalView.current
    val filterWeightState = remember { Animatable(if (teamVm.filterAreaOpened) 5f else 1f) }

    val memberAccessedValue = Utils.memberAccessed.collectAsState().value

    LaunchedEffect(teamVm.filterAreaOpened) {
        filterWeightState.animateTo(
            if (teamVm.filterAreaOpened) 5f else 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    val sortWeightState = remember { Animatable(if (teamVm.sortAreaOpened) 5f else 1f) }

    LaunchedEffect(teamVm.sortAreaOpened) {
        sortWeightState.animateTo(
            if (teamVm.sortAreaOpened) 5f else 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Filter button and dropdown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(filterWeightState.value)
                .background(
                    if (teamVm.filterTextField == Utils.filteringMap.keys.first()) Color.Transparent else colorResource(
                        id = R.color.secondary_color
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (teamVm.filterTextField == Utils.filteringMap.keys.first()) Color.Gray else colorResource(
                        id = R.color.primary_color
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(end = if (teamVm.filterAreaOpened) 10.dp else 0.dp),
            horizontalArrangement = if (teamVm.filterAreaOpened) Arrangement.SpaceBetween else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    teamVm.sortAreaOpened = false
                    teamVm.filterAreaOpened = !teamVm.filterAreaOpened
                },
                modifier = Modifier
                    .height(56.dp)
                    .wrapContentSize(unbounded = true),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterAlt,
                    contentDescription = "Filter icon",
                    tint = if (teamVm.filterTextField == Utils.filteringMap.keys.first()) Color.Gray else colorResource(
                        id = R.color.primary_color
                    )
                )
            }

            if (teamVm.filterAreaOpened) {
                val filterTextField = Utils.getTaskFilterStringId(teamVm.filterTextField)
                    ?.let { filter -> stringResource(filter) } ?: teamVm.filterTextField
                Box(
                    modifier = Modifier.clickable {
                        teamVm.filteringDropdownMenuOpened = true
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(
                                1.dp,
                                colorResource(id = R.color.primary_color),
                                RoundedCornerShape(20.dp)
                            )
                            .background(
                                color = colorResource(id = R.color.secondary_color),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clip(RoundedCornerShape(20.dp)),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (teamVm.filterTextField == "${memberAccessedValue.name} ${memberAccessedValue.surname}") {
                                "$filterTextField (${stringResource(R.string.me)})"
                            } else {
                                filterTextField
                            },
                            style = MaterialTheme.typography.labelSmall
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        if (teamVm.filterTextField != Utils.filteringMap.keys.first()) {
                            IconButton(
                                onClick = {
                                    Utils.playSoundEffectWithSoundCheck(
                                        view,
                                        SoundEffectConstants.CLICK
                                    )
                                    teamVm.filterTextField = Utils.filteringMap.keys.first()
                                    setFilterField { true }
                                },
                                modifier = Modifier.width(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = stringResource(id = R.string.delete_field)
                                )
                            }
                        }
                        IconButton(onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            teamVm.filteringDropdownMenuOpened = true
                        }) {
                            Icon(
                                imageVector = if (teamVm.filteringDropdownMenuOpened) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                contentDescription = if (teamVm.filteringDropdownMenuOpened) stringResource(
                                    id = R.string.collapse_filter_field_dropdown_menu
                                ) else stringResource(id = R.string.expand_filter_field_dropdown_menu)
                            )
                        }
                    }

                    val tags =
                        teamVm.tasksList.flatMap { it.assignTagsField.value.toList() }.distinct()

                    DropdownMenu(
                        expanded = teamVm.filteringDropdownMenuOpened,
                        onDismissRequest = { teamVm.filteringDropdownMenuOpened = false }
                    ) {

                        Utils.filteringMap.keys.forEach { key ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        val filterTextFieldKey = Utils.getTaskFilterStringId(key)
                                            ?.let { filter -> stringResource(filter) } ?: key
                                        if (teamVm.filterTextField == key) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(6.dp, 0.dp)
                                                    .border(
                                                        width = 2.dp,
                                                        color = colorResource(id = R.color.primary_color),
                                                        shape = MaterialTheme.shapes.medium
                                                    )
                                                    .background(
                                                        color = colorResource(id = R.color.secondary_color),
                                                        shape = MaterialTheme.shapes.medium
                                                    )
                                                    .clip(MaterialTheme.shapes.medium)
                                            ) {
                                                Text(
                                                    text = if (key == "${memberAccessedValue.name} ${memberAccessedValue.surname}") {
                                                        "$filterTextFieldKey (${stringResource(R.string.me)})"
                                                    } else {
                                                        filterTextFieldKey
                                                    },
                                                    modifier = Modifier.padding(10.dp, 2.dp)
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = if (key == "${memberAccessedValue.name} ${memberAccessedValue.surname}") {
                                                    "$filterTextFieldKey (${stringResource(R.string.me)})"
                                                } else {
                                                    filterTextFieldKey
                                                }
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    Utils.playSoundEffectWithSoundCheck(
                                        view,
                                        SoundEffectConstants.CLICK
                                    )
                                    teamVm.filterTextField = key
                                    setFilterField(Utils.filteringMap.getValue(key))
                                    teamVm.filteringDropdownMenuOpened = false
                                }
                            )

                        }

                        tags.forEach { tag ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (teamVm.filterTextField == tag) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(6.dp, 0.dp)
                                                    .border(
                                                        width = 2.dp,
                                                        color = colorResource(id = R.color.primary_color),
                                                        shape = MaterialTheme.shapes.medium
                                                    )
                                                    .background(
                                                        color = colorResource(id = R.color.secondary_color),
                                                        shape = MaterialTheme.shapes.medium
                                                    )
                                                    .clip(MaterialTheme.shapes.medium)
                                            ) {
                                                Text(
                                                    text = tag,
                                                    modifier = Modifier.padding(10.dp, 2.dp)
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = tag
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    Utils.playSoundEffectWithSoundCheck(
                                        view,
                                        SoundEffectConstants.CLICK
                                    )
                                    teamVm.filterTextField = tag
                                    setFilterField {
                                        it.assignTagsField.value.contains(tag)
                                    }
                                    teamVm.filteringDropdownMenuOpened = false
                                }
                            )
                        }

                    }
                }
            }
        }

        // Search field
        if (!teamVm.filterAreaOpened && !teamVm.sortAreaOpened) {
            OutlinedTextField(
                value = teamVm.searchByNameTextField,
                onValueChange = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    teamVm.searchByNameTextField = it
                    setSearchByNameField(it)
                },
                label = {
                    Text(
                        stringResource(id = R.string.search_task_by_name),
                        fontSize = TextUnit(13f, TextUnitType.Sp)
                    )
                },
                trailingIcon = {
                    Row(
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (teamVm.searchByNameTextField.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    Utils.playSoundEffectWithSoundCheck(
                                        view,
                                        SoundEffectConstants.CLICK
                                    )
                                    teamVm.searchByNameTextField = ""
                                    setSearchByNameField("")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = stringResource(id = R.string.delete_field)
                                )
                            }
                        }

                        IconButton(onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            setSearchByNameField(teamVm.searchByNameTextField)
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = stringResource(id = R.string.search_task_by_name)
                            )
                        }

                    }

                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }

        // Sort button and dropdown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(sortWeightState.value)
                .border(
                    width = 1.dp,
                    color = if (teamVm.sortTextField == Utils.orderingMap.keys.first()) Color.Gray else colorResource(
                        id = R.color.primary_color
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .background(
                    if (teamVm.sortTextField == Utils.orderingMap.keys.first()) Color.Transparent else colorResource(
                        id = R.color.secondary_color
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(start = if (teamVm.sortAreaOpened) 10.dp else 0.dp),
            horizontalArrangement = if (teamVm.sortAreaOpened) Arrangement.SpaceBetween else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (teamVm.sortAreaOpened) {
                val sortTextField = Utils.getTaskOrderStringId(teamVm.sortTextField)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(7f)
                        .clickable { teamVm.sortingDropdownMenuOpened = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(
                                1.dp,
                                colorResource(id = R.color.primary_color),
                                RoundedCornerShape(20.dp)
                            )
                            .background(
                                color = colorResource(id = R.color.secondary_color),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clip(RoundedCornerShape(20.dp)),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (sortTextField != null) {
                                stringResource(sortTextField)
                            } else {
                                teamVm.sortTextField
                            },
                            style = MaterialTheme.typography.labelSmall
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        if (teamVm.sortTextField != Utils.orderingMap.keys.first()) {
                            IconButton(
                                onClick = {
                                    Utils.playSoundEffectWithSoundCheck(
                                        view,
                                        SoundEffectConstants.CLICK
                                    )
                                    teamVm.sortTextField = Utils.orderingMap.keys.first()
                                    setSortField { _, _ -> 0 }
                                },
                                modifier = Modifier.width(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = stringResource(id = R.string.delete_field)
                                )
                            }
                        }
                        IconButton(onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            teamVm.sortingDropdownMenuOpened = true
                        }) {
                            Icon(
                                imageVector = if (teamVm.sortingDropdownMenuOpened) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                contentDescription = if (teamVm.sortingDropdownMenuOpened) stringResource(
                                    id = R.string.collapse_sorting_field_dropdown_menu
                                ) else stringResource(id = R.string.expand_sorting_field_dropdown_menu)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = teamVm.sortingDropdownMenuOpened,
                        onDismissRequest = { teamVm.sortingDropdownMenuOpened = false }
                    ) {
                        Utils.orderingMap.keys.forEach { key ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        val sortTextFieldKey = Utils.getTaskOrderStringId(key)
                                        if (teamVm.sortTextField == key) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(6.dp, 0.dp)
                                                    .border(
                                                        width = 2.dp,
                                                        color = colorResource(id = R.color.primary_color),
                                                        shape = MaterialTheme.shapes.medium
                                                    )
                                                    .background(
                                                        color = colorResource(id = R.color.secondary_color),
                                                        shape = MaterialTheme.shapes.medium
                                                    )
                                                    .clip(MaterialTheme.shapes.medium)
                                            ) {
                                                Text(
                                                    text = if (sortTextFieldKey != null) {
                                                        stringResource(sortTextFieldKey)
                                                    } else {
                                                        key
                                                    },
                                                    modifier = Modifier.padding(10.dp, 2.dp)
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = if (sortTextFieldKey != null) {
                                                    stringResource(sortTextFieldKey)
                                                } else {
                                                    key
                                                }
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    Utils.playSoundEffectWithSoundCheck(
                                        view,
                                        SoundEffectConstants.CLICK
                                    )
                                    teamVm.sortTextField = key
                                    setSortField(Utils.orderingMap.getValue(key))
                                    teamVm.sortingDropdownMenuOpened = false
                                }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    teamVm.filterAreaOpened = false
                    teamVm.sortAreaOpened = !teamVm.sortAreaOpened
                },
                modifier = Modifier
                    .height(56.dp)
                    .weight(2f)
                    .wrapContentSize(unbounded = true),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = "Sort icon",
                    tint = if (teamVm.sortTextField == Utils.orderingMap.keys.first()) Color.Gray else colorResource(
                        id = R.color.primary_color
                    )
                )
            }

        }
    }


    val filterTextFieldTranslated = Utils.getTaskFilterStringId(teamVm.filterTextField)
        ?.let { stringResource(it) } ?: ""

    if (filterTextFieldTranslated == stringResource(R.string.start_date)) {

        var fromIsOpen by remember { mutableStateOf(false) }
        var toIsOpen by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 0.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.Top
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = teamVm.fromStartDateFilter.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                readOnly = true,
                isError = teamVm.fromInputError,
                supportingText = {
                    if (teamVm.fromInputError) {
                        Text(
                            text = stringResource(R.string.cannot_select_post_date),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(start = 5.dp, top = 3.dp),
                            color = colorResource(R.color.redError)
                        )
                    }
                },
                onValueChange = {
                },
                label = {
                    Text(
                        stringResource(id = R.string.from),
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            fromIsOpen = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null
                        )
                    }
                }
            )

            if (fromIsOpen) {
                DatePickerDialog(
                    onAccept = { selectedDate ->
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        if (selectedDate != null) {
                            val instant = Instant.ofEpochMilli(selectedDate)
                            val selectedLocalDate =
                                instant.atZone(ZoneId.systemDefault()).toLocalDate()

                            if (selectedLocalDate.isBefore(teamVm.toStartDateFilter)) {
                                teamVm.fromStartDateFilter = selectedLocalDate
                                teamVm.fromInputError = false
                            } else {
                                teamVm.fromInputError = true
                            }
                        }
                        fromIsOpen = false
                    },
                    onCancel = {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        fromIsOpen = false
                        teamVm.fromInputError = true
                    }
                )
            }

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = teamVm.toStartDateFilter.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                readOnly = true,
                isError = teamVm.toInputError,
                supportingText = {
                    if (teamVm.toInputError) {
                        Text(
                            text = stringResource(R.string.cannot_select_prev_date),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(start = 5.dp, top = 3.dp),
                            color = colorResource(R.color.redError)
                        )
                    }
                },
                onValueChange = {
                },
                label = {
                    Text(
                        stringResource(id = R.string.to),
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            toIsOpen = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null
                        )
                    }
                }
            )

            if (toIsOpen) {
                DatePickerDialog(
                    onAccept = { selectedDate ->
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        if (selectedDate != null) {
                            val instant = Instant.ofEpochMilli(selectedDate)
                            val selectedLocalDate =
                                instant.atZone(ZoneId.systemDefault()).toLocalDate()

                            if (selectedLocalDate.isAfter(teamVm.fromStartDateFilter)) {
                                teamVm.toStartDateFilter = selectedLocalDate
                                teamVm.toInputError = false
                            } else {
                                teamVm.toInputError = true
                            }
                        }
                        toIsOpen = false
                    },
                    onCancel = {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        toIsOpen = false
                        teamVm.toInputError = false
                    }
                )
            }

        }
    }

    if (filterTextFieldTranslated == stringResource(R.string.due_date)) {

        var fromIsOpen by remember { mutableStateOf(false) }
        var toIsOpen by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 0.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.Top
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = teamVm.fromDueDateFilter.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                readOnly = true,
                isError = teamVm.fromInputError,
                supportingText = {
                    if (teamVm.fromInputError) {
                        Text(
                            text = stringResource(R.string.cannot_select_post_date),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(start = 5.dp, top = 3.dp),
                            color = colorResource(R.color.redError)
                        )
                    }
                },
                onValueChange = {
                },
                label = {
                    Text(
                        stringResource(id = R.string.from),
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            fromIsOpen = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null
                        )
                    }
                }
            )

            if (fromIsOpen) {
                DatePickerDialog(
                    onAccept = { selectedDate ->
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        if (selectedDate != null) {
                            val instant = Instant.ofEpochMilli(selectedDate)
                            val selectedLocalDate =
                                instant.atZone(ZoneId.systemDefault()).toLocalDate()

                            if (selectedLocalDate.isBefore(teamVm.toDueDateFilter)) {
                                teamVm.fromDueDateFilter = selectedLocalDate
                                teamVm.fromInputError = false
                            } else {
                                teamVm.fromInputError = true
                            }
                        }
                        fromIsOpen = false
                    },
                    onCancel = {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        fromIsOpen = false
                        teamVm.fromInputError = true
                    }
                )
            }

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = teamVm.toDueDateFilter.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                readOnly = true,
                isError = teamVm.toInputError,
                supportingText = {
                    if (teamVm.toInputError) {
                        Text(
                            text = stringResource(R.string.cannot_select_prev_date),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(start = 5.dp, top = 3.dp),
                            color = colorResource(R.color.redError)
                        )
                    }
                },
                onValueChange = {
                },
                label = {
                    Text(
                        stringResource(id = R.string.to),
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            toIsOpen = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null
                        )
                    }
                }
            )

            if (toIsOpen) {
                DatePickerDialog(
                    onAccept = { selectedDate ->
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        if (selectedDate != null) {
                            val instant = Instant.ofEpochMilli(selectedDate)
                            val selectedLocalDate =
                                instant.atZone(ZoneId.systemDefault()).toLocalDate()

                            if (selectedLocalDate.isAfter(teamVm.fromDueDateFilter)) {
                                teamVm.toDueDateFilter = selectedLocalDate
                                teamVm.toInputError = false
                            } else {
                                teamVm.toInputError = true
                            }
                        }
                        toIsOpen = false
                    },
                    onCancel = {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        toIsOpen = false
                        teamVm.toInputError = false
                    }
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabsBar(selectedTabIndex: Int, tabTitles: List<String>, onTabSelected: (Int) -> Unit) {
    val view = LocalView.current

    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex
    ) {
        tabTitles.forEachIndexed { index, tabTitle ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    onTabSelected(index)
                },
                modifier = Modifier.wrapContentWidth(unbounded = true),
                text = {
                    Text(
                        text = tabTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = TextUnit(12f, TextUnitType.Sp)
                    )
                }
            )
        }
    }
}

@Composable
fun PageContent(
    teamVm: TeamViewModel,
    tabTitles: List<Int>,
    selectedTabIndex: Int,
    selectTask: (String, String) -> Unit,
    searchByNameField: String,
    sortField: Comparator<TaskViewModel>,
    routerActions: RouterActions
) {
    val view = LocalView.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 200.dp)
            .padding(15.dp, 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        teamVm.currentPageTasks = teamVm.tasksList.toList()
            .filter {
                stringResource(id = Utils.convertStatusToString(it.statusField.status)) == stringResource(
                    tabTitles[selectedTabIndex]
                )
            }
            .filter {
                searchByNameField.isBlank() || it.titleField.value.lowercase()
                    .contains(searchByNameField.lowercase())    //Name searching
            }
            .filter { teamVm.filterField(it) }
            .sortedWith(sortField)  //Sorting

        if (teamVm.filterTextField == stringResource(R.string.start_date)) {
            teamVm.currentPageTasks = teamVm.currentPageTasks!!.filter {
                it.startDateField.date.isAfter(teamVm.fromStartDateFilter) && it.startDateField.date.isBefore(
                    teamVm.toStartDateFilter
                )
            }
        } else if (teamVm.filterTextField == stringResource(R.string.due_date)) {
            teamVm.currentPageTasks = teamVm.currentPageTasks!!.filter {
                it.dueDateField.date.isAfter(teamVm.fromDueDateFilter) && it.dueDateField.date.isBefore(
                    teamVm.toDueDateFilter
                )
            }
        }

        if (teamVm.currentPageTasks!!.isEmpty()) {
            Text(
                text = stringResource(id = R.string.no_tasks_having) + " " + stringResource(
                    tabTitles[selectedTabIndex]
                ).lowercase() + " " + stringResource(
                    id = R.string.status
                ).lowercase() + ".", fontSize = 13.sp
            )
            if (selectedTabIndex == 0)
                Text(text = stringResource(id = R.string.create_one_to_start), fontSize = 13.sp)
        } else {
            teamVm.currentPageTasks!!.forEach { task ->

                Box(
                    modifier = Modifier
                        .clickable {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            selectTask(teamVm.teamField.id, task.id)
                        }
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                teamVm.offsetX += delta
                            },
                            onDragStarted = {
                                if (task.canEditBy()) {
                                    teamVm.draggedTask = task
                                }
                            },
                            onDragStopped = {
                                if (task.canEditBy()) {
                                    if (teamVm.offsetX > 400f && (teamVm.selectedTabIndex + 1) < tabTitles.size) {
                                        task.statusField.setVal(
                                            statusParam = Utils.convertStringToStatus(tabTitles[teamVm.selectedTabIndex + 1]),
                                            taskId = task.id
                                        )
                                    } else if (teamVm.offsetX < -400f && (teamVm.selectedTabIndex - 1) >= 0) {
                                        task.statusField.setVal(
                                            statusParam = Utils.convertStringToStatus(tabTitles[teamVm.selectedTabIndex - 1]),
                                            taskId = task.id
                                        )
                                    }
                                }
                                teamVm.offsetX = 0f
                                teamVm.draggedTask = null
                            }
                        )
                        .offset {
                            if (teamVm.draggedTask !== null && task == teamVm.draggedTask) {
                                if (teamVm.selectedTabIndex == 0 && teamVm.offsetX < 0) {
                                    IntOffset(
                                        0,
                                        0
                                    )
                                } else if (teamVm.selectedTabIndex == 4 && teamVm.offsetX > 0) {
                                    IntOffset(
                                        0,
                                        0
                                    )
                                } else {
                                    IntOffset(
                                        teamVm.offsetX.roundToInt(),
                                        0
                                    )
                                }

                            } else {
                                IntOffset(0, 0)
                            }
                        }
                ) {

                    Task(
                        teamVm = teamVm,
                        task = task,
                        routerActions = routerActions
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}


@Composable
fun Task(
    teamVm: TeamViewModel,
    task: TaskViewModel,
    routerActions: RouterActions
) {
    val memberAccessedValue = Utils.memberAccessed.collectAsState().value

    Column(
        modifier = Modifier
            .clickable {
                routerActions.navigateToViewTask(teamVm.teamField.id, task.id)
            }
            .fillMaxWidth()
            .height(120.dp)
            .background(
                color = colorResource(id = R.color.secondary_color).copy(
                    alpha = if (task.delegateTasksField.members.any { it.profile.id == memberAccessedValue.id }
                        || task.creationField.createdBy.id == memberAccessedValue.id
                    ) .9f else .3f
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                1.dp,
                colorResource(id = R.color.primary_color).copy(alpha = .3f),
                shape = RoundedCornerShape(10.dp)
            ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(if (task.canEditBy()) .7f else 1f)
            ) {
                Text(
                    text = task.titleField.value,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Calendar icon",
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = task.startDateField.dateToString(),
                        fontWeight = FontWeight.Light,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowForward,
                        contentDescription = "Arrow forward",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = task.dueDateField.dateToString(),
                        fontWeight = FontWeight.Light,
                        fontSize = 13.sp
                    )
                }
            }

            if (task.canEditBy()) {
                TaskButtons(
                    teamVm = teamVm,
                    task = task,
                    routerActions = routerActions,
                    onSetOpenAlertDialog = { teamVm.openDeleteAlertDialog = task }
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                val members = task.delegateTasksField.members
                    .sortedByDescending { it.profile.id == memberAccessedValue.id }
                    .take(minOf(3, task.delegateTasksField.members.size))
                    .toMutableList()

                Box(
                    modifier = Modifier
                        .offset((20 * (members.size - 1)).dp, 0.dp)
                        .align(Alignment.CenterEnd)
                ) {
                    members.reversed().forEachIndexed { index, member ->
                        Box(
                            modifier = Modifier
                                .offset((-20 * index).dp, 0.dp)
                                .border(
                                    1.dp,
                                    color = if (member.profile.id == memberAccessedValue.id) colorResource(
                                        id = R.color.attention_color
                                    ) else colorResource(
                                        id = R.color.primary_color
                                    ),
                                    shape = CircleShape
                                )
                        ) {
                            if (member.profile.photo != null) {
                                ImageView(
                                    painterBitmap = member.profile.photo!!,
                                    size = 35.dp
                                )
                            } else {
                                AvatarView(
                                    name = member.profile.name,
                                    surname = member.profile.surname,
                                    35
                                )
                            }
                        }
                    }
                    if (members.size < task.delegateTasksField.members.size) {
                        Text(
                            text = "+ ${task.delegateTasksField.members.size - members.size} ${
                                stringResource(R.string.more)
                            }",
                            fontSize = TextUnit(10f, TextUnitType.Sp),
                            modifier = Modifier.offset(40.dp, (5).dp),
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.End) {
                Text(
                    text = stringResource(Utils.convertCategoryToString(task.categoryField.category)),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                )
            }
        }

    }

    if (teamVm.openDeleteAlertDialog == task)
        DeleteTaskAlertDialog(
            team = teamVm,
            task = task,
            onDeleteConfirmed = {
                teamVm.deleteTask(task)
            },
            onDismissRequest = {
                teamVm.openDeleteAlertDialog = null
            }
        )
}

@Composable
fun AddTaskFloatingButton(
    teamVm: TeamViewModel,
    routerActions: RouterActions
) {
    val view = LocalView.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp, 20.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        ExtendedFloatingActionButton(
            text = { Text(stringResource(R.string.add_task)) },
            icon = { Icon(Icons.Filled.Add, stringResource(R.string.create_task)) },
            onClick = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                routerActions.navigateToCreateTask(teamVm.teamField.id)
            },
            containerColor = colorResource(id = R.color.primary_color),
            contentColor = colorResource(id = R.color.white)
        )
    }
}

@Composable
fun ChangeStatusDropdownMenu(
    changeStatusMenuOpened: Boolean,
    onMenuClosed: () -> Unit,
    task: TaskViewModel
) {
    val view = LocalView.current
    Box(
        modifier = Modifier
            .offset(x = (-48).dp)
            .padding(start = 4.dp)
            .width(IntrinsicSize.Min)
    ) {
        DropdownMenu(
            expanded = changeStatusMenuOpened,
            onDismissRequest = { onMenuClosed() },
            modifier = Modifier.padding(4.dp)
        ) {
            Utils.StatusEnum.entries.map { Utils.convertStatusToString(it) }.forEach { text ->
                val stringText = stringResource(id = text)
                DropdownMenuItem(
                    onClick = {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        task.statusField.setVal(
                            statusParam = Utils.convertStringToStatus(value = text),
                            taskId = task.id
                        )
                        onMenuClosed()
                    },
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (Utils.convertStatusToString(task.statusField.status) == text) {
                                Box(
                                    modifier = Modifier
                                        .padding(6.dp, 0.dp)
                                        .border(
                                            width = 2.dp,
                                            color = colorResource(id = R.color.primary_color),
                                            shape = MaterialTheme.shapes.medium
                                        )
                                        .background(
                                            color = colorResource(id = R.color.secondary_color),
                                            shape = MaterialTheme.shapes.medium
                                        )
                                        .clip(MaterialTheme.shapes.medium)
                                ) {
                                    Text(
                                        text = stringText,
                                        modifier = Modifier.padding(10.dp, 2.dp)
                                    )
                                }
                            } else {
                                Text(text = stringText)
                            }

                        }

                    }
                )
            }
        }
    }
}


@Composable
fun TaskButtons(
    teamVm: TeamViewModel,
    task: TaskViewModel,
    routerActions: RouterActions,
    onSetOpenAlertDialog: (TaskViewModel?) -> Unit
) {
    val view = LocalView.current
    val mediaPlayerDialog: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.dialog)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {

        ChangeStatusDropdownMenu(
            changeStatusMenuOpened = teamVm.changeStatusMenuOpened == task,
            onMenuClosed = { teamVm.changeStatusMenuOpened = null },
            task = task
        )

        val iconWidth: Dp = 30.dp
        IconButton(
            onClick = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                teamVm.changeStatusMenuOpened = task
            },
            modifier = Modifier
                .width(iconWidth)
                .offset(x = 0.dp, y = (-15).dp)
        ) {
            Icon(Icons.Outlined.MoreHoriz, stringResource(R.string.change_status_task))
        }

        IconButton(
            modifier = Modifier
                .width(iconWidth)
                .offset(x = 0.dp, y = (-15).dp),
            onClick = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                routerActions.navigateToModifyTask(teamVm.teamField.id, task.id)
            }
        ) {
            Icon(Icons.Outlined.Edit, stringResource(R.string.modify_task))
        }

        IconButton(
            onClick = {
                Utils.useSafeMediaPlayer(mediaPlayerDialog)
                onSetOpenAlertDialog(task)
            },
            colors = IconButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Red,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.LightGray
            ),
            modifier = Modifier
                .width(iconWidth)
                .offset(x = 0.dp, y = (-15).dp)
        ) {
            Icon(Icons.Outlined.Delete, stringResource(R.string.delete_task))
        }
    }
}
