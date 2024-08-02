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

@Composable
fun InfoDialog.Show() {
    val showDialog = remember { mutableStateOf(true) }
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
}

@Composable
fun AbortElseDialog.Show() {
    val showDialog = remember { mutableStateOf(true) }
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
                    Text(text = stringResource(android.R.string.cancel))
                }
            },
            properties = DialogProperties(dismissOnClickOutside = handleDismiss, dismissOnBackPress = handleDismiss)
        )
    }
}

typealias ValueSelectionDialogContent<T> = @Composable (onSelected: (T) -> Unit) -> Unit
data class ShowValueSelectionDialog<T>(
    val content: ValueSelectionDialogContent<T>,
    val dialog: ValueSelectionDialog<T>
): Dialog by dialog
fun <T> ValueSelectionDialog<T>.content(content: ValueSelectionDialogContent<T>) = ShowValueSelectionDialog<T>(content, this@content)

@Composable
fun <T> ShowValueSelectionDialog<T>.Show() {
    val selectedValue = remember { mutableStateOf<T?>(null) }
    val showDialog = remember { mutableStateOf(true) }
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
                    Text(text = stringResource(android.R.string.cancel))
                }
            },
            properties = DialogProperties(dismissOnClickOutside = dialog.handleDismiss, dismissOnBackPress = dialog.handleDismiss)
        )
    }
}

@Composable
@Preview
fun InfoDialogPreview() {
    val ctx = LocalContext.current
    InfoDialog.build {
        title = { "Test Title" }
        content = { "This is a long and good content describing the action happening to invoke this." }
        context = ctx
    }.Show()
}

@Composable
@Preview
fun AbortElseDialogPreview() {
    val ctx = LocalContext.current
    AbortElseDialog.build {
        title = { "Test Title" }
        content = { "This is a long and good content explaining a decision the user have to made."}
        context = ctx
        acceptLabel = "Just Do It!"
    }.Show()
}

@Composable
@Preview
fun ShowValueSelectionDialogPreview() {
    val ctx = LocalContext.current
    ValueSelectionDialog.build<String> {
        title = { "Test Title" }
        context = ctx
        acceptLabel = "Just Do It!"
        onConfirmation = { Log.i("ValueSelectionDialog", it) }
    }.content { update ->
        val state = remember { mutableStateOf("") }
        TextField(state.value, { update(it); state.value = it })
    }.Show()
}