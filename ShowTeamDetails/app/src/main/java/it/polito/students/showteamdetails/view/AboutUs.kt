package it.polito.students.showteamdetails.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.Fixture
import it.polito.students.showteamdetails.R

@Composable
fun AboutUs() {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        /* Logo */
        Image(
            painter = painterResource(R.drawable.synchro_task_foreground),
            contentDescription = "copyright",
            modifier = Modifier.size(450.dp)
        )
        /* Version and year Copyright */
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.version) + ": ${Fixture.NUMBER_VERSION}",
            fontWeight = FontWeight.Light,
            fontSize = 15.sp
        )
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.copy_right),
                fontWeight = FontWeight.Light,
                fontSize = 15.sp
            )
            Icon(
                painter = painterResource(R.drawable.baseline_copyright_24),
                contentDescription = "copyright",
                modifier = Modifier
                    .size(15.dp),
                tint = Color.DarkGray
            )
            Text(
                text = " ${Fixture.year}",
                fontWeight = FontWeight.Light,
                fontSize = 15.sp
            )
        }
    }
}