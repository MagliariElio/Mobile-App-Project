package it.polito.students.showteamdetails.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.AvatarView
import it.polito.students.showteamdetails.ImageView
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.MemberInfoTeam
import it.polito.students.showteamdetails.viewmodel.TaskViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HistoryTaskPage(taskVm: TaskViewModel) {
    val sortedHistory = taskVm.historyListField.editHistories.sortedByDescending { it.date }
    var oldDate: String? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        sortedHistory.forEachIndexed { index, it ->
            val date = it.date.toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))

            HistoryCard(
                dateTime = it.date,
                delegateMember = it.delegatedMember,
                value = it.value,
                member = it.member,
                oldDate = oldDate,
                index = index,
                length = sortedHistory.size
            )
            oldDate = date
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Composable
fun HistoryCard(
    dateTime: LocalDateTime,
    delegateMember: MemberInfoTeam?,
    value: Int,
    member: Member,
    oldDate: String?,
    index: Int,
    length: Int
) {
    var textHistory = member.name + " " + member.surname + " "

    textHistory += if (delegateMember != null) {
        stringResource(value, delegateMember.profile.name, delegateMember.profile.surname)
    } else {
        stringResource(value)
    }

    //Date and time
    val date = dateTime.toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icons indicate timeline
            if (oldDate?.equals(date) == false || oldDate == null) {
                Spacer(modifier = Modifier.width(15.dp))
                when (index) {
                    0 -> {
                        Icon(
                            painter = painterResource(id = R.drawable.history_icon_top),
                            contentDescription = null,
                            modifier = Modifier.size(35.dp)
                        )
                    }

                    length - 1 -> {
                        Icon(
                            painter = painterResource(id = R.drawable.history_icon),
                            contentDescription = null,
                            modifier = Modifier.size(35.dp),
                            tint = Color.Unspecified
                        )
                    }

                    else -> {
                        Icon(
                            painter = painterResource(id = R.drawable.history_icon),
                            contentDescription = null,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = date,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        if (oldDate?.equals(date) == false || oldDate == null) {
            Spacer(modifier = Modifier.padding(0.dp, 25.dp, 0.dp, 0.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp, 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (member.photo != null) {
                        ImageView(painterBitmap = member.photo, size = 45.dp)
                    } else {
                        AvatarView(name = member.name, surname = member.surname, 45)
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    //Member
                    Text(
                        text = textHistory,
                        fontWeight = FontWeight.Medium,
                        fontSize = 19.sp
                    )
                }
            }
        }
    }
}