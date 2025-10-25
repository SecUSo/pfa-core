package org.secuso.pfacore.model.dialog

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * A Dialog consist at least of a title.
 * Note that the specific content of a dialog is dependent on its kind and will be handled by the dialog itself.
 *
 * @author Patrick Schneider
 */
interface Dialog {
    val context: Context
    val title: () -> String
}

typealias InfoDialogDSL = InfoDialog.Builder.() -> Unit

/**
 * A dialog which is intended to show a helpful information which can be acknowledged.
 * To show the dialog, some additional calls may be needed depending on the ui implementation.
 *
 * Intended Usage:
 *
 *      val dialog = InfoDialog.build(context) {
 *          title { "super important title, x: $x" }
 *          content { "As expected, x is indeed $x" }
 *          onClose { doSomethingOnClose }
 *      }
 *      dialog.show()
 *
 * @author Patrick Schneider
 */
data class InfoDialog(
    override val context: Context,
    override val title: () -> String,
    val content: () -> String,
    val onClose: () -> Unit
): Dialog {
    class Builder(var context: Context) {
        lateinit var title: () -> String
        lateinit var content: () -> String
        var onClose: () -> Unit = { }

        internal fun build() = InfoDialog(context, title, content, onClose)
    }

    companion object {
        fun build(context: Context, initializer: InfoDialogDSL) = InfoDialog.Builder(context).apply(initializer).build()
    }
}

typealias AbortElseDialogDSL = AbortElseDialog.Builder.() -> Unit

/**
 * A dialog which is intended to interact with the user in a Yes/No way.
 * To show the dialog, some additional calls may be needed depending on the ui implementation.
 *
 * Intended Usage:
 *
 *      val dialog = AbortElseDialog.build(context) {
 *          title { "super important title, x: $x" }
 *          content { "Do you want to continue and delete all those entries? \n entries: ${entries.joinToString { ',' } }" }
 *          acceptLabel = "Yes, please delete"
 *          onAbort { doNotDelete() }
 *          onElse { delete() }
 *      }
 *      dialog.show()
 *
 * @param onElse Will be executed if the user clicks on the positive button acknowledging the intention of the dialog.
 * @param onAbort Will be executed if the user clicks on the abort button.
 * @param handleDismiss Shall the dialog being closed by dismissal (return, touch outside, ...) be treated as abort?
 *
 * @author Patrick Schneider
 */
data class AbortElseDialog(
    override val context: Context,
    override val title: () -> String,
    val content: () -> String,
    val acceptLabel: String,
    val abortLabel: String,
    val onAbort: () -> Unit,
    val onElse: () -> Unit,
    val handleDismiss: Boolean = true
): Dialog {
    class Builder(var context: Context) {
        lateinit var title: () -> String
        lateinit var content: () -> String
        var acceptLabel: String = ContextCompat.getString(context, android.R.string.ok)
        var abortLabel: String = ContextCompat.getString(context, android.R.string.cancel)
        var onAbort: () -> Unit = { }
        var onElse: () -> Unit = { }
        var handleDismiss: Boolean = true

        internal fun build() = AbortElseDialog(context, title, content, acceptLabel, abortLabel, onAbort, onElse, handleDismiss)
    }

    companion object {
        fun build(context: Context, initializer: AbortElseDialogDSL) = AbortElseDialog.Builder(context).apply(initializer).build()
    }
}

typealias ValueSelectionDialogDSL<T> = ValueSelectionDialog.Builder<T>.() -> Unit

/**
 * A dialog which is intended to interact with the user to give an option to select some value.
 * Note that the content is dependent on the ui implementation and must be supplied given the ui implementation.
 * To show the dialog, some additional calls may be needed depending on the ui implementation.
 *
 * Intended Usage:
 *
 *      val dialog = ValueSelectionDialog.build(context) {
 *          title { "super important title, x: $x" }
 *          acceptLabel = "Select it"
 *          onAbort { resetUi() }
 *          onConfirmation { setValue(it) }
 *      }
 *      // supply the content via the dialog ui implementation.
 *      dialog.show()
 *
 * @param onConfirmation Will be executed if the user clicks on the positive button acknowledging the selection of the dialog.
 * @param onAbort Will be executed if the user clicks on the abort button.
 * @param handleDismiss Shall the dialog being closed by dismissal (return, touch outside, ...) be treated as abort?
 *
 * @author Patrick Schneider
 */
data class ValueSelectionDialog<T>(
    override val context: Context,
    override val title: () -> String,
    val lifecycleOwner: LifecycleOwner,
    val acceptLabel: String,
    val abortLabel: String,
    val onAbort: () -> Unit,
    val isValid: () -> LiveData<Boolean>,
    val onConfirmation: (T) -> Unit,
    val handleDismiss: Boolean = true
): Dialog {
    class Builder<T>(var context: Context) {
        lateinit var title: () -> String
        lateinit var acceptLabel: String
        lateinit var onConfirmation: (T) -> Unit
        lateinit var lifecycleOwner: LifecycleOwner
        var abortLabel: String = ContextCompat.getString(context, android.R.string.cancel)
        var onAbort: () -> Unit = { }
        var handleDismiss: Boolean = true
        var isValid = { MutableLiveData(true) }

        internal fun build() = ValueSelectionDialog(
            context,
            title,
            lifecycleOwner,
            acceptLabel,
            abortLabel,
            onAbort,
            isValid,
            onConfirmation,
            handleDismiss
        )
    }

    companion object {
        fun <T> build(context: Context, initializer: ValueSelectionDialogDSL<T>) = ValueSelectionDialog.Builder<T>(context).apply(initializer).build()
        fun <T> build(activity: AppCompatActivity, initializer: ValueSelectionDialogDSL<T>) = ValueSelectionDialog.Builder<T>(activity)
            .apply(initializer)
            .apply {
                lifecycleOwner = activity
            }.build()
    }
}
