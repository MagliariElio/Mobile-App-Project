package it.polito.students.showteamdetails.view

import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.MemberInfoTeam
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.TeamListViewModel
import it.polito.students.showteamdetails.viewmodel.TeamViewModel

@Composable
fun AfterQRScanning(teamVm: TeamViewModel, routerActions: RouterActions) {
    val mediaPlayerDialog: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.dialog)
    val mediaPlayerSave: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.save)

    val isLoggedInMemberInTeam =
        teamVm.teamField.membersField.members.any { it.profile.id == Utils.memberAccessed.value.id }
    val cancelText = if (isLoggedInMemberInTeam) R.string.go_back else R.string.cancel
    val messageText = if (isLoggedInMemberInTeam) R.string.user_member else R.string.join_team

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
                    Spacer(modifier = Modifier.height(10.dp))
                    TeamDetails(teamVm)
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = stringResource(messageText),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = if (isLoggedInMemberInTeam) colorResource(R.color.redError) else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    //Cancel and Join buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                Utils.useSafeMediaPlayer(mediaPlayerDialog)
                                routerActions.navigateToHomeTeam()
                            },
                            border = BorderStroke(1.dp, Color.Black),
                            modifier = Modifier.padding(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                colorResource(R.color.white),
                                colorResource(R.color.black)
                            )
                        ) {
                            Text(text = stringResource(cancelText))
                        }

                        if (!isLoggedInMemberInTeam) {
                            Button(
                                onClick = {
                                    Utils.useSafeMediaPlayer(mediaPlayerSave)
                                    routerActions.navigateToRoleAssignationTeam(
                                        Utils.memberAccessed.value.id,
                                        teamVm.teamField.id
                                    )
                                },
                                border = BorderStroke(1.dp, colorResource(R.color.black)),
                                modifier = Modifier.padding(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    colorResource(R.color.primary_color),
                                    colorResource(R.color.white)
                                )
                            ) {
                                Text(text = stringResource(id = R.string.join))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaveTeam(
    teamListVm: TeamListViewModel,
    teamVm: TeamViewModel,
    isDeleteTeamPage: Boolean,
    routerActions: RouterActions
) {

    val mediaPlayerDialog: MediaPlayer? =
        Utils.loadMediaPlayer(LocalContext.current, R.raw.dialog)
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
                    Spacer(modifier = Modifier.height(30.dp))
                    TeamDetails(teamVm)
                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = stringResource(if (!isDeleteTeamPage) R.string.leave_team_confirmation else R.string.delete_team_confirm),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = buildAnnotatedString {
                            if (isDeleteTeamPage) {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(stringResource(R.string.delete_team_warning))
                                }
                                withStyle(style = SpanStyle(color = Color.DarkGray)) {
                                    append(stringResource(R.string.delete_team_warning_detail))
                                }
                            } else {
                                append("")
                            }
                        },
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    //Cancel and Leave buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                Utils.useSafeMediaPlayer(mediaPlayerDialog)
                                routerActions.goBack()
                            },
                            border = BorderStroke(1.dp, Color.Black),
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
                                Utils.useSafeMediaPlayer(mediaPlayerSave)

                                if (!isDeleteTeamPage) {
                                    val loggedInUser = Utils.profileMemberAccessed.value
                                    val memberToDelete =
                                        teamVm.teamField.membersField.members.find { member ->
                                            member.profile.id == loggedInUser.id
                                        }
                                    memberToDelete?.let {
                                        teamListVm.leaveMemberByTeam(it, teamVm)
                                    }
                                } else {
                                    teamListVm.deleteTeam(teamVm)
                                }

                                routerActions.navigateToHomeTeam()
                            },
                            border = BorderStroke(1.dp, colorResource(R.color.black)),
                            modifier = Modifier.padding(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                colorResource(R.color.redError),
                                colorResource(R.color.white)
                            )
                        ) {
                            Text(text = stringResource(if (!isDeleteTeamPage) R.string.leave else R.string.delete))
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RemoveMember(
    member: MemberInfoTeam,
    teamVm: TeamViewModel,
    routerActions: RouterActions
) {
    val mediaPlayerDialog: MediaPlayer? =
        Utils.loadMediaPlayer(LocalContext.current, R.raw.dialog)
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
                    MemberDetails(member = member.profile)
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(Utils.convertRoleToString(member.role)),
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 19.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    Text(
                        text = stringResource(id = R.string.remove_member),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(50.dp))

                    //Cancel and Remove buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                Utils.useSafeMediaPlayer(mediaPlayerDialog)
                                routerActions.navigateToEditTeam(teamVm.teamField.id)
                            },
                            border = BorderStroke(1.dp, Color.Black),
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
                                Utils.useSafeMediaPlayer(mediaPlayerSave)
                                teamVm.deleteMemberTeam(member)
                                routerActions.navigateToEditTeam(teamVm.teamField.id)
                            },
                            border = BorderStroke(1.dp, colorResource(R.color.black)),
                            modifier = Modifier.padding(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                colorResource(R.color.redError),
                                colorResource(R.color.white)
                            )
                        ) {
                            Text(text = stringResource(id = R.string.remove_photo))
                        }
                    }

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}