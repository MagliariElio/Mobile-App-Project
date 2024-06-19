package it.polito.students.showteamdetails.view

import android.media.MediaPlayer
import android.view.SoundEffectConstants
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.AlertDialogComponent
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.SemiBoldText
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.Review
import it.polito.students.showteamdetails.isLandscape
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.TeamListViewModel
import it.polito.students.showteamdetails.viewmodel.UserViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PortraitView(
    user: Member,
    teamListVm: TeamListViewModel,
    routerActions: RouterActions,
    userViewModel: UserViewModel,
    resetUsers: (List<Member>) -> Unit,
    allUsers: List<Member>
) {
    ProfilePicturePortrait(user)
    Spacer(modifier = Modifier.height(10.dp))
    NameAndNick(user)
    PersonalInfoAndBio(user)
    TeamsSkillsReviews(
        user,
        teamListVm = teamListVm,
        routerActions,
        userViewModel,
        resetUsers,
        allUsers
    )
}

@Composable
fun LandscapeView(
    user: Member,
    teamListVm: TeamListViewModel,
    routerActions: RouterActions,
    userViewModel: UserViewModel,
    resetUsers: (List<Member>) -> Unit,
    allUsers: List<Member>
) {
    Spacer(modifier = Modifier.height(40.dp))
    ProfilePictureLandscape(user)
    Spacer(modifier = Modifier.height(30.dp))
    BioText(user)
    TeamsSkillsReviews(
        user,
        teamListVm = teamListVm,
        routerActions,
        userViewModel,
        resetUsers,
        allUsers
    )
}

@Composable
fun AvatarView(user: Member) {
    Text(
        text = user.name[0].uppercaseChar() + "" + user.surname[0].uppercaseChar(),
        fontSize = 55.sp,
        textAlign = TextAlign.Center,
        color = Color.Black
    )
}

@Composable
fun ImageView(user: Member) {
    // Visualization of the image
    Image(
        bitmap = user.photo!!,
        contentDescription = stringResource(id = R.string.image_profile),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(200.dp)
            .border(
                BorderStroke(2.dp, Color.Black),
                CircleShape
            )
            .padding(2.dp)
            .clip(CircleShape)
    )
}

@Composable
fun ProfilePicturePortrait(user: Member) {
    val greenBackground = colorResource(R.color.green)
    val darkGrayBackground = colorResource(R.color.darkGray)
    val grayBackground = colorResource(R.color.gray)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(156.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(grayBackground, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (user.photo == null) {
                AvatarView(user)
            } else {
                ImageView(user)
            }

            // Online/Offline status
            Box(
                modifier = Modifier
                    .padding(start = 20.dp, bottom = 2.dp)
                    .size(15.dp)
                    .clip(CircleShape)
                    .align(Alignment.BottomStart)
                    .background(color = if (user.online) greenBackground else darkGrayBackground)
            )
        }
    }
}

@Composable
fun NameAndNick(user: Member) {

    //Name and surname
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${user.name} ${user.surname}",
            fontWeight = FontWeight.SemiBold,
            fontSize = 25.sp,
        )
    }

    //Nickname
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "@${user.nickname}",
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
        )
    }
}

@Composable
fun PersonalInfoView(user: Member) {

    val mailIcon = painterResource(id = R.drawable.mail)
    val locationIcon = painterResource(id = R.drawable.location)
    val cakeIcon = painterResource(id = R.drawable.cake)
    val userIcon = painterResource(id = R.drawable.user)
    val telephoneIcon = painterResource(id = R.drawable.telephone)
    val fontSize = 18.sp
    val iconSize = Modifier.size(23.dp)

    //Mail
    if (!user.privateEmail) { //Se non è privato viene mostrato
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = mailIcon,
                contentDescription = stringResource(id = R.string.mail),
                modifier = iconSize,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.email,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Normal,
                fontSize = fontSize,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }

    //Location
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            painter = locationIcon,
            contentDescription = stringResource(id = R.string.location),
            modifier = iconSize
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = user.address,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Normal,
            fontSize = fontSize,
        )
    }
    Spacer(modifier = Modifier.height(10.dp))

    // Birthday and Gender
    if (!user.privateBirthdate && !user.privateGender) { //Viene mostrata tutta la riga se entrambi non sono privati
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                painter = cakeIcon,
                contentDescription = stringResource(id = R.string.birthday),
                modifier = iconSize
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.birthdate.toString(),
                fontWeight = FontWeight.Normal,
                fontSize = fontSize,
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = userIcon,
                contentDescription = stringResource(id = R.string.gender),
                modifier = iconSize
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(
                    id = Utils.convertGenderToString(
                        user.gender
                    )
                ),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Normal,
                fontSize = fontSize,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

    } else if (!user.privateBirthdate && user.privateGender) { //viene mostrato solo birthday se gender è privato
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                painter = cakeIcon,
                contentDescription = stringResource(id = R.string.birthday),
                modifier = iconSize
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.birthdate.toString(),
                fontWeight = FontWeight.Normal,
                fontSize = fontSize,
            )
            Spacer(modifier = Modifier.weight(3f))
        }
        Spacer(modifier = Modifier.height(10.dp))
    } else if (user.privateBirthdate && !user.privateGender) { //viene mostrato solo gender se birthday è privato
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                painter = userIcon,
                contentDescription = stringResource(id = R.string.gender),
                modifier = iconSize
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.gender.toString(),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Normal,
                fontSize = fontSize,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }

    //Telephone
    if (!user.privatePhone) { //Se non è privato viene mostrato
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = telephoneIcon,
                contentDescription = stringResource(id = R.string.telephone),
                modifier = iconSize
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.phoneNumber,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Normal,
                fontSize = fontSize,
            )
        }
    }
}

@Composable
fun PersonalInfoLandscapeView(user: Member) {
    val mailIcon = painterResource(id = R.drawable.mail)
    val locationIcon = painterResource(id = R.drawable.location)
    val cakeIcon = painterResource(id = R.drawable.cake)
    val userIcon = painterResource(id = R.drawable.user)
    val telephoneIcon = painterResource(id = R.drawable.telephone)

    //Name and surname
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "${user.name} ${user.surname}",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        )
    }
    Spacer(modifier = Modifier.height(2.dp))

    //Nickname and gender
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "@${user.nickname}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        if (!user.privateGender) {
            Icon(
                painter = userIcon,
                contentDescription = stringResource(id = R.string.gender),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(
                    id = Utils.convertGenderToString(user.gender)
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))


    //Mail and birthday
    if (!user.privateEmail && !user.privateBirthdate) { //Viene mostrata tutta la riga se entrambi non sono privati
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = mailIcon,
                contentDescription = stringResource(id = R.string.mail),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))

            //Birthday
            Icon(
                painter = cakeIcon,
                contentDescription = stringResource(id = R.string.birthday),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.birthdate.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    } else if (!user.privateEmail && user.privateBirthdate) { //Viene mostrata solo la mail
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = mailIcon,
                contentDescription = stringResource(id = R.string.mail),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(10.dp))
    } else if (user.privateEmail && !user.privateBirthdate) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            //Birthday
            Icon(
                painter = cakeIcon,
                contentDescription = stringResource(id = R.string.birthday),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.birthdate.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }

    //Location
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            painter = locationIcon,
            contentDescription = stringResource(id = R.string.location),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = user.address,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        //Telephone
        if (!user.privatePhone) { //Se non è privato viene mostrato
            Icon(
                painter = telephoneIcon,
                contentDescription = stringResource(id = R.string.telephone),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.phoneNumber,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))

}

@Composable
fun BioText(user: Member) {

    val darkGrayBackground = colorResource(R.color.darkGray)

    OutlinedTextField(
        value = user.bio,
        label = {
            Text(
                stringResource(
                    id = R.string.bio_title
                ),
                color = darkGrayBackground,
                fontSize = 16.sp
            )
        },
        onValueChange = {},
        textStyle = TextStyle(
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 30.sp
        ),
        modifier = Modifier.fillMaxWidth(),
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = darkGrayBackground, // Change the color of the border when the field is focused
            unfocusedBorderColor = darkGrayBackground,
        )
    )
}

@Composable
fun TeamView(teamListVm: TeamListViewModel, user: Member, routerActions: RouterActions) {
    val grayBackground = colorResource(R.color.gray)

    val currentTeams by teamListVm.teamList.collectAsState()
    val memberTeams = currentTeams.filter { teamField ->
        teamField.teamField.membersField.members.any { memberInfo ->
            memberInfo.profile == user
        }
    }

    Column(
        modifier = Modifier
            .border(
                width = 2.dp,
                color = grayBackground,
                shape = RoundedCornerShape(8.dp)
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                fontWeight = FontWeight.Light,
                text = stringResource(id = R.string.number_teams_member, memberTeams.size),
                fontSize = 18.sp,
                color = Color.Black
            )
        }

        memberTeams.forEach { teamField ->
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                teamField.teamField.picture.let {
                    TeamRow(
                        teamField.teamField.id,
                        teamField.teamField.nameField.value,
                        teamField.numberCompletedTask,
                        teamField.numberAssignedTask,
                        teamField.grade,
                        teamField.teamField.picture.bitmapView.value,
                        routerActions
                    )
                }
            }
        }
    }
}

@Composable
fun TeamRow(
    teamId: String,
    nameTeam: String,
    numCompletedTasks: Int,
    numAssignedTasks: Int,
    grade: Int,
    picture: ImageBitmap? = null,
    routerActions: RouterActions
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

    val imagePicture = painterResource(R.drawable.company)

    var backgroundGrade = greenBackground
    if (grade < 8 && grade >= 5) {
        backgroundGrade = yellowBackground
    } else if (grade < 5) {
        backgroundGrade = redBackground
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { routerActions.navigateToEditTeam(teamId) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(grayBackground, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (picture != null) {
                Image(
                    bitmap = picture,
                    contentDescription = stringResource(id = R.string.team_picture_profile),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(45.dp)
                        .border(
                            BorderStroke(0.5.dp, Color.Black),
                            CircleShape
                        )
                        .padding(2.dp)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = imagePicture,
                    contentDescription = stringResource(id = R.string.team_picture_profile),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(45.dp)
                        .border(
                            BorderStroke(0.5.dp, Color.Black),
                            CircleShape
                        )
                        .padding(2.dp)
                        .clip(CircleShape)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
        ) {
            Text(
                text = nameTeam,
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
                        numCompletedTasks,
                        numAssignedTasks
                    ),
                    fontSize = 10.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Light
                )
            }
        }

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                ) {
                    append(grade.toString())
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

@Composable
fun SkillsViewPortrait(user: Member) {
    val grayBackground = colorResource(R.color.gray)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = grayBackground,
                shape = RoundedCornerShape(8.dp)
            ),
    ) {
        TechnicalSkillsSection(user)

        Divider(
            color = grayBackground,
            thickness = 2.dp,
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .width(200.dp)
                .align(Alignment.CenterHorizontally)
        )

        LanguageSkillSection(user)
    }
}

@Composable
fun SkillsViewLandscape(user: Member) {
    val grayBackground = colorResource(R.color.gray)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = grayBackground,
                shape = RoundedCornerShape(8.dp)
            ),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 10.dp, top = 10.dp, start = 10.dp)
        ) {
            TechnicalSkillsSection(user)
        }

        Column {
            Divider(
                color = grayBackground,
                thickness = 2.dp,
                modifier = Modifier
                    .padding(top = 50.dp)
                    .width(2.dp)
                    .height(200.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp, top = 10.dp, start = 10.dp)
        ) {
            LanguageSkillSection(user)
        }
    }
}

@Composable
fun TechnicalSkillsSection(user: Member) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp, start = 10.dp)
    ) {
        Text(
            text = stringResource(id = R.string.technical_skills_title),
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )
    }

    for (skill in user.technicalSkills) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .padding(start = 6.dp)
        ) {
            Text(
                text = "•  $skill",
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun LanguageSkillSection(user: Member) {
    val blueBackground = colorResource(R.color.blue)

    val languageTypeList = listOf("A1", "A2", "B1", "B2", "C1", "C2")

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.languages_title),
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        if (user.languages.isNotEmpty()) {
            languageTypeList.forEach {
                Text(
                    text = it,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 2.dp),
                )
            }
        }
    }

    if (user.languages.isEmpty()) {
        Text(
            modifier = Modifier
                .padding(10.dp)
                .then(Modifier.padding(start = 10.dp)),
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                ) {
                    append(stringResource(id = R.string.empty_list))
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 19.sp)) {
                    append("\n ${stringResource(id = R.string.go_to_fill_edit_page)}")
                }
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    } else {
        for (language in user.languages) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${language.first.displayLanguage[0].uppercase()}${
                        language.first.displayLanguage.substring(
                            1
                        )
                    }",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )

                val numberCircle = language.second
                val sizeCircle = 20.dp
                for (i in 0..<numberCircle) {
                    BoxCircleWithBorder(blueBackground, sizeCircle)
                }

                for (i in numberCircle..<6) {
                    BoxCircleWithBorder(Color.White, sizeCircle)
                }
            }
        }
    }
}

@Composable
fun BoxCircleWithBorder(color: Color, sizeCircle: Dp) {
    Box(
        modifier = Modifier
            .padding(start = 3.dp)
            .size(sizeCircle)
            .background(color = Color.Black, shape = CircleShape)
    ) {
        Box(
            modifier = Modifier
                .size(sizeCircle - 2.dp)
                .background(color, shape = CircleShape)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun Reviews(
    user: Member,
    userViewModel: UserViewModel,
    resetUsers: (List<Member>) -> Unit,
    allUsers: List<Member>
) {

    if (user.reviews.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                stringResource(id = R.string.no_reviews),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    val reviewSorted = user.reviews.sortedByDescending { review: Review -> review.dateCreation }



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 220.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        reviewSorted.forEach { review ->
            var iCreate = false
            if (Utils.memberAccessed.value.id == review.createdBy.id) {
                iCreate = true
            }
            ReviewCard(
                user = user,
                review = review,
                createdByMe = iCreate,
                userViewModel = userViewModel,
                resetUsers,
                allUsers
            )
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewReview(
    user: Member,
    userViewModel: UserViewModel,
    resetUsers: (List<Member>) -> Unit,
    allUsers: List<Member>
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val mediaPlayerSave: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.save)



    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp)
        ) {
            TextField(
                value = userViewModel.editReview,
                onValueChange = { userViewModel.editReview = it },
                placeholder = { Text(stringResource(id = R.string.add_review)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    Utils.useSafeMediaPlayer(mediaPlayerSave)

                    // Creates a new review
                    val newReview = Review(
                        message = userViewModel.editReview,
                        createdBy = Utils.memberAccessed.value,
                        dateCreation = LocalDateTime.now(),
                        dateModification = null
                    )
                    userViewModel.addReview(user, newReview, resetUsers, allUsers)
                    userViewModel.editReview = ""
                    keyboardController?.hide()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = if (userViewModel.errorNewReview != -1) colorResource(R.color.redError) else Color.Gray, // Colorazione del bordo in base all'errore
                        shape = RoundedCornerShape(8.dp)
                    ),
                isError = userViewModel.errorNewReview != -1,
                colors = TextFieldDefaults.textFieldColors(
                    disabledTextColor = Color.Gray,
                    errorCursorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
            )

            if (userViewModel.errorNewReview != -1) {
                Text(
                    text = stringResource(userViewModel.errorNewReview),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(16.dp, 1.dp),
                    color = colorResource(R.color.redError)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(40.dp))
}

@Composable
fun ReviewCard(
    user: Member,
    review: Review,
    createdByMe: Boolean,
    userViewModel: UserViewModel,
    resetUsers: (List<Member>) -> Unit,
    allUsers: List<Member>
) {

    val view = LocalView.current
    val context = LocalContext.current
    val mediaPlayerSave: MediaPlayer? = Utils.loadMediaPlayer(context, R.raw.save)
    val mediaPlayerDialog: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.dialog)

    var showAll by remember { mutableStateOf(false) }
    var delete by remember { mutableStateOf(false) }
    val maxLengthMessage = 70 // Comment showed only until 50 characters
    var modifyReview by remember { mutableStateOf(false) }
    fun setModifyComment() {
        modifyReview = !modifyReview
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp, 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(id = R.string.avatar),
                        modifier = Modifier
                            .padding(2.dp, 0.dp)
                            .size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))

                    Column {
                        Text(
                            text = stringResource(id = R.string.anonymous_user),
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp
                        )
                        // Date and time
                        val date = review.dateCreation.toLocalDate()
                            .format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                        val time = review.dateCreation.toLocalTime()
                            .format(DateTimeFormatter.ofPattern("HH:mm"))
                        Row {
                            Text(
                                text = "$date, $time",
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                if (createdByMe) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(x = 18.dp, y = (-4).dp)
                        ) {
                            IconButton(
                                onClick = {
                                    modifyReview = true
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.edit_icon),
                                    contentDescription = stringResource(id = R.string.edit_icon),
                                    modifier = Modifier.size(17.dp),
                                    tint = Color.Unspecified
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .offset(x = 0.dp, y = (-4).dp)
                        ) {
                            IconButton(
                                onClick = {
                                    // Sound and vibration
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
            }

            if (!modifyReview) {
                val messageToShow = review.message

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
                            showAll = !showAll // Added animation
                        },
                    maxLines = if (!showAll) 2 else 30, // Lines to show
                )

                // Comment showed only until 50 characters
                if (!showAll && review.message.length > maxLengthMessage) {
                    Text(
                        text = stringResource(id = R.string.see_more),
                        color = colorResource(R.color.darkGray),
                        modifier = Modifier
                            .padding(5.dp, 5.dp)
                            .clickable {
                                Utils.playSoundEffectWithSoundCheck(
                                    view,
                                    SoundEffectConstants.CLICK
                                )
                                showAll = true
                            }
                    )
                }
            }

            if (delete) {
                val textMessageToDelete = if (review.message.length > maxLengthMessage)
                    review.message.substring(0, 42) + "..."
                else
                    review.message
                AlertDialogComponent(
                    textToDelete = textMessageToDelete,
                    textMessage = stringResource(R.string.are_you_sure_delete_message),
                    dismissButtonText = stringResource(R.string.cancel),
                    confirmButtonText = stringResource(R.string.delete_review),
                    onConfirmedRequest = {
                        Utils.useSafeMediaPlayer(mediaPlayerSave)
                        Utils.vibrate(context)

                        userViewModel.deleteReview(user, review, resetUsers, allUsers)
                        delete = false
                    }) {
                    delete = false
                }
            }

            if (modifyReview) {
                userViewModel.editReview = review.message
                ReviewModifyCard(
                    user,
                    review,
                    userViewModel,
                    { setModifyComment() },
                    resetUsers,
                    allUsers
                )
                showAll = false
            }
        }
    }
}


@Composable
fun ReviewModifyCard(
    user: Member,
    review: Review,
    userViewModel: UserViewModel,
    setModifyComment: () -> Unit,
    resetUsers: (List<Member>) -> Unit,
    allUsers: List<Member>
) {
    val mediaPlayerSave: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.save)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp, 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(5.dp))

                Row {
                    Column {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 5.dp),
                            value = userViewModel.editReview,
                            onValueChange = { userViewModel.editReview = it },
                            label = {
                                Text(
                                    stringResource(id = R.string.new_review),
                                    color = colorResource(R.color.darkGray),
                                    fontSize = 16.sp
                                )
                            },
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                                lineHeight = 30.sp
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxSize(1f)
                        ) {
                            IconButton(
                                onClick = {
                                    Utils.useSafeMediaPlayer(mediaPlayerSave)

                                    val modifiedDate = LocalDateTime.now()
                                    val modifiedReview = Review(
                                        message = userViewModel.editReview,
                                        createdBy = Utils.memberAccessed.value,
                                        dateCreation = review.dateCreation,
                                        dateModification = modifiedDate
                                    )
                                    userViewModel.addReview(user, review, resetUsers, allUsers)
                                    userViewModel.deleteReview(user, review, resetUsers, allUsers)
                                    userViewModel.editReview = ""
                                    setModifyComment()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_send_24),
                                    contentDescription = stringResource(id = R.string.new_review),
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Unspecified
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamsSkillsReviews(
    user: Member,
    teamListVm: TeamListViewModel,
    routerActions: RouterActions,
    userViewModel: UserViewModel,
    resetUsers: (List<Member>) -> Unit,
    allUsers: List<Member>
) {
    Spacer(modifier = Modifier.height(50.dp))


    SemiBoldText(stringResource(id = R.string.teams_title))
    Spacer(modifier = Modifier.height(5.dp))
    TeamView(teamListVm, user, routerActions)
    Spacer(modifier = Modifier.height(25.dp))

    SemiBoldText(stringResource(id = R.string.skills_title))
    Spacer(modifier = Modifier.height(5.dp))

    if (isLandscape()) {
        SkillsViewLandscape(user)
    } else {
        SkillsViewPortrait(user)
    }

    Spacer(modifier = Modifier.height(25.dp))
    SemiBoldText(stringResource(id = R.string.reviews_title))
    Spacer(modifier = Modifier.height(5.dp))
    Reviews(user, userViewModel, resetUsers, allUsers)
    Spacer(modifier = Modifier.height(80.dp))
}

@Composable
fun ProfilePictureLandscape(user: Member) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(colorResource(R.color.gray), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (user.photo == null) {
                AvatarView(user)
            } else {
                ImageView(user)
            }

            //Online/offline status
            Box(
                modifier = Modifier
                    .padding(start = 20.dp, bottom = 2.dp)
                    .size(15.dp)
                    .clip(CircleShape)
                    .align(Alignment.BottomStart)
                    .background(
                        color = if (user.online) colorResource(R.color.green) else colorResource(
                            R.color.darkGray
                        )
                    )
            )

        }

        Spacer(modifier = Modifier.width(20.dp))
        Column {
            PersonalInfoLandscapeView(user)
        }
    }
}

@Composable
fun PersonalInfoAndBio(user: Member) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(0.dp, 40.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            PersonalInfoView(user)
            Spacer(modifier = Modifier.height(20.dp))
            BioText(user)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}