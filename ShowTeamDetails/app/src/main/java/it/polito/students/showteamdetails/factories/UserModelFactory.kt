package it.polito.students.showteamdetails.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.polito.students.showteamdetails.MyApplication
import it.polito.students.showteamdetails.viewmodel.UserViewModel

class UserModelFactory(context: Context) : ViewModelProvider.Factory {
    val model =
        (context.applicationContext as? MyApplication)?.userModel ?: throw IllegalArgumentException(
            "Wrong application class!"
        )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(model) as T
        } else throw IllegalArgumentException("Unexpected view model class!")
    }
}