package it.polito.students.showteamdetails

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import it.polito.students.showteamdetails.model.ChatModel
import it.polito.students.showteamdetails.model.TaskModel
import it.polito.students.showteamdetails.model.UserModel

class MyApplication : Application() {
    lateinit var individualChatsModel: ChatModel
    lateinit var userModel: UserModel
    lateinit var tasksModel: TaskModel

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        /*val providerFactory = PlayIntegrityAppCheckProviderFactory.getInstance()
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(providerFactory)*/

        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )

        individualChatsModel = ChatModel()
        userModel = UserModel()
        tasksModel = TaskModel()
    }

}