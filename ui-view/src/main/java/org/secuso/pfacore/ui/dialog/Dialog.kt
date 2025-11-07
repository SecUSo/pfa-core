package org.secuso.pfacore.ui.dialog

import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.secuso.pfacore.R
import org.secuso.pfacore.model.dialog.AbortElseDialog
import org.secuso.pfacore.model.dialog.Dialog
import org.secuso.pfacore.model.dialog.InfoDialog
import org.secuso.pfacore.model.dialog.ValueSelectionDialog

fun InfoDialog.show() {
    MaterialAlertDialogBuilder(context).apply {
        setIcon(icon ?: android.R.drawable.ic_dialog_info)
        setTitle(title())
        setMessage(content())
        setNeutralButton(R.string.okay) { _, _ -> onClose() }

        onShow()
        show()
    }
}

fun AbortElseDialog.show() {
    // To prevent calling onAbort twice (once per abort button and once on dismiss)
    var aborted = false
    MaterialAlertDialogBuilder(context).apply {
        setIcon(icon ?: android.R.drawable.ic_dialog_info)
        setTitle(title())
        setMessage(content())
        setNegativeButton(abortLabel) { _,_ ->
            aborted = true
            onAbort()
        }
        setPositiveButton(acceptLabel) { _,_ -> onElse() }
        if (handleDismiss) {
            setOnDismissListener {
                if (!aborted) {
                    onAbort()
                }
            }
        }
        onShow()
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
    // To prevent calling onAbort twice (once per abort button and once on dismiss)
    var aborted = false
    MaterialAlertDialogBuilder(dialog.context).apply {
        setIcon(dialog.icon ?: android.R.drawable.ic_dialog_info)
        setTitle(title())
        setView(binding.root)
        if (!dialog.required) {
            setNegativeButton(dialog.abortLabel) { _,_ ->
                aborted = true
                dialog.onAbort()
            }
        } else {
            setCancelable(false)
        }
        setPositiveButton(dialog.acceptLabel) { _,_ -> dialog.onConfirmation(extraction(binding)) }
        if (dialog.handleDismiss) {
            setOnDismissListener {
                if (!aborted) {
                    dialog.onAbort()
                }
            }
        }
        dialog.onShow()
        val alertDialog = show()
        val isValid = dialog.isValid()
        isValid.observe(dialog.lifecycleOwner) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = it
        }
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = isValid.value != false
    }
}


