package it.polito.students.showteamdetails

import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.android.gms.auth.api.identity.Identity
import it.polito.students.showteamdetails.entity.File
import it.polito.students.showteamdetails.ui.theme.ShowTeamDetailsTheme
import it.polito.students.showteamdetails.view.HomePage
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {

    companion object {
        lateinit var pictureProfile1: ImageBitmap
        lateinit var pictureProfile2: ImageBitmap
    }

    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    val pickFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultData: Intent? = result.data

            if (resultData != null && resultData.data != null) {
                val uri: Uri = resultData.data!!
                Log.d("UPLOAD FILE", uri.toString())
                val file = getFileInformationByUri(uri)
                FileUploadUriSingleton.setFile(file)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pictureProfile1 = getBitmapFromDrawable(R.drawable.woman_photo_profile)
        pictureProfile2 = getBitmapFromDrawable(R.drawable.man_photo_profile)

        setContent {
            ShowTeamDetailsTheme {
                HomePage(this, intent, googleAuthUiClient)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        setContent {
            HomePage(this, intent, googleAuthUiClient)
        }
    }

    private fun getFileInformationByUri(uri: Uri): File {
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        var name = ""
        var size = 0.0

        val contentTypeString = contentResolver.getType(uri)
        val uploadedBy = Utils.memberAccessed.value
        val dateUpload = LocalDateTime.now()

        cursor?.use {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

            cursor.moveToFirst()

            name = cursor.getString(nameIndex)
            size = cursor.getDouble(sizeIndex)
        }

        val contentType = Utils.getContentTypeFromString(contentTypeString)

        val file = File(
            name,
            contentType,
            Utils.formatFileSize(size),
            uploadedBy,
            dateUpload,
            uri
        )

        return file
    }

    fun getBitmapFromDrawable(drawableId: Int): ImageBitmap {
        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, drawableId)
        return bitmap.asImageBitmap()
    }

}