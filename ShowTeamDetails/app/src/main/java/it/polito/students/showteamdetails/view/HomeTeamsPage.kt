package it.polito.students.showteamdetails.view

//noinspection UsingMaterialAndMaterial3Libraries
import android.media.MediaPlayer
import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.TeamRow
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.TeamListViewModel
import it.polito.students.showteamdetails.viewmodel.TeamViewModel

@Composable
fun HomeTeamsPage(routerActions: RouterActions, teamListVm: TeamListViewModel) {
    val currentTeams by teamListVm.teamList.collectAsState()

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
                .padding(horizontal = 15.dp)
                .fillMaxSize(pageWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SearchSortFilterTeam(
                    currentTeams = currentTeams.toMutableList(),
                    teamListVm = teamListVm
                )
                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (teamListVm.isTeamsListLoading) {
                        CircularProgressIndicator()
                    } else {
                        if (currentTeams.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.no_teams),
                                fontSize = 13.sp
                            )
                        } else {
                            currentTeams.map { team ->
                                TeamRow(
                                    teamVm = team,
                                    isTeamsListHomePage = true,
                                    routerActions = routerActions,
                                    teamListVm = teamListVm,
                                    onTeamClicked = {
                                        routerActions.navigateToTeam(team.teamField.id)
                                    }
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun TeamButtons(
    teamVm: TeamViewModel,
    routerActions: RouterActions,
    teamListVm: TeamListViewModel
) {
    val view = LocalView.current
    val mediaPlayerDialog: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.dialog)

    Row(
        modifier = Modifier.padding(end = 10.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {

        val iconWidth: Dp = 30.dp
        val positionY = (-15).dp
        IconButton(
            modifier = Modifier
                .width(iconWidth)
                .offset(x = 0.dp, y = positionY),
            onClick = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                routerActions.navigateToEditTeam(teamVm.teamField.id)
            }
        ) {
            Icon(Icons.Outlined.Edit, stringResource(R.string.team_settings))
        }

        IconButton(
            onClick = {
                Utils.useSafeMediaPlayer(mediaPlayerDialog)
                routerActions.navigateToDeleteTeam(teamVm.teamField.id)
            },
            colors = IconButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Red,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.LightGray
            ),
            modifier = Modifier
                .width(iconWidth)
                .offset(x = 0.dp, y = positionY)
        ) {
            Icon(Icons.Outlined.Delete, stringResource(R.string.delete_team))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSortFilterTeam(
    teamListVm: TeamListViewModel,
    currentTeams: MutableList<TeamViewModel>
) {
    val view = LocalView.current
    val filterActivated = remember { mutableStateOf(false) }
    val filterMenuOpened = remember { mutableStateOf(false) }

    val filterByType = remember { mutableStateOf<Utils.FilterTeamBy?>(null) }
    val searchOrFilterByValue = remember { mutableStateOf("") }

    // Search and Filter Logic
    val filteredTeams by remember {
        derivedStateOf {
            teamListVm.teamList.value.filter { team ->
                team.teamField.nameField.value.contains(
                    searchOrFilterByValue.value,
                    ignoreCase = true
                )
                        || team.teamField.categoryField.category == (searchOrFilterByValue.value.toIntOrNull()
                    ?: -1)
            }
        }
    }

    // Apply the search and filter when the value changes
    LaunchedEffect(searchOrFilterByValue.value) {
        currentTeams.clear()
        currentTeams.addAll(filteredTeams)
    }

    // Search Bar
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (!filterActivated.value) {
            /* Filter Icon Button */
            Button(
                onClick = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    filterActivated.value = true
                    filterMenuOpened.value = false
                    searchOrFilterByValue.value = ""
                    filterByType.value = null
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
                    tint = colorResource(R.color.primary_color)
                )
            }
            /* Filter Icon Button */
        } else {
            /* Search Icon Button */
            Button(
                onClick = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    filterActivated.value = false
                    filterMenuOpened.value = false
                    searchOrFilterByValue.value = ""
                    filterByType.value = null
                },
                modifier = Modifier
                    .height(56.dp)
                    .wrapContentSize(unbounded = true),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(id = R.string.search),
                    tint = colorResource(R.color.primary_color)
                )
            }
            /* Search Icon Button */
        }

        if (!filterActivated.value) {
            /* Search By Name */
            SearchOrFilterFieldComponent(searchOrFilterByValue)
            /* Search By Name */
        } else {
            Column {
                /* Filter By Field */
                ExposedDropdownMenuBox(
                    expanded = filterMenuOpened.value,
                    onExpandedChange = { filterMenuOpened.value = it }
                ) {
                    val filterValue = if (filterByType.value != null) stringResource(
                        Utils.convertFilterTeamByToString(filterByType.value!!)
                    ) else ""

                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        value = filterValue,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        label = {
                            Text(
                                stringResource(
                                    id = R.string.filter_by
                                ),
                                color = colorResource(R.color.darkGray),
                                fontSize = 13.sp
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterMenuOpened.value) },
                    )

                    ExposedDropdownMenu(
                        expanded = filterMenuOpened.value,
                        onDismissRequest = { filterMenuOpened.value = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Utils.FilterTeamBy.entries.forEach { filterValue ->
                            DropdownMenuItem(
                                onClick = {
                                    filterByType.value = filterValue
                                    filterMenuOpened.value = false
                                }
                            ) {
                                Text(
                                    text = stringResource(
                                        Utils.convertFilterTeamByToString(
                                            filterValue
                                        )
                                    )
                                )
                            }
                        }
                    }
                    /* Filter By Field */
                }
                if (filterByType.value == Utils.FilterTeamBy.CATEGORY) {
                    Spacer(modifier = Modifier.height(7.dp))
                    /* Filter By Category */
                    SearchOrFilterFieldComponent(
                        fieldValue = searchOrFilterByValue,
                        title = R.string.category,
                        filterByType = filterByType.value
                    )
                    /* Filter By Category */
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchOrFilterFieldComponent(
    fieldValue: MutableState<String>,
    title: Int = R.string.search,
    filterByType: Utils.FilterTeamBy? = null
) {
    val view = LocalView.current
    val isMenuExpanded = remember { mutableStateOf(false) }
    val readOnly = filterByType == Utils.FilterTeamBy.CATEGORY

    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = isMenuExpanded.value,
        onExpandedChange = {
            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
            isMenuExpanded.value = it
        }) {
        val filterValue =
            if (filterByType == Utils.FilterTeamBy.CATEGORY) fieldValue.value.toIntOrNull()
                ?.let { stringResource(it) }
            else fieldValue.value

        OutlinedTextField(
            value = filterValue ?: "",
            onValueChange = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                fieldValue.value = it
            },
            label = {
                Text(
                    stringResource(title),
                    fontSize = TextUnit(13f, TextUnitType.Sp)
                )
            },
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.End
                ) {
                    if (fieldValue.value.isNotBlank()) {
                        IconButton(
                            onClick = {
                                Utils.playSoundEffectWithSoundCheck(
                                    view,
                                    SoundEffectConstants.CLICK
                                )
                                fieldValue.value = ""
                            },
                            modifier = Modifier.offset(0.dp, 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(id = R.string.delete_field)
                            )
                        }
                    }

                    if (filterByType == null) {
                        IconButton(onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = stringResource(id = R.string.search)
                            )
                        }
                    }

                }

            },
            readOnly = readOnly,
            singleLine = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        if (filterByType == Utils.FilterTeamBy.CATEGORY) {
            ExposedDropdownMenu(
                expanded = isMenuExpanded.value,
                onDismissRequest = { isMenuExpanded.value = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Utils.categoryTeamList().map { category ->
                    DropdownMenuItem(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            isMenuExpanded.value = false
                            fieldValue.value = category.toString()
                        },
                    ) {
                        Text(text = stringResource(category))
                    }
                }
            }
        }
    }
}