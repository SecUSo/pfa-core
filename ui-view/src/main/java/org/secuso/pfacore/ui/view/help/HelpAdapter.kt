package org.secuso.pfacore.ui.view.help

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.secuso.ui.view.databinding.SimpleExpandableItemBinding
import org.secuso.pfacore.ui.view.replace

class HelpAdapter(private val items: List<HelpData>, private val inflater: LayoutInflater): RecyclerView.Adapter<HelpAdapter.ViewHolder>() {

    override fun getItemCount() = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SimpleExpandableItemBinding.inflate(inflater, parent, true))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title.replace(inflater, items[position].title)
        holder.binding.description.replace(inflater, items[position].summary)
    }

    class ViewHolder(val binding: SimpleExpandableItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.toggle.setOnClickListener { binding.expanded = !binding.expanded }
        }
    }
}