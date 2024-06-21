package org.secuso.pfacore.ui.view.dialog

import androidx.databinding.ViewDataBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.secuso.pfacore.R
import org.secuso.pfacore.model.dialog.AbortElseDialog
import org.secuso.pfacore.model.dialog.Dialog
import org.secuso.pfacore.model.dialog.InfoDialog
import org.secuso.pfacore.model.dialog.ValueSelectionDialog

fun InfoDialog.show() {
    MaterialAlertDialogBuilder(context)
        .setIcon(android.R.drawable.ic_dialog_info)
        .setTitle(title())
        .setMessage(content())
        .setNeutralButton(R.string.okay) { _, _ -> onClose() }
        .show()
}

fun AbortElseDialog.show() {
    MaterialAlertDialogBuilder(context).apply {
        setIcon(android.R.drawable.ic_dialog_info)
        setTitle(title())
        setMessage(content())
        setNegativeButton(android.R.string.cancel) { _,_ -> onAbort() }
        setPositiveButton(acceptLabel) { _,_ -> onElse() }
        if (handleDismiss) {
            setOnDismissListener { onAbort() }
        }
        show()
    }
}

data class ShowValueSelectionDialog<T, B: ViewDataBinding>(
    val binding: B,
    val extraction: (B) -> T,
    val dialog: ValueSelectionDialog<T>
): Dialog by dialog
fun <T,B: ViewDataBinding> ValueSelectionDialog<T>.content(binding: B, extraction: (B) -> T) = ShowValueSelectionDialog<T, B>(binding, extraction, this@content)

fun <T, B: ViewDataBinding> ShowValueSelectionDialog<T, B>.show() {
    MaterialAlertDialogBuilder(context).apply {
        setIcon(android.R.drawable.ic_dialog_info)
        setTitle(title())
        setView(binding.root)
        setNegativeButton(android.R.string.cancel) { _,_ -> dialog.onAbort() }
        setPositiveButton(dialog.acceptLabel) { _,_ -> dialog.onConfirmation(extraction(binding)) }
        if (dialog.handleDismiss) {
            setOnDismissListener { dialog.onAbort() }
        }
        show()
    }
}


