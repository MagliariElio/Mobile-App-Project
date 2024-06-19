package it.polito.students.showteamdetails.view

import android.content.ClipData
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.SemiBoldText
import it.polito.students.showteamdetails.ShowToast
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.routers.RouterActions
import it.polito.students.showteamdetails.viewmodel.TeamViewModel
import kotlinx.coroutines.delay
import java.util.Date

@Composable
fun ShareTeamPage(teamVm: TeamViewModel, routerActions: RouterActions) {
    val context = LocalContext.current
    val showToast = remember { mutableStateOf(false) }
    val countDownTimeValue = 3600 // scadenza qr code di 1 ora
    val countdownTime = remember { mutableStateOf(countDownTimeValue) }
    val expiryDate =
        remember { mutableStateOf(Date(System.currentTimeMillis() + 1000 * countDownTimeValue)) } // Scadenza iniziale di 1 ora

    BoxWithConstraints(
        modifier = Modifier.fillMaxHeight()
    ) {
        val pageWidth: Float = if (this.maxWidth < this.maxHeight) {
            //Portrait mode
            this.maxWidth.value * .85f
        } else {
            //Landscape mode
            this.maxWidth.value * .8f
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(Dp(pageWidth)),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    val pageTitle = stringResource(R.string.share_team)

                    // Title and Back Button
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.align(Alignment.CenterStart),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { routerActions.goBack() }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                                    contentDescription = stringResource(R.string.go_back),
                                    modifier = Modifier.size(35.dp),
                                )
                            }
                        }
                        Box(modifier = Modifier.align(Alignment.Center)) {
                            SemiBoldText(pageTitle)
                        }
                    }

                    // Section QR Code
                    val qrCodeBitmap = Utils.generateQrCode(teamVm.teamField.id, expiryDate.value)
                    if (qrCodeBitmap != null) {
                        Image(
                            bitmap = qrCodeBitmap.asImageBitmap(),
                            contentDescription = "Qr Code",
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(300.dp)
                        )
                    } else {
                        Text(text = stringResource(R.string.qr_code_error))
                    }

                    // Countdown Timer
                    CountdownTimer(countdownTime.value) { secondsRemaining ->
                        countdownTime.value = secondsRemaining
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Button Copy Link
                    Button(
                        onClick = {
                            val clipboardManager = context.applicationContext.getSystemService(
                                Context.CLIPBOARD_SERVICE
                            ) as android.content.ClipboardManager
                            val clipData = ClipData.newPlainText(
                                "link",
                                Utils.copyToClipboard(teamVm.teamField.id)
                            )
                            clipboardManager.setPrimaryClip(clipData)
                            showToast.value = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            colorResource(R.color.blue),
                            colorResource(R.color.white)
                        )
                    ) {
                        Text(text = stringResource(R.string.copy_link))
                    }

                    if (countdownTime.value <= 0) {
                        Button(
                            onClick = {
                                expiryDate.value =
                                    Date(System.currentTimeMillis() + 1000 * countDownTimeValue)
                                countdownTime.value = countDownTimeValue
                            },
                            colors = ButtonDefaults.buttonColors(
                                colorResource(R.color.blue),
                                colorResource(R.color.white)
                            )
                        ) {
                            Text(text = stringResource(R.string.regenerate_qr))
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Advice
                    Text(
                        text = stringResource(R.string.qr_code_advice_message),
                        fontSize = 14.sp,
                        softWrap = true
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }

        if (showToast.value) {
            ShowToast(message = stringResource(R.string.link_copied))
            showToast.value = false
        }
    }
}

@Composable
fun CountdownTimer(initialSeconds: Int, onTick: (Int) -> Unit) {
    val currentSeconds = remember { mutableStateOf(initialSeconds) }

    LaunchedEffect(initialSeconds) {
        currentSeconds.value = initialSeconds
        while (currentSeconds.value > 0) {
            delay(1000L)
            currentSeconds.value--
            onTick(currentSeconds.value)
        }
    }

    Text(
        text = stringResource(R.string.countdown_label, currentSeconds.value),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}