package it.polito.students.showteamdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.students.showteamdetails.entity.Chat
import it.polito.students.showteamdetails.entity.Message
import it.polito.students.showteamdetails.model.ChatModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    val chatModel: ChatModel,
) : ViewModel() {

    private val _individualChats = MutableStateFlow<List<Chat>>(emptyList())
    val individualChats: StateFlow<List<Chat>> = _individualChats

    private val _groupChats = MutableStateFlow<List<Chat>>(emptyList())
    val groupChats: StateFlow<List<Chat>> = _groupChats

    init {
        // Avvia la raccolta dei dati di individualChats
        viewModelScope.launch {
            chatModel.getMyIndividualChats().collect { chats ->
                _individualChats.value = chats
            }
        }

        // Avvia la raccolta dei dati di groupChats
        viewModelScope.launch {
            chatModel.getMyGroupChats().collect { chats ->
                _groupChats.value = chats
            }
        }
    }

    fun sendNewMessage(chat: Chat, newMessage: Message) {
        viewModelScope.launch {
            chatModel.sendNewMessage(chat, newMessage)
        }
    }

    fun readMessages(messages: List<Message>) = chatModel.readMessages(messages)

    fun editMessage(newMessage: Message, newText: String) =
        chatModel.editMessage(newMessage, newText)

    fun deleteMessage(chat: Chat, messageId: String) = chatModel.deleteMessage(chat, messageId)

    suspend fun addNewIndividualChat(newChat: Chat): String =
        chatModel.addNewIndividualChat(newChat)
}

