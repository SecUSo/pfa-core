package org.secuso.pfacore.ui.view.settings.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import org.secuso.pfacore.model.SettingEntry
import org.secuso.pfacore.ui.view.help.HelpData
import org.secuso.pfacore.ui.view.replace
import org.secuso.ui.view.databinding.PreferenceActionRadioItemBinding

class RadioAdapter<T>(private val inflater: LayoutInflater, private val items: List<SettingEntry<T>>, active: T? = null, private val onClick: (T) -> Unit): RecyclerView.Adapter<RadioAdapter.ViewHolder>() {
    private var selected: Int = items.indexOfFirst { active == it.value }
    private var selectedHolder: ViewHolder? = null

    override fun getItemCount() = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PreferenceActionRadioItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == selected) {
            selectedHolder = holder
        }
        holder.binding.checked = (position == selected)
        holder.binding.text = items[position].entry
        holder.binding.element.setOnClickListener {
            holder.binding.checked = false
            selected = holder.adapterPosition
            selectedHolder = holder
            holder.binding.checked = true
            onClick(items[holder.adapterPosition].value)
        }
    }

    class ViewHolder(val binding: PreferenceActionRadioItemBinding): RecyclerView.ViewHolder(binding.root)
}
