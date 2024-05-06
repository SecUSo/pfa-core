package org.secuso.pfacore.ui.view.settings.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import org.secuso.pfacore.model.settings.SettingComposite
import org.secuso.pfacore.model.settings.SettingHierarchy
import org.secuso.pfacore.ui.view.replace
import org.secuso.pfacore.ui.view.settings.InflatableSetting
import org.secuso.pfacore.ui.view.settings.SettingCategory
import org.secuso.pfacore.ui.view.settings.SettingMenu
import org.secuso.ui.view.databinding.PreferenceBasicBinding
import org.secuso.ui.view.databinding.PreferenceCategoryBinding

class SettingsMenuAdapter(private val inflater: LayoutInflater, private val owner: LifecycleOwner, private val openMenu: (SettingMenu) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<SettingHierarchy<SettingComposite<InflatableSetting>>> = listOf()
        // We want to display the whole menu by using a single recyclerview
        // therefore we want to flatten the hierarchy by treating the category as title only
        // and appending the settings of the category.
        set(value) {
            field = value.map { when(it) {
                is SettingCategory -> mutableListOf<SettingHierarchy<SettingComposite<InflatableSetting>>>(it).apply { this.addAll(it.settings) }
                else -> listOf(it)
            } }.flatten()
        }

    override fun getItemCount() = items.count()
    override fun getItemViewType(position: Int) = items[position]::class.hashCode()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SettingCategory::class.hashCode() -> CategoryViewHolder(PreferenceCategoryBinding.inflate(inflater, parent, false))
            SettingMenu::class.hashCode(),
            SettingComposite::class.hashCode() -> SettingViewHolder(PreferenceBasicBinding.inflate(inflater, parent, false))
            else -> throw IllegalStateException("ViewType $viewType is not known")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> holder.binding.text = (items[position] as SettingCategory).name
            is SettingViewHolder -> {
                val setting = when (val item = items[position]) {
                    is SettingComposite -> item as InflatableSetting
                    is SettingMenu -> {
                        item.menu.setting as InflatableSetting
                    }
                    else -> throw IllegalStateException("A category cannot contain another category.")
                }
                holder.binding.apply {
                    title.replace(inflater, owner, setting.title)
                    if (setting.description != null) {
                        description.replace(inflater, owner, setting.description!!)
                    }
                    if (!setting.expandable) {
                        if (setting.action != null) {
                            toggle.replace(inflater, owner, setting.action!!)
                        } else {
                            toggle.setOnClickListener {
                                expanded = !expanded
                                when (val icon = setting.expandableIcon(expanded)) {
                                    null -> {}
                                    else -> toggle.setImageResource(icon)
                                }
                                when (val item = items[holder.adapterPosition]) {
                                    is SettingMenu -> openMenu(item)
                                    else -> {}
                                }
                            }
                            expanded = false
                            when (val icon = setting.expandableIcon(expanded)) {
                                null -> {}
                                else -> toggle.setImageResource(icon)
                            }
                        }
                    } else {
                        if (setting.action != null) {
                            action.replace(inflater, owner, setting.action!!)
                            toggle.setOnClickListener {
                                expanded = !expanded
                                when (val icon = setting.expandableIcon(expanded)) {
                                    null -> {}
                                    else -> toggle.setImageResource(icon)
                                }
                            }
                            expanded = false
                            when (val icon = setting.expandableIcon(expanded)) {
                                null -> {}
                                else -> toggle.setImageResource(icon)
                            }
                        }
                    }

                    setting.enabled.observe(owner) { enabled = it}
                    enabled = setting.enabled.value ?: true
                }
            }
        }
    }

    class CategoryViewHolder(val binding: PreferenceCategoryBinding): RecyclerView.ViewHolder(binding.root)
    class SettingViewHolder(val binding: PreferenceBasicBinding): RecyclerView.ViewHolder(binding.root)
}