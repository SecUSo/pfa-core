package org.secuso.pfacore.ui.dialog

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import org.secuso.pfacore.R
import org.secuso.pfacore.model.dialog.AbortElseDialog
import org.secuso.pfacore.model.dialog.Dialog
import org.secuso.pfacore.model.dialog.InfoDialog
import org.secuso.pfacore.model.dialog.ValueSelectionDialog

fun interface DialogHandle {
    fun show()
}

@Composable
fun InfoDialog.register(): DialogHandle {
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { onClose(); showDialog.value = false },
            title = { Text(text = title(), style = MaterialTheme.typography.headlineMedium) },
            text = { Text(text = content()) },
            confirmButton = {
                TextButton(onClick = { onClose(); showDialog.value = false }) {
                    Text(text = stringResource(R.string.okay))
                }
            }
        )
    }
    return DialogHandle { showDialog.value = true }
}

@Composable
fun AbortElseDialog.register(): DialogHandle {
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { onAbort(); showDialog.value = false },
            title = { Text(text = title(), style = MaterialTheme.typography.headlineMedium) },
            text = { Text(text = content()) },
            confirmButton = {
                TextButton(onClick = { onElse(); showDialog.value = false }) {
                    Text(text = acceptLabel)
                }
            },
            dismissButton = {
                TextButton(onClick = { onAbort(); showDialog.value = false }) {
                    Text(text = abortLabel)
                }
            },
            properties = DialogProperties(dismissOnClickOutside = handleDismiss, dismissOnBackPress = handleDismiss)
        )
    }
    return DialogHandle { showDialog.value = true }
}

typealias ValueSelectionDialogContent<T> = @Composable (onSelected: (T) -> Unit) -> Unit
data class ShowValueSelectionDialog<T>(
    val content: ValueSelectionDialogContent<T>,
    val dialog: ValueSelectionDialog<T>
): Dialog by dialog
fun <T> ValueSelectionDialog<T>.content(content: ValueSelectionDialogContent<T>) = ShowValueSelectionDialog<T>(content, this@content)

@Composable
fun <T> ShowValueSelectionDialog<T>.register(): DialogHandle {
    val selectedValue = remember { mutableStateOf<T?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { dialog.onAbort(); showDialog.value = false },
            title = { Text(text = dialog.title(), style = MaterialTheme.typography.headlineMedium) },
            text = { content { selectedValue.value = it} },
            confirmButton = {
                TextButton(
                    onClick = { dialog.onConfirmation(selectedValue.value!!); showDialog.value = false },
                    enabled = selectedValue.value != null
                ) {
                    Text(text = dialog.acceptLabel)
                }
            },
            dismissButton = {
                TextButton(onClick = { dialog.onAbort(); showDialog.value = false }) {
                    Text(text = dialog.abortLabel)
                }
            },
            properties = DialogProperties(dismissOnClickOutside = dialog.handleDismiss, dismissOnBackPress = dialog.handleDismiss)
        )
    }
    return DialogHandle { showDialog.value = true }
}

@Composable
@Preview
fun InfoDialogPreview() {
    InfoDialog.build(LocalContext.current) {
        title = { "Test Title" }
        content = { "This is a long and good content describing the action happening to invoke this." }
    }.register().show()
}

@Composable
@Preview
fun AbortElseDialogPreview() {
    AbortElseDialog.build(LocalContext.current) {
        title = { "Test Title" }
        content = { "This is a long and good content explaining a decision the user have to made."}
        acceptLabel = "Just Do It!"
    }.register().show()
}

@Composable
@Preview
fun ShowValueSelectionDialogPreview() {
    ValueSelectionDialog.build<String>(LocalContext.current) {
        title = { "Test Title" }
        acceptLabel = "Just Do It!"
        onConfirmation = { Log.i("ValueSelectionDialog", it) }
    }.content { update ->
        val state = remember { mutableStateOf("") }
        TextField(state.value, { update(it); state.value = it })
    }.register().show()
}