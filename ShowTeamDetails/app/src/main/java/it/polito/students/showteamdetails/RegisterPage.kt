package it.polito.students.showteamdetails

import android.view.SoundEffectConstants
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.view.AddressEditForm
import it.polito.students.showteamdetails.view.DatePickerDialog
import it.polito.students.showteamdetails.view.NameEditForm
import it.polito.students.showteamdetails.view.PhoneNumberEditForm
import it.polito.students.showteamdetails.view.SurnameEditForm
import it.polito.students.showteamdetails.view.isValidDateFormat
import it.polito.students.showteamdetails.viewmodel.ProfileFormViewModel
import it.polito.students.showteamdetails.viewmodel.TeamListViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@Composable
fun RegisterPage(
    routerActions: RouterActions,
    teamListVm: TeamListViewModel,
    userData: UserData?
) {

    val newMember = Member(
        id = "",
        photo = null, //Da account
        name = "",
        surname = "",
        email = userData?.email ?: "", //Da account
        online = true,
        password = "",
        gender = Utils.Gender.Male,
        birthdate = LocalDate.now(),
        nickname = "",
        bio = "",
        address = "",
        phoneNumber = "",
        technicalSkills = emptyList(),
        languages = emptyList(),
        reviews = emptyList(),
        privateBirthdate = false,
        privatePhone = false,
        privateEmail = false,
        privateGender = false,
        sound = false,
        vibration = false,
        language = Locale("ENGLISH")
    )

    val profileFormVm = ProfileFormViewModel(newMember)


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

                    Spacer(modifier = Modifier.height(40.dp))
                    SemiBoldText(text = stringResource(id = R.string.register))
                    Spacer(modifier = Modifier.height(20.dp))
                    NameEditForm(profileFormVm)
                    SurnameEditForm(profileFormVm)
                    NickNameForm(profileFormVm)
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalLine()
                    Spacer(modifier = Modifier.height(15.dp))
                    AddressEditForm(profileFormVm)
                    PhoneNumberEditForm(profileFormVm, false)
                    BirthdateField(
                        vm = profileFormVm,
                        labelDate = stringResource(R.string.birthday),
                        editDate = profileFormVm.birthdateField.editDate,
                        onDateChange = { selectedDate, _ ->
                            profileFormVm.birthdateField.editDate = selectedDate
                            profileFormVm.birthdateField.validateBirthday()
                        },
                        onErrorDate = profileFormVm.birthdateField.errorDate,
                    )
                    GenderForm(profileFormVm)
                    Spacer(modifier = Modifier.height(30.dp))
                    ButtonsProfileSection(
                        routerActions = routerActions,
                        profileFormVm = profileFormVm,
                        user = newMember,
                        isRegister = true,
                        teamListVm = teamListVm
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun NickNameForm(vm: ProfileFormViewModel) {
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
                value = vm.nicknameField.editValue,
                onValueChange = { vm.nicknameField.editValue = it },
                label = {
                    Text(
                        stringResource(
                            id = R.string.nickname
                        ),
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                isError = vm.nicknameField.error != -1,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 30.sp
                )
            )

            if (vm.nicknameField.error != -1)
                Text(
                    text = stringResource(vm.nicknameField.error),
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
fun BirthdateField(
    vm: ProfileFormViewModel,
    labelDate: String,
    editDate: LocalDate,
    onDateChange: (LocalDate, Boolean) -> Unit,
    onErrorDate: Int,
) {
    val isOpen = remember { mutableStateOf(false) }
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
                    vm.birthdateField.resetErrorBirthdayDate()

                    isOpen.value = false
                },
                onCancel = {
                    isOpen.value = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderForm(vm: ProfileFormViewModel) {
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
                value = stringResource(Utils.convertGenderToString(vm.genderField.editGender)),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = {
                    Text(
                        stringResource(
                            id = R.string.gender
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
                Utils.Gender.entries.map { gender ->
                    DropdownMenuItem(
                        text = { Text(stringResource(Utils.convertGenderToString(gender))) },
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            vm.genderField.editGender = gender
                            vm.genderField.error = -1
                            isMenuExpanded.value = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
        if (vm.genderField.error != -1) {
            Text(
                text = stringResource(vm.genderField.error),
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