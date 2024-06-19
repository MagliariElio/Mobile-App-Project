package it.polito.students.showteamdetails.view

import android.annotation.SuppressLint
import android.view.SoundEffectConstants
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import it.polito.students.showteamdetails.AvatarView
import it.polito.students.showteamdetails.HorizontalLine
import it.polito.students.showteamdetails.ImageView
import it.polito.students.showteamdetails.MemberDetailRow
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.SemiBoldText
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.TaskViewModel
import java.time.format.DateTimeFormatter

@Composable
fun InfoTaskPage(taskVm: TaskViewModel, routerActions: RouterActions, teamId: String) {
    InfoTask(taskVm)
    Spacer(modifier = Modifier.height(30.dp))
    DescriptionSection(taskVm)
    Spacer(modifier = Modifier.height(30.dp))
    CategorySection(taskVm)
    Spacer(modifier = Modifier.height(10.dp))
    HorizontalLine()
    Spacer(modifier = Modifier.height(30.dp))
    TagsSection(taskVm)
    Spacer(modifier = Modifier.height(10.dp))
    HorizontalLine()
    Spacer(modifier = Modifier.height(30.dp))
    SemiBoldText(text = stringResource(R.string.delegates))
    MembersListSection(taskVm, routerActions, teamId)
    Spacer(modifier = Modifier.height(30.dp))
}


@Composable
fun InfoTask(taskVm: TaskViewModel) {
    val dueDateIcon = painterResource(id = R.drawable.due_date)
    val repeatIcon = painterResource(id = R.drawable.repeat)
    val fontSize = 18.sp
    val iconSize = Modifier.size(23.dp)

    val statusIcon = when (taskVm.statusField.editStatus) {
        Utils.StatusEnum.PENDING -> painterResource(id = R.drawable.pending)
        Utils.StatusEnum.IN_PROGRESS -> painterResource(id = R.drawable.in_progress)
        Utils.StatusEnum.ON_HOLD -> painterResource(id = R.drawable.on_hold)
        Utils.StatusEnum.OVERDUE -> painterResource(id = R.drawable.overdue)
        Utils.StatusEnum.DONE -> painterResource(id = R.drawable.done_icon)
    }

    val member = taskVm.creationField.createdBy

    //Created by
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (member.photo != null)
            ImageView(painterBitmap = member.photo, size = 45.dp)
        else
            AvatarView(name = member.name, surname = member.surname, size = 45)

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.task_created_by))
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                    )
                ) {
                    append(" ${taskVm.creationField.createdBy.name} ${taskVm.creationField.createdBy.surname}")
                }
            },
            fontSize = fontSize,
        )
    }
    Spacer(modifier = Modifier.height(15.dp))

    //Start date
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            painter = dueDateIcon,
            contentDescription = null,
            modifier = iconSize
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buildAnnotatedString {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("${stringResource(R.string.start_date)}: ")
                pop()
                append("${taskVm.startDateField.dateToString()}, ${taskVm.startDateField.timeToString()}")
            },
            fontSize = fontSize,
        )
    }
    Spacer(modifier = Modifier.height(15.dp))

    //Due date
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            painter = dueDateIcon,
            contentDescription = null,
            modifier = iconSize
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buildAnnotatedString {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("${stringResource(R.string.due_date)}: ")
                pop()
                append("${taskVm.dueDateField.dateToString()}, ${taskVm.dueDateField.timeToString()}")
            },
            fontSize = fontSize,
        )
    }
    Spacer(modifier = Modifier.height(15.dp))

    //Recurrence
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            painter = repeatIcon,
            contentDescription = null,
            modifier = iconSize
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buildAnnotatedString {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("${stringResource(R.string.recurrence)}: ")
                pop()
                append(stringResource(Utils.convertRepeatToString(taskVm.repeatField.value)))

                if (taskVm.repeatField.value != Utils.RepeatEnum.NO_REPEAT) {
                    append("\n")
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append("${stringResource(R.string.repeat_end_date)}: ")
                    pop()
                    append(taskVm.repeatField.repeatEndDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                }
            },
            fontSize = fontSize,
        )
    }
    Spacer(modifier = Modifier.height(15.dp))

    //Status
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            painter = statusIcon,
            contentDescription = null,
            modifier = iconSize,
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buildAnnotatedString {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("${stringResource(R.string.status)}: ")
                pop()
                append(stringResource(Utils.convertStatusToString(taskVm.statusField.status)))
            },
            fontSize = fontSize,
        )
    }
}

@Composable
fun DescriptionSection(taskVm: TaskViewModel) {
    val darkGrayBackground = colorResource(R.color.darkGray)
    var expanded by remember { mutableStateOf(false) }
    val view = LocalView.current

    Column {
        OutlinedTextField(
            value = taskVm.descriptionField.value,
            label = {
                Text(
                    stringResource(id = R.string.description_task),
                    color = darkGrayBackground,
                    fontSize = 16.sp
                )
            },
            onValueChange = {},
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 30.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                .clickable {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    expanded = !expanded //Added animation
                },
            maxLines = if (!expanded) 4 else 500, //lines
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = darkGrayBackground,
                unfocusedBorderColor = darkGrayBackground,
            )
        )

        if (!expanded && taskVm.descriptionField.value.length > 100) { //100 characters
            Text(
                text = stringResource(id = R.string.see_more),
                color = colorResource(R.color.darkGray),
                modifier = Modifier.clickable {
                    Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                    expanded = true
                }
            )
        }
    }
}


@Composable
fun CategorySection(taskVm: TaskViewModel) {
    val darkGrayBackground = colorResource(R.color.darkGray)

    OutlinedTextField(
        value = stringResource(id = Utils.convertCategoryToString(taskVm.categoryField.category)),
        label = {
            Text(
                stringResource(
                    id = R.string.category
                ),
                color = darkGrayBackground,
                fontSize = 16.sp
            )
        },
        onValueChange = {},
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 30.sp
        ),
        modifier = Modifier.fillMaxWidth(),
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = darkGrayBackground,
            unfocusedBorderColor = darkGrayBackground,
        )
    )
}

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsSection(taskVm: TaskViewModel) {
    SemiBoldText(text = stringResource(R.string.tags))
    Spacer(modifier = Modifier.height(5.dp))

    if (taskVm.assignTagsField.value.isEmpty()) {
        Text(
            text = stringResource(R.string.any_tags),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 10.dp),
            fontSize = 17.sp
        )
    } else {
        Column {
            FlowRow(
                Modifier
                    .fillMaxWidth(1f)
                    .wrapContentHeight(align = Alignment.Top),
                horizontalArrangement = Arrangement.Start,
            ) {
                taskVm.assignTagsField.value.fastForEachIndexed { _, element ->
                    val tagColor = taskVm.assignTagsField.getTagColor(element)
                    val chipColor = colorResource(tagColor) // Convert ID color in object Color
                    AssistChip(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .align(alignment = Alignment.CenterVertically),
                        onClick = { },
                        label = {
                            Text(
                                color = MaterialTheme.colorScheme.primary,
                                text = element,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = chipColor
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MembersListSection(taskVm: TaskViewModel, routerActions: RouterActions, teamId: String) {
    Spacer(modifier = Modifier.height(5.dp))
    Column {
        taskVm.delegateTasksField.members.forEach { member ->
            MemberDetailRow(
                taskVm,
                member,
                false,
                null,
                routerActions = routerActions,
                teamId = teamId
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}