package org.secuso.pfacore.ui.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.secuso.pfacore.R
import org.secuso.pfacore.model.dialog.AbortElseDialog
import org.secuso.pfacore.model.dialog.Dialog
import org.secuso.pfacore.model.dialog.InfoDialog
import org.secuso.pfacore.model.dialog.SelectOptionDialog
import org.secuso.pfacore.model.dialog.SelectOptionDialogDSL
import org.secuso.pfacore.model.dialog.ValueSelectionDialog
import org.secuso.ui.view.databinding.DialogOptionBinding
import org.secuso.ui.view.databinding.DialogOptionItemBinding

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

data class ShowSelectOptionDialog(
    val layoutInflater: LayoutInflater,
    val dialog: SelectOptionDialog
): Dialog by dialog {
    constructor(activity: AppCompatActivity, initializer: SelectOptionDialogDSL)
        : this(activity.layoutInflater, SelectOptionDialog.build(activity, initializer))

    class OptionAdapter(
        val layoutInflater: LayoutInflater,
        val options: List<SelectOptionDialog.Entry>
    ): RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): OptionViewHolder {
            return OptionViewHolder(DialogOptionItemBinding.inflate(layoutInflater, parent, false))
        }

        override fun onBindViewHolder(
            holder: OptionViewHolder,
            position: Int
        ) {
            options[position].let {
                holder.binding.apply {
                    if (it.icon != null) {
                        icon.setImageResource(it.icon!!)
                    }
                    title.text = it.title
                    if (it.description != null) {
                        description.text = it.description
                    } else {
                        description.visibility = View.GONE
                    }
                    root.setOnClickListener { _ -> it.onClick() }
                }
            }
        }

        override fun getItemCount() = options.size

        class OptionViewHolder(val binding: DialogOptionItemBinding): RecyclerView.ViewHolder(binding.root)
    }
}
fun ShowSelectOptionDialog.show() {
    // To prevent calling onAbort twice (once per abort button and once on dismiss)
    var aborted = false
    val binding = DialogOptionBinding.inflate(layoutInflater)
    binding.recyclerView.apply {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = ShowSelectOptionDialog.OptionAdapter(layoutInflater, dialog.entries)
        hasFixedSize()
    }

    MaterialAlertDialogBuilder(context).apply {
        setIcon(dialog.icon ?: android.R.drawable.ic_dialog_info)
        setTitle(title())
        setView(binding.root)
        setNegativeButton(dialog.abortLabel) { _,_ ->
            aborted = true
            dialog.onAbort()
        }
        if (dialog.handleDismiss) {
            setOnDismissListener {
                if (!aborted) {
                    dialog.onAbort()
                }
            }
        }
        dialog.onShow()
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


