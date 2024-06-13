package org.secuso.pfacore.model.dialog

import android.content.Context

interface Dialog {
    val context: Context
    val title: () -> String
}

typealias InfoDialogDSL = InfoDialog.Builder.() -> Unit
data class InfoDialog(
    override val context: Context,
    override val title: () -> String,
    val content: () -> String,
    val onClose: () -> Unit
): Dialog {
    class Builder {
        lateinit var context: Context
        lateinit var title: () -> String
        lateinit var content: () -> String
        var onClose: () -> Unit = { }

        internal fun build() = InfoDialog(context, title, content, onClose)
    }

    companion object {
        fun build(initializer: InfoDialogDSL) = InfoDialog.Builder().apply(initializer).build()
    }
}

typealias AbortElseDialogDSL = AbortElseDialog.Builder.() -> Unit
data class AbortElseDialog(
    override val context: Context,
    override val title: () -> String,
    val content: () -> String,
    val acceptLabel: String,
    val onAbort: () -> Unit,
    val onElse: () -> Unit,
    val handleDismiss: Boolean = true
): Dialog {
    class Builder {
        lateinit var context: Context
        lateinit var title: () -> String
        lateinit var content: () -> String
        lateinit var acceptLabel: String
        var onAbort: () -> Unit = { }
        var onElse: () -> Unit = { }
        var handleDismiss: Boolean = true

        internal fun build() = AbortElseDialog(context, title, content, acceptLabel, onAbort, onElse, handleDismiss)
    }

    companion object {
        fun build(initializer: AbortElseDialogDSL) = AbortElseDialog.Builder().apply(initializer).build()
    }
}

typealias ValueSelectionDialogDSL<T> = ValueSelectionDialog.Builder<T>.() -> Unit
data class ValueSelectionDialog<T>(
    override val context: Context,
    override val title: () -> String,
    val acceptLabel: String,
    val onAbort: () -> Unit,
    val onConfirmation: (T) -> Unit,
    val handleDismiss: Boolean = true
): Dialog {
    class Builder<T> {
        lateinit var context: Context
        lateinit var title: () -> String
        lateinit var acceptLabel: String
        lateinit var onConfirmation: (T) -> Unit
        var onAbort: () -> Unit = { }
        var handleDismiss: Boolean = true

        internal fun build() = ValueSelectionDialog(context, title, acceptLabel, onAbort, onConfirmation, handleDismiss)
    }

    companion object {
        fun <T> build(initializer: ValueSelectionDialogDSL<T>) = ValueSelectionDialog.Builder<T>().apply(initializer).build()
    }
}
