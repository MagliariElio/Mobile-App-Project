package it.polito.students.showteamdetails.view

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.polito.students.showteamdetails.ButtonsProfileSection
import it.polito.students.showteamdetails.EditPhotoSection
import it.polito.students.showteamdetails.HorizontalLine
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.factories.UserModelFactory
import it.polito.students.showteamdetails.isLandscape
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.ProfileFormViewModel
import it.polito.students.showteamdetails.viewmodel.TeamListViewModel
import it.polito.students.showteamdetails.viewmodel.UserViewModel


@Composable
fun ProfileScreen(
    user: Member,
    routerActions: RouterActions,
    teamListVm: TeamListViewModel,
    resetUsers: (List<Member>) -> Unit,
    allUsers: List<Member>,
    userViewModel: UserViewModel = viewModel(factory = UserModelFactory(LocalContext.current)),

    ) {
    BackHandler(onBack = {
        if (userViewModel.imageViewModel?.isOpenedCamera == true) {
            userViewModel.imageViewModel.exitCameraMode()
        }
        routerActions.goBackUntilRouteDifferentFrom(
            setOf(
                Utils.RoutesEnum.PROFILE.name,
                "${Utils.RoutesEnum.PROFILE.name}/edit",
                "${Utils.RoutesEnum.MEMBER_PROFILE}/${user.id}"
            )
        )
    })
    ViewPane(user, teamListVm = teamListVm, routerActions, userViewModel, resetUsers, allUsers)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(
    profileFormViewModel: ProfileFormViewModel,
    scaffoldState: BottomSheetScaffoldState,
    routerActions: RouterActions,
    user: Member,
    teamListVm: TeamListViewModel
) {
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
                    .fillMaxSize()

            ) {
                item {
                    EditPhotoSection(
                        profileFormViewModel.imageField,
                        scaffoldState,
                        profileFormViewModel.nameField.editValue,
                        profileFormViewModel.surnameField.editValue,
                        canEditBy = true
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "@${profileFormViewModel.nicknameField.value}",
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp,
                        )
                    }
                    PersonalInfoFormSection(profileFormViewModel)
                    HorizontalLine()
                    BiographyFormSection(profileFormViewModel)
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalLine()
                    Spacer(modifier = Modifier.height(15.dp))
                    AccountFormSection(profileFormViewModel)
                    OnlineStatusFormSection(profileFormViewModel)
                    ButtonsProfileSection(
                        routerActions = routerActions,
                        profileFormVm = profileFormViewModel,
                        user = user,
                        isRegister = false,
                        teamListVm = teamListVm
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ViewPane(
    user: Member,
    teamListVm: TeamListViewModel,
    routerActions: RouterActions,
    userViewModel: UserViewModel,
    resetUsers: (List<Member>) -> Unit,
    allUsers: List<Member>
) {
    // Presentation view of the profile page

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
                    .fillMaxSize()
            ) {
                item {

                    if (!isLandscape()) {
                        //Portrait mode
                        PortraitView(
                            user,
                            teamListVm = teamListVm,
                            routerActions,
                            userViewModel,
                            resetUsers,
                            allUsers
                        )
                    } else {
                        //Landscape mode
                        LandscapeView(
                            user,
                            teamListVm = teamListVm,
                            routerActions,
                            userViewModel,
                            resetUsers,
                            allUsers
                        )
                    }
                }
            }
            //Possibility to write a new review
            if (user.name != Utils.memberAccessed.value.name ||
                user.surname != Utils.memberAccessed.value.surname
            ) {
                AddNewReview(user, userViewModel, resetUsers, allUsers)
            }
        }
    }
}

@Composable
fun AccountSettingsPage(profileFormVm: ProfileFormViewModel, routerActions: RouterActions) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        val paddingValue: Dp = if (this.maxWidth < this.maxHeight) {
            //Portrait mode
            25.dp
        } else {
            //Landscape mode
            100.dp
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
                    .fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = stringResource(R.string.account_settings).uppercase(),
                        color = Color.Gray,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = colorResource(R.color.secondary_color),
                                shape = RectangleShape
                            )
                            .padding(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.sound),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = profileFormVm.accountSettings.sound,
                            onCheckedChange = { profileFormVm.accountSettings.toggleSound() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color.Green.copy(green = 0.7f),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = colorResource(R.color.darkGray).copy(alpha = 0.5f)
                            )
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = colorResource(R.color.secondary_color),
                                shape = RectangleShape
                            )
                            .padding(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.vibration),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = profileFormVm.accountSettings.vibration,
                            onCheckedChange = { profileFormVm.accountSettings.toggleVibration() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color.Green.copy(green = 0.7f),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = colorResource(R.color.darkGray).copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }
    }
}


