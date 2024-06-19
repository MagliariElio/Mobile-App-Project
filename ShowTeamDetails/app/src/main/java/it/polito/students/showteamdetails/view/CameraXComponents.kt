package it.polito.students.showteamdetails.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.viewmodel.ProfileImageViewModel
import kotlinx.coroutines.launch

@Composable
fun ImageProfile(vm: ProfileImageViewModel) {
    // Visualization of the image
    Image(
        bitmap = vm.bitmapEdit.value!!,
        contentDescription = stringResource(id = R.string.image_profile),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(200.dp)
            .border(
                BorderStroke(2.dp, Color.Black),
                CircleShape
            )
            .padding(2.dp)
            .clip(CircleShape)
            .graphicsLayer {
                this.alpha = 0.85f
            }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseMenuComponent(
    applicationContext: Context,
    scaffoldState: BottomSheetScaffoldState,
    vm: ProfileImageViewModel
) {
    val scope = rememberCoroutineScope()
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->

            if (uri != null) {
                vm.showRemovePhoto = true
            }

            uri?.let {
                val inputStream = applicationContext.contentResolver.openInputStream(it)
                vm.onTakePhoto(BitmapFactory.decodeStream(inputStream).asImageBitmap())
            }
        }
    )

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(14.dp))
        Icon(
            painter = painterResource(R.drawable.gallery_icon),
            contentDescription = "SelectPhoto",
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        ClickableText(
            text = AnnotatedString(stringResource(R.string.select_photo)),
            onClick = {


                // Select photo from gallery
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )

                scope.launch {
                    scaffoldState.bottomSheetState.hide()
                }
            }
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(14.dp))

        Icon(
            painter = painterResource(R.drawable.photo_icon),
            contentDescription = "TakePhoto",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        ClickableText(
            text = AnnotatedString(stringResource(R.string.take_photo)),
            onClick = {
                // Take photo
                vm.enterCameraMode()
                scope.launch {
                    scaffoldState.bottomSheetState.hide()
                }
            }
        )
    }

    if (vm.showRemovePhoto) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.width(14.dp))
            Icon(
                painter = painterResource(R.drawable.trash_icon),
                contentDescription = "Trash",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            ClickableText(
                text = AnnotatedString(stringResource(R.string.delete_photo)),
                onClick = {
                    // Remove photo
                    vm.showConfirmRemovePhoto = true

                    scope.launch {
                        scaffoldState.bottomSheetState.hide()
                    }
                }
            )
        }
        if (vm.showConfirmRemovePhoto) {
            ConfirmRemovePhoto(vm)
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
}


@Composable
fun ConfirmRemovePhoto(vm: ProfileImageViewModel) {
    var openDialog by remember {
        mutableStateOf(true)
    }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.delete_photo))
                }

            },
            text = {
                Text(
                    text = stringResource(id = R.string.confirm_delete_photo)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        vm.removePhoto()
                        vm.showConfirmRemovePhoto = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.delete_photo))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        vm.showConfirmRemovePhoto = false
                    },
                    border = BorderStroke(1.dp, colorResource(R.color.blue)),
                ) {
                    Text(text = stringResource(id = R.string.keep_photo))
                }
            }
        )
    }
}

@Composable
fun CameraViewComponent(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}

fun takePhotoCustom(
    controller: LifecycleCameraController,
    onPhotoTaken: (ImageBitmap) -> Unit,
    applicationContext: Context
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(applicationContext),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    super.onCaptureSuccess(image)
                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                    }
                    val rotatedBitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        0,
                        0,
                        image.width,
                        image.height,
                        matrix,
                        true
                    )
                    onPhotoTaken(rotatedBitmap.asImageBitmap())
                    Log.i("Camera", "The picture has been taken!")
                } catch (e: Exception) {
                    println("${e.message}")
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Could not take photo: ", exception)
            }
        }
    )
}

val CAMERAX_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA
)

fun hasRequiredPermissions(applicationContext: Context): Boolean {
    return CAMERAX_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            applicationContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun rotateBitmap(bitmap: ImageBitmap, degrees: Float): ImageBitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(
        bitmap.asAndroidBitmap(), 0, 0, bitmap.width, bitmap.height, matrix, true
    ).asImageBitmap()
}
