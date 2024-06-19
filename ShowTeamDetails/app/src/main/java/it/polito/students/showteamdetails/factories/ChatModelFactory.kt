package it.polito.students.showteamdetails.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.polito.students.showteamdetails.MyApplication
import it.polito.students.showteamdetails.viewmodel.ChatViewModel

class ChatModelFactory(context: Context) : ViewModelProvider.Factory {
    val model = (context.applicationContext as? MyApplication)?.individualChatsModel
        ?: throw IllegalArgumentException("Wrong application class!")

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(model) as T
        } else throw IllegalArgumentException("Unexpected view model class!")
    }
}