package it.polito.students.showteamdetails.view

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.polito.students.showteamdetails.MemberProfileCircle
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.TeamProfileCircle
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.Chat
import it.polito.students.showteamdetails.entity.Member
import it.polito.students.showteamdetails.entity.Message
import it.polito.students.showteamdetails.factories.ChatModelFactory
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.ChatViewModel
import it.polito.students.showteamdetails.viewmodel.TeamListViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("UnrememberedMutableState")
@Composable
fun ChatPage(
    openedChat: Chat,
    teamListVm: TeamListViewModel,
    routerActions: RouterActions,
    chatViewModel: ChatViewModel = viewModel(factory = ChatModelFactory(LocalContext.current))
) {

    var openChat =
        chatViewModel.individualChats.collectAsState().value.find { it.id == openedChat.id }
            ?: openedChat

    BackHandler {
        teamListVm.newTextMessage = ""
        chatViewModel.readMessages(openChat.messages.filter { !it.read })
        routerActions.goBack()
    }

    val listState = rememberLazyListState()
    val messages = openChat.messages.sortedBy { it.sendDate }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        val pageWidth: Float = if (this.maxWidth < this.maxHeight) {
            30f
        } else {
            100f
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            TopChatBar(teamListVm = teamListVm, openChat, routerActions)

            Box(
                modifier = Modifier
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 70.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        openChat.messages.sortedBy { it.sendDate }
                            .forEachIndexed { index, message ->
                                teamListVm.recomposition //Do NOT remove, used just to update the view manually

                                if (!message.read && (index == 0 || openChat.messages[index - 1].read)) {
                                    NotReadLabel()
                                }

                                if (index == 0 || (index > 0 && openChat.messages[index - 1].sendDate.toLocalDate() != message.sendDate.toLocalDate())) {
                                    DayLabel(message = message)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 10.dp,
                                            end = 10.dp,
                                            bottom = if (index != openChat.messages.size - 1 && openChat.messages[index + 1].sender == message.sender) 6.dp else 15.dp
                                        )
                                        .requiredSizeIn(minHeight = 30.dp)
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onLongPress = {
                                                    if (message.sender == Utils.memberAccessed.value)
                                                        teamListVm.messageMenuOpen = message
                                                }
                                            )
                                        },
                                    horizontalArrangement = if (message.sender == Utils.memberAccessed.value) Arrangement.End else Arrangement.Start
                                ) {

                                    MessageBox(
                                        pageWidth = pageWidth,
                                        message = message,
                                        teamListVm = teamListVm,
                                        openChat = openChat
                                    )

                                    if (teamListVm.messageMenuOpen == message) {
                                        DropdownMenu(
                                            modifier = Modifier
                                                .background(Color.White)
                                                .border(
                                                    1.dp,
                                                    colorResource(id = R.color.primary_color),
                                                    RoundedCornerShape(4.dp)
                                                ),
                                            expanded = teamListVm.messageMenuOpen == message,
                                            onDismissRequest = {
                                                teamListVm.messageMenuOpen = null
                                            }) {

                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            5.dp
                                                        ),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Edit,
                                                            contentDescription = "Edit message"
                                                        )
                                                        Text(text = stringResource(id = R.string.edit_message))
                                                    }
                                                },
                                                onClick = {
                                                    teamListVm.editingMessage = message
                                                    teamListVm.messageMenuOpen = null
                                                    teamListVm.newTextMessage = message.text
                                                })

                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            5.dp
                                                        ),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Delete,
                                                            contentDescription = "Edit message",
                                                            tint = colorResource(id = R.color.light_red)
                                                        )
                                                        Text(text = stringResource(id = R.string.delete_message))
                                                    }
                                                },
                                                onClick = {
                                                    teamListVm.deleteMessageModalOpen = message
                                                    teamListVm.messageMenuOpen = null
                                                })

                                        }
                                    }

                                }

                            }
                    }
                    item {
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }

                if (teamListVm.deleteMessageModalOpen != null) {
                    AlertDialog(
                        onDismissRequest = {
                            teamListVm.deleteMessageModalOpen = null
                        },
                        title = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = stringResource(id = R.string.delete_message))
                            }
                        },
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.delete_message_confirm),
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    chatViewModel.deleteMessage(
                                        openChat,
                                        teamListVm.deleteMessageModalOpen!!.id
                                    )
                                    teamListVm.deleteMessageModalOpen = null
                                }
                            ) {
                                Text(
                                    text = stringResource(id = R.string.delete_message),
                                    color = colorResource(id = R.color.light_red),
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    teamListVm.deleteMessageModalOpen = null
                                },
                                border = BorderStroke(1.dp, colorResource(R.color.blue)),
                            ) {
                                Text(text = stringResource(R.string.cancel))
                            }
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .imePadding()
                        .padding(10.dp)
                ) {
                    TextBottomBar(teamListVm = teamListVm, openChat = openChat)
                }
            }
        }
    }
}

fun sendMessage(
    teamListViewModel: TeamListViewModel,
    openChat: Chat,
    sendNewMessage: (Chat, Message) -> Unit
) {
    if (teamListViewModel.newTextMessage.isNotBlank()) {
        val newMessage = Message(
            id = "",
            sendDate = LocalDateTime.now(),
            sender = Utils.memberAccessed.value,
            text = teamListViewModel.newTextMessage.trim(),
            read = true
        )

        sendNewMessage(openChat, newMessage)

        teamListViewModel.newTextMessage = ""
    }
}

fun editMessage(
    teamListViewModel: TeamListViewModel,
    openChat: Chat,
    editMessage: (Message, String) -> Unit
) {
    if (teamListViewModel.newTextMessage.isNotBlank() && teamListViewModel.newTextMessage != teamListViewModel.editingMessage!!.text) {

        editMessage(teamListViewModel.editingMessage!!, teamListViewModel.newTextMessage)

        var messages: MutableList<Message> = openChat.messages.map { msg ->
            if (msg.id == teamListViewModel.editingMessage!!.id) {
                msg.copy(text = teamListViewModel.newTextMessage, edited = true)
            } else
                msg
        }.toMutableList()

        openChat.messages = messages

        teamListViewModel.newTextMessage = ""
        teamListViewModel.editingMessage = null
        teamListViewModel.recomposition++ //Do NOT remove, just to update the view
    } else {
        teamListViewModel.newTextMessage = ""
        teamListViewModel.editingMessage = null
    }
}

@Composable
fun getSenderColor(sender: Member): Color {
    val colors = listOf(
        Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8),
        Color(0xFF9575CD), Color(0xFF7986CB), Color(0xFF64B5F6),
        Color(0xFF4FC3F7), Color(0xFF4DD0E1), Color(0xFF4DB6AC),
        Color(0xFF81C784), Color(0xFFAED581), Color(0xFFFFD54F),
        Color(0xFFFFB74D), Color(0xFFFF8A65), Color(0xFFA1887F),
        Color(0xFFE0E0E0), Color(0xFF90A4AE)
    )
    val index = sender.id.hashCode() % colors.size
    if (index < 0) return colors[0]
    return colors[index]
}


@Composable
fun MessageBox(
    pageWidth: Float,
    message: Message,
    teamListVm: TeamListViewModel,
    openChat: Chat
) {
    val senderColor = if (message.sender == Utils.memberAccessed.value) {
        if (teamListVm.messageMenuOpen == message || teamListVm.editingMessage == message) {
            colorResource(id = R.color.attention_color)
        } else {
            colorResource(id = R.color.green).copy(alpha = .3f)
        }
    } else {
        getSenderColor(message.sender).copy(alpha = .3f)
    }

    val borderColor = if (message.sender == Utils.memberAccessed.value) {
        colorResource(id = R.color.primary_color)
    } else {
        getSenderColor(message.sender)
    }

    Column(
        modifier = Modifier
            .widthIn(max = if (pageWidth == 30f) 300.dp else 600.dp, min = 70.dp)
            .background(
                color = senderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(7.dp)
    ) {
        if (openChat.teamId != null && message.sender.id != Utils.memberAccessed.value.id) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MemberProfileCircle(member = message.sender, size = 30)
                Text(
                    text = "${message.sender.name} ${message.sender.surname}",
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Clip,
                    softWrap = true
                )
            }
        }
        Text(
            text = message.text,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Light,
            overflow = TextOverflow.Clip,
            softWrap = true
        )
        Row(
            modifier = Modifier
                .padding(end = 3.dp)
                .align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (message.edited) {
                Text(
                    text = "(${stringResource(id = R.string.edited)})",
                    fontSize = 10.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Clip,
                    softWrap = true,
                    lineHeight = TextUnit(1f, TextUnitType.Sp)
                )
            }

            Text(
                text = message.sendDate.format(DateTimeFormatter.ofPattern("HH:mm")),
                fontSize = 10.sp,
                color = Color.Black,
                fontWeight = FontWeight.Light,
                overflow = TextOverflow.Clip,
                softWrap = true,
                lineHeight = TextUnit(1f, TextUnitType.Sp)
            )
        }
    }
}

@Composable
fun DayLabel(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .requiredSizeIn(minHeight = 30.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .background(colorResource(id = R.color.gray), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (message.sendDate.toLocalDate()) {
                    LocalDate.now() -> stringResource(id = R.string.today)
                    LocalDate.now().minusDays(1) -> stringResource(id = R.string.yesterday)
                    else -> message.sendDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                },
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Light,
                overflow = TextOverflow.Clip,
                softWrap = true
            )
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TopChatBar(
    teamListVm: TeamListViewModel,
    openChat: Chat,
    routerActions: RouterActions
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(Color.White)
            .padding(start = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        IconButton(onClick = {
            teamListVm.newTextMessage = ""
            openChat.messages.filter { !it.read }.forEach { it.read = true }
            routerActions.goBack()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back to chats page"
            )
        }

        Row {
            if (openChat.teamId != null) {
                TeamProfileCircle(
                    teamVm = teamListVm.teamList.value.find { it.teamField.id == openChat.teamId!! }!!,
                    size = 50
                )
            } else {
                MemberProfileCircle(member = openChat.singleSender!!, size = 50)
            }

            Column(
                modifier = Modifier
                    .height(50.dp)
                    .padding(10.dp, 0.dp)
                    .weight(.7f)
                    .clickable {
                        if (openChat.teamId != null) {
                            routerActions.navigateToEditTeam(openChat.teamId!!)
                        } else {
                            routerActions.navigateToMemberProfile(openChat.singleSender!!.id)
                        }
                    },
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = (
                            if (openChat.teamId != null) {
                                teamListVm.teamList.value.find { it.teamField.id == openChat.teamId!! }!!.teamField.nameField.value
                            } else {
                                "${openChat.singleSender!!.name} ${openChat.singleSender!!.surname}"
                            }
                            ),
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (openChat.teamId != null) "${
                        teamListVm.teamList.value.find { it.teamField.id == openChat.teamId!! }!!.teamField.membersField.members.size
                    } ${stringResource(id = R.string.members)}" else if (openChat.singleSender!!.online) stringResource(
                        id = R.string.online
                    ) else stringResource(id = R.string.offline),
                    fontSize = 13.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Light
                )

            }


        }
    }
}

@Composable
fun NotReadLabel() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color.Gray
        )

        Text(
            text = stringResource(id = R.string.unread_messages),
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color.Gray
        )
    }
}

@Composable
fun TextBottomBar(
    teamListVm: TeamListViewModel,
    openChat: Chat,
    chatViewModel: ChatViewModel = viewModel(factory = ChatModelFactory(LocalContext.current))
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = teamListVm.newTextMessage,
            onValueChange = { teamListVm.newTextMessage = it },
            modifier = Modifier
                .weight(1f)
                .background(Color.White, RoundedCornerShape(30.dp))
                .border(
                    1.dp,
                    colorResource(id = R.color.primary_color),
                    RoundedCornerShape(30.dp)
                )
                .clip(RoundedCornerShape(30.dp)),
            placeholder = { Text(stringResource(id = R.string.new_message)) },
            shape = RoundedCornerShape(30.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = true,
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(onSend = {
                if (teamListVm.editingMessage != null) {
                    //Sto editando un messaggio
                    editMessage(teamListVm, openChat, chatViewModel::editMessage)
                } else {
                    //Sto mandando un nuovo messaggio
                    sendMessage(teamListVm, openChat, chatViewModel::sendNewMessage)
                }
            }),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = colorResource(id = R.color.gray),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )


        IconButton(
            onClick = {
                if (teamListVm.editingMessage != null) {
                    editMessage(teamListVm, openChat, chatViewModel::editMessage)
                } else {
                    sendMessage(teamListVm, openChat, chatViewModel::sendNewMessage)
                }
            },
            modifier = Modifier
                .border(
                    1.dp,
                    colorResource(id = R.color.primary_color),
                    CircleShape
                )
                .background(colorResource(id = R.color.primary_color), CircleShape)
        ) {
            if (teamListVm.editingMessage != null) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit message",
                    tint = Color.White
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send message",
                    tint = Color.White,
                    modifier = Modifier.graphicsLayer(rotationZ = -45f)
                )
            }

        }


    }
}
