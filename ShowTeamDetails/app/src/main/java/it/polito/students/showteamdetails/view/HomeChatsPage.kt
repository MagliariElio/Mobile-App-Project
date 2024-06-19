package it.polito.students.showteamdetails.view

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.polito.students.showteamdetails.AvatarView
import it.polito.students.showteamdetails.ImageView
import it.polito.students.showteamdetails.MemberProfileCircle
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.Chat
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.Team
import it.polito.students.showteamdetails.factories.ChatModelFactory
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.ChatViewModel
import it.polito.students.showteamdetails.viewmodel.TeamListViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeChatsPage(
    routerActions: RouterActions,
    individualChats: List<Chat>,
    groupChats: List<Chat>,
    teamListVm: TeamListViewModel
) {

    BoxWithConstraints(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        val pageWidth: Float = if (this.maxWidth < this.maxHeight) {
            30f
        } else {
            100f
        }

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 5.dp)
                .fillMaxSize(pageWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ChatsPage(routerActions, teamListVm, individualChats, groupChats)
            }
        }
    }
}

@Composable
fun ChatsPage(
    routerActions: RouterActions,
    teamListVm: TeamListViewModel,
    individualChats: List<Chat>,
    groupChats: List<Chat>
) {
    SearchArea(teamListVm)

    var chats = (individualChats + groupChats).sortedWith(compareByDescending {
        it.lastMessageDate ?: LocalDateTime.MIN
    })

    if (teamListVm.searchedChat.isNotBlank()) {
        chats = chats.filter {
            ((it.teamId != null && (teamListVm.teamList.value.find { team ->
                team.teamField.nameField.value.contains(
                    teamListVm.searchedChat
                )
            } != null) || ((it.singleSender!!.name + " " + it.singleSender!!.surname).contains(
                teamListVm.searchedChat,
                ignoreCase = true
            ))))
        }
    }

    Spacer(modifier = Modifier.height(15.dp))

    chats.forEach {
        ChatBox(routerActions, chat = it, teamListVm = teamListVm)
        Spacer(modifier = Modifier.height(20.dp))
    }

    Spacer(modifier = Modifier.height(100.dp))
}

@Composable
fun NewChatPage(
    teamListVm: TeamListViewModel,
    routerActions: RouterActions,
    neighborUsers: List<Member>,
    individualChats: List<Chat>,
    loggedUser: Member = Utils.memberAccessed.value
) {

    BoxWithConstraints(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        val pageWidth: Float = if (this.maxWidth < this.maxHeight) {
            30f
        } else {
            100f
        }

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 5.dp)
                .fillMaxSize(pageWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .padding(0.dp, 5.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.add_chat),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 25.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .align(Alignment.CenterEnd)
                            .background(colorResource(id = R.color.gray_2), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { routerActions.goBack() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close new chat page"
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(10.dp))
                SearchArea(teamListVm)
                Spacer(modifier = Modifier.height(10.dp))

                var newChats = neighborUsers
                    .distinct()
                    .filter { member ->
                        member.id != Utils.memberAccessed.value.id && !individualChats.any { chat -> chat.singleSender?.email == member.email }
                    }

                if (teamListVm.searchedChat.isNotBlank()) {
                    newChats = newChats.filter {
                        it.name.contains(
                            teamListVm.searchedChat,
                            ignoreCase = true
                        )
                    }
                }
                newChats.sortedBy { it.name + it.surname }
                    .forEach {
                        NewChatRow(member = it, teamListVm = teamListVm, routerActions)
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                if (newChats.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.cannot_add_chat))
                    }
                }
            }
        }
    }

}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ChatBox(routerActions: RouterActions, chat: Chat, teamListVm: TeamListViewModel) {
    BadgedBox(
        badge = {
            if (chat.messages.any { !it.read }) {
                Badge(
                    containerColor = colorResource(id = R.color.attention_color),
                    modifier = Modifier
                        .padding(10.dp)
                        .size(20.dp)
                ) {
                    Text("${chat.messages.filter { !it.read }.size}", fontSize = 13.sp)
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .border(
                    1.dp,
                    color = colorResource(id = R.color.primary_color),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(10.dp, 0.dp)
                .clickable {
                    routerActions.navigateToChat(chat.id)
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Row {
                if (chat.teamId != null) {  // Chat di gruppo
                    val team: Team =
                        teamListVm.teamList.value.find { team -> team.teamField.id == chat.teamId!! }
                            ?.getTeam()!!
                    if (team.picture != null) {
                        ImageView(
                            painterBitmap = team.picture,
                            size = 55.dp
                        )
                    } else {
                        ImageView(painterId = R.drawable.company, size = 55.dp)
                    }
                    Column(
                        modifier = Modifier
                            .height(55.dp)
                            .padding(10.dp, 0.dp)
                            .weight(.7f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = team.name,
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )

                        Row {
                            Text(
                                text = (
                                        if (chat.messages.isNotEmpty()) {
                                            if (chat.messages.last().sender == Utils.memberAccessed.value)
                                                stringResource(id = R.string.me)
                                            else
                                                "${chat.messages.last().sender.name} ${chat.messages.last().sender.surname}"
                                        } else ""),
                                fontSize = 13.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = if (chat.messages.isNotEmpty()) ": ${chat.messages.last().text}" else "",
                                fontSize = 13.sp,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.Black,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .height(55.dp)
                            .weight(.1f),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (chat.messages.isNotEmpty()) chat.messages.last().sendDate.format(
                                DateTimeFormatter.ofPattern("HH:mm")
                            ) else "",
                            fontSize = 11.sp,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.Black,
                            fontWeight = FontWeight.Light
                        )
                    }

                } else {   // Chat individuale
                    Row {
                        if (chat.singleSender!!.photo != null) {
                            ImageView(
                                painterBitmap = chat.singleSender!!.photo,
                                size = 55.dp
                            )
                        } else {
                            AvatarView(
                                name = chat.singleSender!!.name,
                                surname = chat.singleSender!!.surname,
                                size = 55
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .height(55.dp)
                            .padding(10.dp, 0.dp)
                            .weight(.7f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${chat.singleSender!!.name} ${chat.singleSender!!.surname}",
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (chat.messages.isNotEmpty()) chat.messages.last().text else "",
                            fontSize = 13.sp,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.Black,
                            fontWeight = FontWeight.Light
                        )
                    }

                    Column(
                        modifier = Modifier
                            .height(55.dp)
                            .weight(.1f),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (chat.messages.isNotEmpty()) chat.messages.last().sendDate.format(
                                DateTimeFormatter.ofPattern("HH:mm")
                            ) else "",
                            fontSize = 11.sp,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.Black,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewChatRow(
    member: Member,
    teamListVm: TeamListViewModel,
    routerActions: RouterActions,
    chatViewModel: ChatViewModel = viewModel(factory = ChatModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .border(
                1.dp,
                colorResource(id = R.color.primary_color),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                coroutineScope.launch {
                    try {
                        val newChat = Chat(
                            id = "",
                            lastMessageDate = null,
                            messages = mutableListOf(),
                            singleSender = member,
                            teamId = null
                        )

                        val newChatId = chatViewModel.addNewIndividualChat(newChat)
                        routerActions.navigateToChat(newChatId)
                    } catch (e: Exception) {
                        Log.e("ERROR", "Failed to add new chat", e)
                        Toast
                            .makeText(context, "Failed to add new chat", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            MemberProfileCircle(member = member, size = 55)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "${member.name} ${member.surname}",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
fun SearchArea(teamListVm: TeamListViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = teamListVm.searchedChatTextField,
            label = {
                Text(
                    text = stringResource(id = R.string.search),
                    fontSize = 13.sp
                )
            },
            onValueChange = {
                teamListVm.searchedChatTextField = it
                teamListVm.searchedChat = teamListVm.searchedChatTextField.trim()
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    teamListVm.searchedChat = teamListVm.searchedChatTextField.trim()
                }
            ),
            trailingIcon = {
                Row {
                    if (teamListVm.searchedChatTextField.isNotBlank())
                        IconButton(onClick = {
                            teamListVm.searchedChatTextField = ""; teamListVm.searchedChat = ""
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear filter"
                            )
                        }

                    IconButton(onClick = {
                        teamListVm.searchedChat = teamListVm.searchedChatTextField.trim()
                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search chat")
                    }
                }

            }
        )
    }
}

