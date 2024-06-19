package it.polito.students.showteamdetails.view

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.media.MediaPlayer
import android.view.SoundEffectConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.ButtonsTeamSection
import it.polito.students.showteamdetails.EditPhotoSection
import it.polito.students.showteamdetails.MemberDetailRow
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.SemiBoldText
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.TeamListViewModel
import it.polito.students.showteamdetails.viewmodel.TeamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyTeamPage(
    teamVm: TeamViewModel,
    scaffoldState: BottomSheetScaffoldState,
    routerActions: RouterActions,
    teamListVm: TeamListViewModel
) {
    val view = LocalView.current
    val menuOpened = remember { mutableStateOf(false) }
    val routes = listOf(
        Utils.RoutesEnum.MODIFY_TEAM.name
    )
    var isCreate = false

    var pageTitle = ""
    if (routerActions.getCurrentRoute()
            .contains(Utils.RoutesEnum.ADD_TEAM.name)
    ) {
        pageTitle = stringResource(R.string.create_team)
        isCreate = true
    } else {
        pageTitle = stringResource(R.string.team_settings)
        isCreate = false
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
                    // Title
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = { routerActions.goBack() }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                                    contentDescription = stringResource(R.string.go_back),
                                    modifier = Modifier.size(35.dp)
                                )
                            }

                            Spacer(modifier = Modifier.weight(0.05f))

                            Text(
                                text = pageTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 25.sp,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                            )

                            Spacer(modifier = Modifier.weight(0.05f))

                            if (routes.any { routerActions.getCurrentRoute().contains(it) }) {
                                // Three Dots Action
                                ActionsOnTeamDropdownMenu(
                                    menuOpened = menuOpened.value,
                                    onMenuClosed = { menuOpened.value = false },
                                    teamVm = teamVm,
                                    routerActions = routerActions
                                )

                                // Three Dots Icon
                                IconButton(
                                    onClick = {
                                        Utils.playSoundEffectWithSoundCheck(
                                            view,
                                            SoundEffectConstants.CLICK
                                        )
                                        menuOpened.value = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Outlined.MoreHoriz,
                                        stringResource(R.string.leave_team_confirmation)
                                    )
                                }
                            }
                        }
                    }

                    // Section Name and Picture
                    NameTeamEditSection(teamVm, scaffoldState, isCreate)
                    Spacer(modifier = Modifier.height(15.dp))

                    // Created By
                    CreatedTeamSection(teamVm)
                    Spacer(modifier = Modifier.height(15.dp))

                    // Section Description, Category
                    DescriptionTeamEditSection(teamVm, isCreate)
                    Spacer(modifier = Modifier.height(15.dp))
                    CategoryTeamEditSection(teamVm, isCreate)
                    Spacer(modifier = Modifier.height(15.dp))

                    // Row Save Button
                    if (teamVm.isModifiedSomething()) {
                        ButtonsTeamSection(
                            teamVm = teamVm,
                            teamListVm = teamListVm,
                            routerActions = routerActions
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (routes.any { routerActions.getCurrentRoute().contains(it) }) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            SemiBoldText(stringResource(R.string.team_members))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        MembersTeamEditSection(teamVm, routerActions)
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun CreatedTeamSection(teamVm: TeamViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val member = teamVm.teamField.creationField.createdBy

        if (member.photo != null)
            it.polito.students.showteamdetails.ImageView(
                painterBitmap = member.photo,
                size = 45.dp
            )
        else
            it.polito.students.showteamdetails.AvatarView(
                name = member.name,
                surname = member.surname,
                size = 45
            )

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.team_created_by))
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                    )
                ) {
                    append(" ${teamVm.teamField.creationField.createdBy.name} ${teamVm.teamField.creationField.createdBy.surname}")
                }
            },
            fontSize = 18.sp,
        )
    }
    Spacer(modifier = Modifier.height(15.dp))

    // Creation date
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            painter = painterResource(R.drawable.due_date),
            contentDescription = null,
            modifier = Modifier.size(23.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buildAnnotatedString {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("${stringResource(R.string.creation_date)}: ")
                pop()
                append(Utils.formatLocalDateTime(teamVm.teamField.creationField.dateCreation))
            },
            fontSize = 18.sp,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameTeamEditSection(
    teamVm: TeamViewModel,
    scaffoldState: BottomSheetScaffoldState,
    isCreate: Boolean
) {
    val view = LocalView.current

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (teamVm.teamField.nameField.editValue.isNotEmpty()) {
            EditPhotoSection(
                vm = teamVm.teamField.picture,
                scaffoldState = scaffoldState,
                name = teamVm.teamField.nameField.editValue,
                surname = null,
                canEditBy = isCreate || teamVm.canEditBy()
            )
            Spacer(modifier = Modifier.height(15.dp))
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 5.dp),
            value = teamVm.teamField.nameField.editValue,
            onValueChange = {
                if (isCreate || teamVm.canEditBy()) {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    teamVm.teamField.nameField.setEditVal(it)
                }
            },
            label = {
                Text(
                    stringResource(
                        id = R.string.name
                    ),
                    color = colorResource(R.color.darkGray),
                    fontSize = 16.sp
                )
            },
            isError = teamVm.teamField.nameField.error != -1,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 30.sp
            ),
            enabled = isCreate || teamVm.canEditBy(),
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = true,
                capitalization = KeyboardCapitalization.Words
            )
        )

        if (teamVm.teamField.nameField.error != -1)
            Text(
                text = stringResource(teamVm.teamField.nameField.error),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(5.dp, 3.dp),
                color = colorResource(R.color.redError)
            )
    }
}

@Composable
fun DescriptionTeamEditSection(teamVm: TeamViewModel, isCreate: Boolean) {
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
                value = teamVm.descriptionField.editValue,
                onValueChange = {
                    if (teamVm.canEditBy() || isCreate) {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        teamVm.descriptionField.editValue = it
                    }
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
                isError = teamVm.descriptionField.error != -1,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 30.sp
                ),
                enabled = teamVm.canEditBy() || isCreate

            )
            if (teamVm.descriptionField.error != -1) {
                Text(
                    text = stringResource(teamVm.descriptionField.error),
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
fun CategoryTeamEditSection(teamVm: TeamViewModel, isCreate: Boolean) {
    val isMenuExpanded = remember { mutableStateOf(false) }
    val view = LocalView.current

    Column {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = isMenuExpanded.value,
            onExpandedChange = {
                if (teamVm.canEditBy() || isCreate) {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    isMenuExpanded.value = it
                }
            }) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = stringResource(teamVm.teamField.categoryField.editCategory),
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
                trailingIcon = {
                    if (teamVm.canEditBy() || isCreate) {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMenuExpanded.value)
                    }
                },
                enabled = teamVm.canEditBy(),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = colorResource(R.color.black),
                )
            )
            if (teamVm.canEditBy() || isCreate) {
                ExposedDropdownMenu(
                    expanded = isMenuExpanded.value,
                    onDismissRequest = { isMenuExpanded.value = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Utils.categoryTeamList().map { category ->
                        DropdownMenuItem(
                            onClick = {
                                Utils.playSoundEffectWithSoundCheck(
                                    view,
                                    SoundEffectConstants.CLICK
                                )
                                teamVm.teamField.categoryField.editCategory = category
                                teamVm.teamField.categoryField.error = -1
                                isMenuExpanded.value = false
                            },
                            //contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        ) {
                            Text(text = stringResource(category))
                        }
                    }
                }
            }
        }
        if (teamVm.teamField.categoryField.error != -1) {
            Text(
                text = stringResource(teamVm.teamField.categoryField.error),
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

@Composable
fun MembersTeamEditSection(teamVm: TeamViewModel, routerActions: RouterActions) {
    val view = LocalView.current
    val searchByNameValue = remember { mutableStateOf("") }
    val memberAccessedValue = Utils.memberAccessed.collectAsState().value

    val currentMembers = teamVm.teamField.membersField.editMembers.filter {
        it.profile.name.contains(
            searchByNameValue.value,
            ignoreCase = true
        )
    }

    // Search Bar
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchByNameValue.value,
            onValueChange = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                searchByNameValue.value = it
            },
            label = {
                Text(
                    stringResource(id = R.string.search_member_by_name),
                    fontSize = TextUnit(13f, TextUnitType.Sp)
                )
            },
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.End
                ) {
                    if (searchByNameValue.value.isNotBlank()) {
                        IconButton(
                            onClick = {
                                Utils.playSoundEffectWithSoundCheck(
                                    view,
                                    SoundEffectConstants.CLICK
                                )
                                searchByNameValue.value = ""
                            },
                            modifier = Modifier.offset(15.dp, 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(id = R.string.delete_field)
                            )
                        }
                    }

                    IconButton(onClick = {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = stringResource(id = R.string.search_member_by_name)
                        )
                    }

                }

            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
        )
    }

    if (currentMembers.isEmpty()) {
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = stringResource(R.string.any_member_with_this_name)
        )
    }

    currentMembers.forEach { member ->
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
                taskVm = null,
                memberInfoTeam = targetMember,
                isDeleteActive = targetMember.profile.id != memberAccessedValue.id,  // it is not possible to delete myself from my team
                onRoleChange = { teamVm.changeRole(targetMember, it) },
                onTimeParticipationChange = { teamVm.changeTimePartecipation(targetMember, it) },
                onDelete = { teamVm.deleteMemberTeam(targetMember) },
                isMemberTeamSettingsPage = true,
                routerActions = routerActions,
                teamId = teamVm.teamField.id
            )
        }
    }
}

@Composable
fun ActionsOnTeamDropdownMenu(
    menuOpened: Boolean,
    onMenuClosed: () -> Unit,
    teamVm: TeamViewModel,
    routerActions: RouterActions
) {
    val view = LocalView.current
    val mediaPlayerDialog: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.dialog)

    Box(
        modifier = Modifier
            .offset(x = (-48).dp)
            .padding(start = 4.dp)
            .width(IntrinsicSize.Min)
    ) {
        DropdownMenu(
            expanded = menuOpened,
            onDismissRequest = { onMenuClosed() },
            modifier = Modifier.padding(4.dp)
        ) {
            DropdownMenuItem(
                onClick = {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    routerActions.navigateToLeaveTeam(teamVm.teamField.id)
                    onMenuClosed()
                }
            ) {
                Text(
                    text = stringResource(R.string.leave_team),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (teamVm.canEditBy()) {
                DropdownMenuItem(
                    onClick = {
                        Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                        Utils.useSafeMediaPlayer(mediaPlayerDialog)
                        routerActions.navigateToDeleteTeam(teamVm.teamField.id)
                        onMenuClosed()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete_team),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}