package it.polito.students.showteamdetails.view

import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.HorizontalLine
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.viewmodel.ProfileFormViewModel
import java.util.Locale


@Composable
fun AvatarEdit(nameParam: String, surnameParam: String?) {
    val name = nameParam.getOrElse(0) { ' ' }.uppercase().trim()
    val surname = (surnameParam ?: "").getOrElse(0) { ' ' }.uppercase().trim()

    Text(
        text = "$name$surname",
        fontSize = 55.sp,
        textAlign = TextAlign.Center,
        color = Color(0x88A6A6A6)
    )
}

@Composable
fun PersonalInfoFormSection(vm: ProfileFormViewModel) {
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
            Text(
                text = stringResource(id = R.string.personal_information),
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = colorResource(R.color.darkGray)
            )
            Spacer(modifier = Modifier.height(10.dp))
            NameEditForm(vm)
            SurnameEditForm(vm)
            AddressEditForm(vm)
            PhoneNumberEditForm(vm, true)
            BirthdateToggleFormSection(vm)
            GenderToggleFormSection(vm)
            Spacer(modifier = Modifier.height(30.dp))
        }

    }
}

@Composable
fun BiographyFormSection(vm: ProfileFormViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.biography),
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = colorResource(R.color.darkGray)
            )
            Spacer(modifier = Modifier.height(3.dp))
            BiographyEditForm(vm)
            HorizontalLine()
            Spacer(modifier = Modifier.height(3.dp))
            TechnicalSkillsEditForm(vm)
            LanguageSkillsEditForm(vm)
        }

    }
}

@Composable
fun AccountFormSection(vm: ProfileFormViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.account),
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = colorResource(R.color.darkGray)
            )
            Spacer(modifier = Modifier.height(10.dp))
            EmailEditForm(vm)

        }

    }
}

@Composable
fun NameEditForm(vm: ProfileFormViewModel) {
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
                value = vm.nameField.editValue,
                onValueChange = { vm.nameField.editValue = it },
                label = {
                    Text(
                        stringResource(
                            id = R.string.name
                        ),
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                isError = vm.nameField.error != -1,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 30.sp
                )
            )

            if (vm.nameField.error != -1)
                Text(
                    text = stringResource(vm.nameField.error),
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
fun SurnameEditForm(vm: ProfileFormViewModel) {
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
                value = vm.surnameField.editValue,
                onValueChange = { vm.surnameField.editValue = it },
                label = {
                    Text(
                        stringResource(id = R.string.surname),
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                isError = vm.surnameField.error != -1,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 30.sp
                )
            )
            if (vm.surnameField.error != -1)
                Text(
                    text = stringResource(vm.surnameField.error),
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
fun AddressEditForm(vm: ProfileFormViewModel) {
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
                value = vm.addressField.editValue,
                onValueChange = { vm.addressField.editValue = it },
                label = {
                    Text(
                        stringResource(id = R.string.address),
                        color = colorResource(R.color.darkGray),
                        fontSize = 16.sp
                    )
                },
                isError = vm.addressField.error != -1,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 30.sp
                )
            )
            if (vm.addressField.error != -1)
                Text(
                    text = stringResource(vm.addressField.error),
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
fun PhoneNumberEditForm(vm: ProfileFormViewModel, isProfile: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = if (isProfile) {
                        Modifier
                            .fillMaxWidth(0.6f)
                            .padding(0.dp, 5.dp)
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 5.dp)
                    },
                    value = vm.phoneNumberField.editValue,
                    onValueChange = { vm.phoneNumberField.editValue = it },
                    label = {
                        Text(
                            stringResource(id = R.string.telephone),
                            color = colorResource(R.color.darkGray),
                            fontSize = 16.sp
                        )
                    },
                    isError = vm.phoneNumberField.error != -1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        lineHeight = 30.sp
                    )
                )

                // Show the Surface with Switch only if isProfile is true
                if (isProfile) {
                    Surface(
                        modifier = Modifier.padding(start = 8.dp),
                        contentColor = Color.Black,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (!vm.phoneNumberField.editPrivate) stringResource(id = R.string.publicInfo)
                                else stringResource(id = R.string.privateInfo),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (!vm.phoneNumberField.editPrivate) colorResource(R.color.green).copy(
                                    green = 0.7f
                                ) else colorResource(R.color.darkGray),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Switch(
                                checked = !vm.phoneNumberField.editPrivate,
                                onCheckedChange = { vm.phoneNumberField.togglePrivate() },
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
            if (vm.phoneNumberField.error != -1) {
                Text(
                    text = stringResource(vm.phoneNumberField.error),
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


@Composable
fun BiographyEditForm(vm: ProfileFormViewModel) {
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
                value = vm.bioField.editValue,
                onValueChange = { vm.bioField.editValue = it },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(id = R.string.bio_title),
                            color = colorResource(R.color.darkGray),
                            fontSize = 16.sp
                        )
                    }
                },
                isError = vm.bioField.error != -1,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    lineHeight = 30.sp
                )

            )
            if (vm.bioField.error != -1)
                Text(
                    text = stringResource(vm.bioField.error),
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
fun TechnicalSkillsEditForm(vm: ProfileFormViewModel) {

    var newTechSkill by remember {
        mutableStateOf("")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp),
                text = stringResource(id = R.string.technical_skills_title),
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = colorResource(R.color.darkGray)
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 5.dp),
                value = newTechSkill,
                onValueChange = { newTechSkill = it },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(id = R.string.add_new_skill),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                },
                isError = vm.technicalSkillsField.error != -1,
                trailingIcon = {
                    if (newTechSkill.isNotBlank())
                        IconButton(onClick = {
                            vm.technicalSkillsField.editValue.add(newTechSkill)
                            newTechSkill = ""
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.AddCircle,
                                contentDescription = stringResource(id = R.string.add_new_skill)
                            )
                        }
                }
            )
            if (vm.technicalSkillsField.error != -1)
                Text(
                    text = stringResource(vm.technicalSkillsField.error),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(5.dp, 3.dp),
                    color = colorResource(R.color.redError)
                )
            vm.technicalSkillsField.editValue.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = colorResource(R.color.darkGray).copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = it,
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .fillMaxWidth(0.8f),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    IconButton(
                        onClick = { vm.technicalSkillsField.removeEditSkill(it) },
                        modifier = Modifier
                            .height(24.dp)

                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete technical skill",
                            tint = Color.Red
                        )
                    }

                }

                Spacer(modifier = Modifier.height(3.dp))

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSkillsEditForm(vm: ProfileFormViewModel) {
    val view = LocalView.current
    var uniqueLocales by remember { mutableStateOf(Utils.getUniqueLocales(vm)) }
    var isExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(isExpanded) {
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp),
            text = stringResource(id = R.string.languages_title),
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
            color = colorResource(R.color.darkGray)
        )

        Spacer(modifier = Modifier.height(10.dp))

        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = isExpanded,
            onExpandedChange = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                isExpanded = it
            }) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = stringResource(id = R.string.select_language),
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
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                uniqueLocales.forEach { language ->
                    androidx.compose.material.DropdownMenuItem(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            vm.languageSkillsField.addEditVal(language, 1)
                            uniqueLocales = Utils.getUniqueLocales(vm)

                            isExpanded = false
                        },
                    ) {
                        Text(text = language.displayLanguage)
                    }
                }
            }
        }
        if (vm.languageSkillsField.error != -1) {
            Text(
                text = stringResource(vm.languageSkillsField.error),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(5.dp, 3.dp),
                color = colorResource(R.color.redError)
            )
        }

        var levelExpanded by remember { mutableStateOf(false) }
        var languageLevelSelected by remember { mutableStateOf(Locale.getDefault()) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            vm.languageSkillsField.editValue.forEach { (language, level) ->
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .border(
                            width = 1.dp,
                            color = colorResource(R.color.darkGray).copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nome della lingua allineato a sinistra
                    Text(
                        text = "${language.displayLanguage[0].uppercase()}${
                            language.displayLanguage.substring(
                                1
                            )
                        }",
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    Text(
                        text = when (level) {
                            1 -> stringResource(id = R.string.beginner_A1)
                            2 -> stringResource(id = R.string.elementary_A2)
                            3 -> stringResource(id = R.string.intermediate_B1)
                            4 -> stringResource(id = R.string.upper_intermediate_B2)
                            5 -> stringResource(id = R.string.advanced_C1)
                            6 -> stringResource(id = R.string.proficiency_C2)
                            else -> stringResource(id = R.string.select_level)
                        },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable {
                                languageLevelSelected = language
                                levelExpanded = true
                            }
                            .background(
                                colorResource(R.color.secondary_color).copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                colorResource(R.color.blue),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Black
                    )


                    IconButton(
                        onClick = {
                            vm.languageSkillsField.removeEditVal(language, level)
                            uniqueLocales = Utils.getUniqueLocales(vm)
                        },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete language skill",
                            tint = Color.Red
                        )
                    }
                }

                DropdownMenu(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .wrapContentHeight(),
                    expanded = levelExpanded,
                    onDismissRequest = { levelExpanded = false }
                ) {
                    listOf(1, 2, 3, 4, 5, 6).forEach { levelOption ->
                        DropdownMenuItem(
                            onClick = {
                                vm.languageSkillsField.addEditVal(
                                    languageLevelSelected,
                                    levelOption
                                )
                                levelExpanded = false
                            },
                            text = {
                                Text(
                                    text = when (levelOption) {
                                        1 -> stringResource(id = R.string.beginner_A1)
                                        2 -> stringResource(id = R.string.elementary_A2)
                                        3 -> stringResource(id = R.string.intermediate_B1)
                                        4 -> stringResource(id = R.string.upper_intermediate_B2)
                                        5 -> stringResource(id = R.string.advanced_C1)
                                        6 -> stringResource(id = R.string.proficiency_C2)
                                        else -> ""
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmailEditForm(vm: ProfileFormViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(0.dp, 5.dp),
                    value = vm.emailField.editValue,
                    onValueChange = { vm.emailField.editValue = it },
                    label = {
                        Text(
                            stringResource(id = R.string.mail),
                            fontSize = 16.sp
                        )
                    },
                    isError = vm.emailField.error != -1,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 30.sp
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                Surface(
                    modifier = Modifier.padding(start = 8.dp),
                    contentColor = Color.Black,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text =
                            if (!vm.emailField.editPrivate) stringResource(id = R.string.publicInfo)
                            else stringResource(id = R.string.privateInfo),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (!vm.emailField.editPrivate) colorResource(R.color.green).copy(
                                green = 0.7f
                            ) else colorResource(R.color.darkGray),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Switch(
                            checked = !vm.emailField.editPrivate,
                            onCheckedChange = { vm.emailField.togglePrivate() },
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

            if (vm.emailField.error != -1)
                Text(
                    text = stringResource(vm.emailField.error),
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
fun OnlineStatusFormSection(vm: ProfileFormViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(156.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.status),
            modifier = Modifier.offset(5.dp, 0.dp),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )

        Surface(
            modifier = Modifier.padding(start = 8.dp),
            contentColor = Color.Black,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text =
                    if (vm.onlineSwitchField.editValue) stringResource(id = R.string.online)
                    else stringResource(id = R.string.offline),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (vm.onlineSwitchField.editValue) colorResource(R.color.green).copy(
                        green = 0.7f
                    ) else colorResource(R.color.darkGray),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = vm.onlineSwitchField.editValue,
                    onCheckedChange = { vm.onlineSwitchField.toggleVal() },
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

@Composable
fun BirthdateToggleFormSection(vm: ProfileFormViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = (stringResource(id = R.string.birthday) + ": " + vm.birthdateField.toString()),
            modifier = Modifier.offset(5.dp, 0.dp),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )

        Surface(
            modifier = Modifier.padding(start = 8.dp),
            contentColor = Color.Black,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text =
                    if (!vm.birthdateField.editPrivate) stringResource(id = R.string.publicInfo)
                    else stringResource(id = R.string.privateInfo),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (!vm.birthdateField.editPrivate) colorResource(R.color.green).copy(
                        green = 0.7f
                    ) else colorResource(R.color.darkGray),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = !vm.birthdateField.editPrivate,
                    onCheckedChange = { vm.birthdateField.togglePrivate() },
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

@Composable
fun GenderToggleFormSection(vm: ProfileFormViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp)) {
                    append(stringResource(id = R.string.gender))
                    append(": ")
                    vm.genderField.value.let {
                        append(stringResource(id = Utils.convertGenderToString(it)))
                    }
                }
            },
            modifier = Modifier
                .weight(1f)
                .offset(5.dp, 0.dp),
        )

        Surface(
            modifier = Modifier.padding(start = 8.dp),
            contentColor = Color.Black,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text =
                    if (!vm.genderField.editPrivate) stringResource(id = R.string.publicInfo)
                    else stringResource(id = R.string.privateInfo),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (!vm.genderField.editPrivate) colorResource(R.color.green).copy(
                        green = 0.7f
                    ) else colorResource(R.color.darkGray),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = !vm.genderField.editPrivate,
                    onCheckedChange = { vm.genderField.togglePrivate() },
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
