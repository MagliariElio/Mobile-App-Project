package it.polito.students.showteamdetails.view

import android.media.MediaPlayer
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.students.showteamdetails.CommentCard
import it.polito.students.showteamdetails.R
import it.polito.students.showteamdetails.Utils
import it.polito.students.showteamdetails.entity.Comment
import it.polito.students.showteamdetails.viewmodel.TaskViewModel

@Composable
fun CommentsTask(taskVm: TaskViewModel) {

    if (taskVm.commentListField.editComments.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                stringResource(id = R.string.no_comment),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    val commentSorted =
        taskVm.commentListField.editComments.sortedByDescending { comment: Comment -> comment.dateCreation }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 220.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        commentSorted.forEach { comment ->
            var iCreate = false
            if (Utils.memberAccessed.value.id == comment.createdBy.id) {
                iCreate = true
            }
            CommentCard(
                taskVm = taskVm,
                comment = comment,
                createdByMe = iCreate
            )
            Spacer(modifier = Modifier.height(30.dp))
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewComment(taskVm: TaskViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val mediaPlayerSave: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.save)

    var commentText by remember { mutableStateOf(TextFieldValue()) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            TextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text(stringResource(id = R.string.add_comment)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    Utils.useSafeMediaPlayer(mediaPlayerSave)

                    taskVm.addComment(commentText.text)

                    // Cleans up the text field after comment submission
                    commentText = TextFieldValue()
                    // Hides the keyboard after sending the comment
                    keyboardController?.hide()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = if (taskVm.commentListField.errorNewComment != -1) colorResource(R.color.redError) else Color.Gray, // Colorazione del bordo in base all'errore
                        shape = RoundedCornerShape(8.dp)
                    ),
                isError = taskVm.commentListField.errorNewComment != -1,
                colors = TextFieldDefaults.textFieldColors(
                    disabledTextColor = Color.Gray,
                    errorCursorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
            )

            if (taskVm.commentListField.errorNewComment != -1) {
                Text(
                    text = stringResource(taskVm.commentListField.errorNewComment),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(16.dp, 1.dp),
                    color = colorResource(R.color.redError)
                )
            }

        }
    }
    Spacer(modifier = Modifier.height(40.dp))
}

@Composable
fun CommentModifyCard(taskVm: TaskViewModel, comment: Comment, setModifyComment: () -> Unit) {
    val mediaPlayerSave: MediaPlayer? = Utils.loadMediaPlayer(LocalContext.current, R.raw.save)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp, 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(5.dp))

                Row {
                    Column {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 5.dp),
                            value = taskVm.commentListField.editComment.value,
                            onValueChange = { taskVm.commentListField.editComment.value = it },
                            label = {
                                Text(
                                    stringResource(id = R.string.new_message),
                                    color = colorResource(R.color.darkGray),
                                    fontSize = 16.sp
                                )
                            },
                            //isError = taskVm.commentListField.errorEditComment != -1,
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                                lineHeight = 30.sp
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                autoCorrect = true,
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Send
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxSize(1f)
                        ) {
                            IconButton(
                                onClick = {
                                    Utils.useSafeMediaPlayer(mediaPlayerSave)

                                    if (taskVm.commentListField.editComment.value.isNotEmpty()) {
                                        taskVm.modifyComment(comment)
                                    }

                                    setModifyComment()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_send_24),
                                    contentDescription = stringResource(id = R.string.new_message),
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Unspecified
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
