package it.polito.students.showteamdetails.view

import android.media.MediaPlayer
import android.view.SoundEffectConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.AvatarView
import it.polito.students.showteamdetails.ImageView
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.SemiBoldText
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.MemberInfoTeam
import it.polito.students.showteamdetails.entity.RoleEnum
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.TeamListViewModel
import it.polito.students.showteamdetails.viewmodel.TeamViewModel

@Composable
fun Requests(teamVm: TeamViewModel, routerActions: RouterActions) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { routerActions.goBack() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_back_24),
                        contentDescription = stringResource(R.string.go_back),
                        modifier = Modifier.size(35.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            Text(
                text = stringResource(id = R.string.new_members_requests),
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        SearchBarAndRequests(teamVm = teamVm, routerActions)
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleAssignation(
    teamListVm: TeamListViewModel,
    member: Member,
    teamVm: TeamViewModel,
    routerActions: RouterActions,
) {

    val isMenuExpanded = remember { mutableStateOf(false) }
    val view = LocalView.current
    val mediaPlayerDialog: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.dialog)
    val mediaPlayerSave: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.save)

    val selectedRole = remember { mutableStateOf<RoleEnum?>(null) }

    BackHandler {
        if (teamVm.teamField.membersField.error != -1)
            teamVm.teamField.membersField.error = -1
        routerActions.goBack()
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxHeight()
    ) {
        val pageWidth: Float = if (this.maxWidth < this.maxHeight) {
            //Portrait mode
            this.maxWidth.value * .85f
        } else {
            //Landscape mode
            this.maxWidth.value * .8f
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(Dp(pageWidth)),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                    MemberDetails(member = member)
                    Spacer(modifier = Modifier.height(30.dp))

                    //Role
                    SemiBoldText(text = stringResource(id = R.string.assign_role))
                    Spacer(modifier = Modifier.height(20.dp))
                    Column {
                        ExposedDropdownMenuBox(
                            modifier = Modifier.fillMaxWidth(),
                            expanded = isMenuExpanded.value,
                            onExpandedChange = {
                                Utils.playSoundEffectWithSoundCheck(
                                    view,
                                    SoundEffectConstants.CLICK
                                )
                                isMenuExpanded.value = it
                            }) {

                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                value = selectedRole.value?.let { role ->
                                    stringResource(Utils.convertRoleToString(role))
                                } ?: stringResource(id = R.string.no_role_assigned),
                                onValueChange = {},
                                readOnly = true,
                                singleLine = true,
                                label = {
                                    Text(
                                        stringResource(
                                            id = R.string.role
                                        ),
                                        color = colorResource(R.color.darkGray),
                                        fontSize = 16.sp
                                    )
                                },
                                isError = teamVm.teamField.membersField.error != -1,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMenuExpanded.value) },
                            )
                            ExposedDropdownMenu(
                                expanded = isMenuExpanded.value,
                                onDismissRequest = { isMenuExpanded.value = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RoleEnum.entries.filter { it != RoleEnum.NO_ROLE_ASSIGNED }
                                    .map { role ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(stringResource(Utils.convertRoleToString(role)))
                                            },
                                            onClick = {
                                                teamVm.teamField.membersField.error = -1
                                                Utils.playSoundEffectWithSoundCheck(
                                                    view,
                                                    SoundEffectConstants.CLICK
                                                )
                                                selectedRole.value = role
                                                isMenuExpanded.value = false
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                        )
                                    }
                            }
                        }
                        if (teamVm.teamField.membersField.error != -1) {
                            Text(
                                text = stringResource(teamVm.teamField.membersField.error),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(5.dp, 3.dp),
                                color = colorResource(R.color.redError)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(50.dp))

                    //Back and Finish buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                Utils.useSafeMediaPlayer(mediaPlayerDialog)
                                if (teamVm.teamField.membersField.error != -1)
                                    teamVm.teamField.membersField.error = -1
                                routerActions.goBack()
                            },
                            border = BorderStroke(1.dp, Color(0xFF283D8F)),
                            modifier = Modifier.padding(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                colorResource(R.color.white),
                                colorResource(R.color.black)
                            )
                        ) {
                            Text(text = stringResource(id = R.string.back))
                        }

                        Button(
                            onClick = {
                                // Add new member
                                val roleValue = selectedRole.value ?: RoleEnum.NO_ROLE_ASSIGNED

                                val member = MemberInfoTeam(
                                    profile = member,
                                    role = roleValue,
                                    participationType = Utils.TimePartecipationTeamTypes.FULL_TIME
                                )

                                //If the member is added, delete request
                                if (teamVm.teamField.membersField.error == -1) {
                                    teamVm.addTeamMember(member)
                                    Utils.useSafeMediaPlayer(mediaPlayerSave)
                                    routerActions.navigateToTeam(teamVm.teamField.id)
                                } else {
                                    Utils.useSafeMediaPlayer(mediaPlayerDialog)
                                }
                            },
                            border = BorderStroke(1.dp, colorResource(R.color.black)),
                            modifier = Modifier.padding(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                colorResource(R.color.blue),
                                colorResource(R.color.white)
                            )
                        ) {
                            Text(text = stringResource(id = R.string.finish))
                        }
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}

@Composable
fun PreviewRequest(
    member: Pair<Member, RoleEnum>,
    teamVm: TeamViewModel,
    routerActions: RouterActions
) {

    val mediaPlayerDialog: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.dialog)
    val mediaPlayerSave: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.save)

    BoxWithConstraints(
        modifier = Modifier.fillMaxHeight()
    ) {
        val pageWidth: Float = if (this.maxWidth < this.maxHeight) {
            //Portrait mode
            this.maxWidth.value * .85f
        } else {
            //Landscape mode
            this.maxWidth.value * .8f
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(Dp(pageWidth)),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                    MemberDetails(member = member.first)
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "${member.first.name} ${member.first.surname} " +
                                stringResource(id = R.string.wants_join_team) + "\n" +
                                stringResource(id = R.string.do_you_agree),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(40.dp))

                    //Reject and Accept buttons
                    if (teamVm.canEditBy()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    Utils.useSafeMediaPlayer(mediaPlayerDialog)
                                    teamVm.deleteMemberRequest(member.first)    // cancella la richiesta al team
                                    routerActions.navigateToTeam(teamVm.teamField.id)
                                },
                                border = BorderStroke(1.dp, colorResource(R.color.black)),
                                modifier = Modifier.padding(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    colorResource(R.color.redError),
                                    colorResource(R.color.white)
                                )
                            ) {
                                Text(text = stringResource(id = R.string.reject))
                            }

                            Button(
                                onClick = {
                                    Utils.useSafeMediaPlayer(mediaPlayerSave)
                                    routerActions.navigateToRoleAssignationMember(
                                        member.first.id,
                                        teamVm.teamField.id
                                    )
                                },
                                border = BorderStroke(1.dp, colorResource(R.color.black)),
                                modifier = Modifier.padding(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    colorResource(R.color.blue),
                                    colorResource(R.color.white)
                                )
                            ) {
                                Text(text = stringResource(id = R.string.accept))
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.no_permissions),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(5.dp, 3.dp),
                            color = colorResource(R.color.redError)
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun MemberDetails(member: Member) {
    //Name and surname
    Text(
        text = "${member.name} ${member.surname}",
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp
    )

    //Show mail if not private
    if (!member.privateEmail) {
        Text(
            text = member.email,
            fontWeight = FontWeight.Light,
            fontSize = 18.sp
        )
    } else {
        Text(
            text = "-",
            fontWeight = FontWeight.Light,
            fontSize = 18.sp
        )
    }
    Spacer(modifier = Modifier.height(20.dp))

    //Profile photo
    if (member.photo != null) {
        ImageView(
            painterBitmap = member.photo,
            size = 200.dp
        )
    } else {
        AvatarView(name = member.name, surname = member.surname, 200)
    }
}

@Composable
fun TeamDetails(teamVm: TeamViewModel) {
    val imageSize = 200.dp
    val borderImage = BorderStroke(0.5.dp, Color.Black)
    val paddingSize = 2.dp

    //Name
    Text(
        text = teamVm.teamField.nameField.value,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(20.dp))

    if (teamVm.teamField.picture.bitmapView.value == null) {
        Image(
            painter = painterResource(R.drawable.icon_company_100),
            contentDescription = stringResource(id = R.string.team_picture_profile),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(imageSize)
                .border(
                    borderImage,
                    CircleShape
                )
                .padding(paddingSize)
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
                .padding(paddingSize)
                .clip(CircleShape)
        )
    }

    Spacer(modifier = Modifier.height(30.dp))

    Row(
        modifier = Modifier.padding(5.dp)
    ) {
        val members = teamVm.teamField.membersField.members
            .sortedByDescending { it.profile.id == Utils.memberAccessed.value.id }
            .take(minOf(3, teamVm.teamField.membersField.members.size))
            .toMutableList()

        Box(
            modifier = Modifier.offset((20 * (members.size - 1)).dp, 0.dp),
            contentAlignment = Alignment.CenterEnd
        ) {

            members.reversed().forEachIndexed { index, member ->
                Box(
                    modifier = Modifier
                        .offset((-20 * index).dp, 0.dp)
                        .border(
                            1.dp,
                            color = if (member.profile.id == Utils.memberAccessed.value.id) colorResource(
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
        }

        if (members.size < teamVm.teamField.membersField.members.size) {
            Text(
                text = "+ ${teamVm.teamField.membersField.members.size - members.size} ${
                    stringResource(
                        id = R.string.more
                    )
                }",
                modifier = Modifier.offset(45.dp, (5).dp),
                fontSize = TextUnit(10f, TextUnitType.Sp)
            )
        }
    }
}


@Composable
fun SearchBarAndRequests(teamVm: TeamViewModel, routerActions: RouterActions) {
    val view = LocalView.current
    val searchRequestByNameValue = remember { mutableStateOf("") }
    val currentRequests = teamVm.teamField.requestsTeamField.editRequests.filter {
        it.name.contains(
            searchRequestByNameValue.value,
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
            value = searchRequestByNameValue.value,
            onValueChange = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                searchRequestByNameValue.value = it
            },
            label = {
                Text(
                    stringResource(id = R.string.search),
                    fontSize = TextUnit(13f, TextUnitType.Sp)
                )
            },
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.End
                ) {
                    if (searchRequestByNameValue.value.isNotBlank()) {
                        IconButton(
                            onClick = {
                                Utils.playSoundEffectWithSoundCheck(
                                    view,
                                    SoundEffectConstants.CLICK
                                )
                                searchRequestByNameValue.value = ""
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
                            contentDescription = stringResource(id = R.string.search)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth(0.9f)
        )
    }

    if (currentRequests.isEmpty()) {
        Text(
            modifier = Modifier.padding(top = 40.dp),
            text = stringResource(R.string.no_requests)
        )
    }
    Spacer(modifier = Modifier.height(20.dp))

    currentRequests.forEach { member ->
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
            RequestsDetailRow(teamVm, targetMember, routerActions)
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
fun RequestsDetailRow(
    teamVm: TeamViewModel?,
    member: Member,
    routerActions: RouterActions
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp, 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { routerActions.navigateToMemberProfile(member.id) }) {
                if (member.photo != null) {
                    ImageView(
                        painterBitmap = member.photo,
                        size = 45.dp
                    )
                } else {
                    AvatarView(
                        name = member.name,
                        surname = member.surname,
                        45
                    )
                }

                Spacer(modifier = Modifier.width(7.dp))
                Column(modifier = Modifier.weight(1f)) {

                    // Name and Surname
                    Text(
                        text = "${member.name} ${member.surname}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp
                    )

                    // Mail
                    if (!member.privateEmail) {
                        Text(
                            text = member.email,
                            fontWeight = FontWeight.Light,
                            fontSize = 15.sp
                        )
                    } else {
                        Text(
                            text = "-",
                            fontWeight = FontWeight.Light,
                            fontSize = 15.sp
                        )
                    }
                }
                //Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        routerActions.navigateToMemberRequestPreview(
                            member.id,
                            teamVm!!.teamField.id
                        )
                    },
                    modifier = Modifier.padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        colorResource(R.color.blue),
                        colorResource(R.color.white)
                    ),
                ) {
                    Text(
                        text = stringResource(id = R.string.preview),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}