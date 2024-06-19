package it.polito.students.showteamdetails.view

import android.view.SoundEffectConstants
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.MemberInfoTeam
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.TaskViewModel
import it.polito.students.showteamdetails.viewmodel.TeamViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun StatisticsPage(teamVm: TeamViewModel, routerActions: RouterActions) {
    val scrollState = rememberScrollState()
    var selectedStatisticsMember by remember { mutableStateOf<MemberInfoTeam?>(null) }
    var tasksFiltered by remember { mutableStateOf(teamVm.tasksList.toMutableList()) }

    LaunchedEffect(selectedStatisticsMember) {
        tasksFiltered = if (selectedStatisticsMember != null) {
            teamVm.tasksList
                .filter {
                    it.creationField.createdBy.nickname == selectedStatisticsMember?.profile?.nickname ||
                            it.delegateTasksField.members.any { member ->
                                member.profile.nickname == selectedStatisticsMember?.profile?.nickname
                            }
                }.toMutableList()
        } else {
            teamVm.tasksList.toMutableList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        StatisticTitle(routerActions)
        Spacer(modifier = Modifier.height(20.dp))

        MemberMenu(
            teamVm.teamField.membersField.members,
            onSelected = { selectedStatisticsMember = it },
            selectedStatisticsMember = selectedStatisticsMember
        )

        if (tasksFiltered.isEmpty()) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(R.string.no_data_statistics),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Spacer(modifier = Modifier.height(10.dp))
            CompletenessSection(tasksFiltered)
            Spacer(modifier = Modifier.height(20.dp))
            if (selectedStatisticsMember == null) {
                DivideStatistics()
                RankingTable(
                    members = teamVm.teamField.membersField.members,
                    tasks = tasksFiltered,
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            DivideStatistics()
            Spacer(modifier = Modifier.height(10.dp))
            DiagramTasksDue(tasksFiltered)
            Spacer(modifier = Modifier.height(30.dp))
            DivideStatistics()
            Spacer(modifier = Modifier.height(30.dp))
            DiagramAverageTimePerTasks(tasksFiltered)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberMenu(
    members: List<MemberInfoTeam>,
    onSelected: (MemberInfoTeam?) -> Unit,
    selectedStatisticsMember: MemberInfoTeam?
) {
    val view = LocalView.current
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = isExpanded,
            onExpandedChange = {
                Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                isExpanded = it
            }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .weight(1f),
                    value = if (selectedStatisticsMember != null)
                        "${selectedStatisticsMember.profile.name} ${selectedStatisticsMember.profile.surname}"
                    else
                        stringResource(id = R.string.select_member),
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = {
                        Text(
                            stringResource(R.string.filter_by_member),
                            color = colorResource(R.color.darkGray),
                            fontSize = 16.sp
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (selectedStatisticsMember != null) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.clear),
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Transparent)
                            .clickable {
                                Utils.playSoundEffectWithSoundCheck(
                                    view,
                                    SoundEffectConstants.CLICK
                                )
                                onSelected(null)
                                isExpanded = false
                            }
                            .padding(4.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                members.forEach { member ->
                    DropdownMenuItem(
                        onClick = {
                            Utils.playSoundEffectWithSoundCheck(view, SoundEffectConstants.CLICK)
                            onSelected(member)

                            isExpanded = false
                        },
                    ) {
                        Text(text = "${member.profile.name} ${member.profile.surname}")
                    }
                }
            }
        }
    }
}

@Composable
fun DivideStatistics() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Divider(
            color = Color.Gray,
            thickness = 2.dp,
            modifier = Modifier
                .width(220.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun StatisticTitle(routerActions: RouterActions) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    routerActions.goBack()
                }
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
            text = stringResource(id = R.string.statistics),
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun CompletenessSection(tasks: List<TaskViewModel>) {
    /* Date should come from db */
    val taskComplete = tasks.filter { it.statusField.status == Utils.StatusEnum.DONE }.size
    val totalTasks = tasks.size

    val progressTask = (taskComplete.toFloat() / totalTasks)
    val percentExecutedTask = (progressTask * 100).toInt()

    val dueDate = tasks.maxBy { it.dueDateField.date }
    val dueDateTime = dueDate.dueDateField.date.atTime(dueDate.dueDateField.time)

    val completedIn = if (dueDate.dueDateField.date.isAfter(LocalDate.now())) {
        Duration.between(LocalDateTime.now(), dueDateTime)
    } else {
        Duration.between(dueDateTime, LocalDateTime.now())
    }.toDays().toString()

    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.completeness),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp)) {
                        append(stringResource(R.string.process_is) + " ")
                    }
                    if (taskComplete != totalTasks) {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        ) {
                            append("$percentExecutedTask% ")
                        }
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp)) {
                        append(stringResource(R.string.completed))
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        LinearProgressIndicator(
            progress = progressTask,
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(14.dp))
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text =
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                        append("$taskComplete/$totalTasks")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp)) {
                        append(" ${stringResource(R.string.completed)} ${stringResource(R.string.tasks).lowercase()}")
                    }
                }
            )
        }

        if (taskComplete != totalTasks) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp)) {
                        if (dueDate.dueDateField.date.isBefore(LocalDate.now())) {
                            append(stringResource(R.string.process_was_supposed))
                        } else {
                            append(stringResource(R.string.process_ends))
                        }
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                        append(" $completedIn")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp)) {
                        append(" " + stringResource(R.string.days))
                        if (dueDate.dueDateField.date.isBefore(LocalDate.now())) {
                            append(" " + stringResource(R.string.ago))
                        }
                    }
                })
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp)) {
                        append(stringResource(R.string.due_date))
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                        append(": " + Utils.formatLocalDate(dueDate.dueDateField.date))
                    }
                })
            }
        }
    }
}

@Composable
fun RankingTable(members: List<MemberInfoTeam>, tasks: List<TaskViewModel>) {
    val data = Utils.calculateRanking(members = members, tasksParam = tasks)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.ranking),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "(${stringResource(R.string.assigned_tasks_completed)})",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (data.isEmpty()) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp)) {
                        append(stringResource(R.string.no_data_ranking))
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                        append(" " + stringResource(R.string.done_status).uppercase())
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp)) {
                        append(" ${stringResource(R.string.status).lowercase()}.")
                    }
                },
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.position_label),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = stringResource(R.string.member),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.weight(0.6f)
                )
                Text(
                    text = stringResource(R.string.tasks),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.weight(0.2f)
                )
            }

            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                thickness = 1.dp
            )

            // Data Rows
            data.forEachIndexed { i, r ->
                val backgroundColor =
                    if (i % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = (i + 1).toString(),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        modifier = Modifier.weight(0.2f)
                    )
                    Text(
                        text = r.first.profile.name + " " + r.first.profile.surname,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        modifier = Modifier.weight(0.7f)
                    )
                    Text(
                        text = r.second.toString(),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        modifier = Modifier.weight(0.1f)
                    )
                }
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
        }
    }
}


@Composable
fun DiagramTasksDue(tasks: List<TaskViewModel>) {
    val colors = listOf(
        Color(0xFF00AFBB),
        Color(0xFFE7B800),
        Color(0xFFFF69B4),
        Color(0xFFFC4E07),
        Color(0xFF32CD32),
    )
    val labels = Utils.StatusEnum.entries.map { stringResource(Utils.convertStatusToString(it)) }

    val total = tasks.size
    val tasksInPending =
        tasks.count { it.statusField.status == Utils.StatusEnum.PENDING }.toFloat() / total
    val tasksInProgress =
        tasks.count { it.statusField.status == Utils.StatusEnum.IN_PROGRESS }.toFloat() / total
    val tasksInOnHold =
        tasks.count { it.statusField.status == Utils.StatusEnum.ON_HOLD }.toFloat() / total
    val tasksInOverdue =
        tasks.count { it.statusField.status == Utils.StatusEnum.OVERDUE }.toFloat() / total
    val tasksInDone =
        tasks.count { it.statusField.status == Utils.StatusEnum.DONE }.toFloat() / total

    val data = listOf(tasksInPending, tasksInProgress, tasksInOnHold, tasksInOverdue, tasksInDone)
    val tasksData = data.map { it * 100 }
    val sweepAngles = data.map { it * 360 }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.status_task),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(250.dp)
        ) {
            DonutChart(sweepAngles = sweepAngles, colors = colors)
            DonutLegend(labels = labels, colors = colors, data = tasksData)
        }
    }
}

@Composable
fun DonutChart(sweepAngles: List<Float>, colors: List<Color>) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Canvas(
        modifier = Modifier
            .size(250.dp)
            .rotate(angle)
    ) {
        var startAngle = -90f

        sweepAngles.forEachIndexed { index, sweepAngle ->
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 70f, cap = StrokeCap.Round)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun DonutLegend(labels: List<String>, colors: List<Color>, data: List<Float>) {
    fun formatPercentage(percentage: Float): String {
        val formattedString = "%.2f".format(percentage) + "%"
        val hasDecimalDigits = formattedString.endsWith(".00%").not()

        return if (hasDecimalDigits) {
            formattedString
        } else {
            "%.0f".format(percentage) + "%"
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(10.dp)
    ) {
        labels.forEachIndexed { index, label ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(colors[index], shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatPercentage(data[index]),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = label,
                    fontSize = 13.sp,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}


@Composable
fun DiagramAverageTimePerTasks(tasks: List<TaskViewModel>) {
    val data = mutableMapOf<Utils.CategoryEnum, Float>()

    // Calculate each category time
    tasks.forEach { task ->
        val category = task.categoryField.category!!
        val dueDate = task.dueDateField.date.atTime(task.dueDateField.time)
        val startDate = task.startDateField.date.atTime(task.startDateField.time)
        val floatTime = Duration.between(startDate, dueDate).toHours().toFloat()

        data[category] = (data[category] ?: 0f) + floatTime
    }

    // Average per Category
    data.map { dataCategory -> dataCategory.value / tasks.filter { it.categoryField.category == dataCategory.key }.size }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.average_time_per_task),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "(${stringResource(R.string.in_hours)})",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            HorizontalBarChart(
                data = data,
            )
        }
    }
}

@Composable
fun HorizontalBarChart(
    data: MutableMap<Utils.CategoryEnum, Float>,
    barColor: Color = Color(0xFFFAC748),
    labelColor: Color = Color(0xFF283D8F),
) {
    val maxDataValue = data.values.maxOrNull() ?: 0f
    val average = data.values.average().toFloat()

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        data.forEach { dataCategory ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(Utils.convertCategoryToString(dataCategory.key)),
                        modifier = Modifier.width(120.dp),
                        color = labelColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${dataCategory.value.toInt()} ${stringResource(R.string.hours)}",
                        modifier = Modifier.width(120.dp),
                        color = labelColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width((dataCategory.value / maxDataValue * 100).dp)
                            .background(barColor, shape = RoundedCornerShape(4.dp))
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${dataCategory.value.toInt()}",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Average line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.Gray)
        ) {
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth(average / maxDataValue)
                    .background(Color.Red)
            )
        }

        // Average value
        Text(
            text = "${stringResource(R.string.average)}: ${average.toInt()} ${stringResource(R.string.hours)}",
            color = labelColor,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
