package it.polito.students.showteamdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.polito.students.showteamdetails.entity.File

object FileUploadUriSingleton {
    private val file = MutableLiveData<File?>()

    fun setFile(fileParam: File?) {
        file.value = fileParam
    }

    fun getFile(): LiveData<File?> {
        return file
    }
}