package org.secuso.pfacore.ui.view.settings.components

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import org.secuso.pfacore.R
import org.secuso.pfacore.model.Setting
import org.secuso.pfacore.model.settings.SettingComposite
import org.secuso.pfacore.model.settings.SettingHierarchy
import org.secuso.pfacore.ui.view.replace
import org.secuso.pfacore.ui.view.settings.InflatableSetting
import org.secuso.pfacore.ui.view.settings.SettingCategory
import org.secuso.pfacore.ui.view.settings.SettingMenu
import org.secuso.ui.view.databinding.PreferenceBasicBinding
import org.secuso.ui.view.databinding.PreferenceCategoryBinding

class SettingsMenuAdapter(private val inflater: LayoutInflater, private val owner: LifecycleOwner, private val openMenu: (SettingMenu) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<SettingHierarchy<InflatableSetting>> = listOf()
        // We want to display the whole menu by using a single recyclerview
        // therefore we want to flatten the hierarchy by treating the category as title only
        // and appending the settings of the category.
        set(value) {
            field = value.map { when(it) {
                is SettingCategory -> mutableListOf<SettingHierarchy<InflatableSetting>>(it).apply { this.addAll(it.settings) }
                else -> listOf(it)
            } }.flatten()
        }

    override fun getItemCount() = items.count()
    override fun getItemViewType(position: Int) = when (items[position]) {
        is SettingCategory -> CATEGORY
        is SettingComposite<InflatableSetting, *>, is SettingMenu -> SETTING
        else -> throw IllegalStateException("Class ${items[position]::class.java} is not a valid setting class")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CATEGORY -> CategoryViewHolder(PreferenceCategoryBinding.inflate(inflater, parent, false))
            SETTING -> SettingViewHolder(PreferenceBasicBinding.inflate(inflater, parent, false))
            else -> throw IllegalStateException("ViewType $viewType is not known")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> holder.binding.text = (items[position] as SettingCategory).name
            is SettingViewHolder -> {
                val setting = when (val item = items[position]) {
                    is SettingComposite<InflatableSetting, *> -> item.setting as InflatableSetting
                    is SettingMenu -> {
                        item.menu.setting as InflatableSetting
                    }
                    else -> throw IllegalStateException("A category cannot contain another category.")
                }
                holder.binding.apply {
                    fun doToggle(state: Boolean? = null) {
                        expanded = state ?: !expanded
                        when (val icon = setting.expandableIcon(expanded)) {
                            null -> toggle.setImageResource(if (expanded) { R.drawable.baseline_expand_less_24} else { R.drawable.baseline_expand_more_24})
                            else -> toggle.setImageResource(icon)
                        }
                        val color = TypedValue().apply {
                            toggle.context.theme.resolveAttribute(org.secuso.ui.view.R.attr.colorOnSurface, this, true)
                        }
                        toggle.setColorFilter(color.data)
                    }

                    title.replace(inflater, owner, setting.title)
                    if (setting.description != null) {
                        description.replace(inflater, owner, setting.description!!)
                    }
                    if (!setting.expandable) {
                        if (setting.action != null) {
                            toggle.replace(inflater, owner, setting.action!!)
                        } else {
                            toggle.setOnClickListener {
                                doToggle()
                                when (val item = items[holder.adapterPosition]) {
                                    is SettingMenu -> openMenu(item)
                                    else -> {}
                                }
                            }
                            doToggle(false)
                        }
                    } else {
                        if (setting.action != null) {
                            action.replace(inflater, owner, setting.action!!)
                            toggle.setOnClickListener {
                                doToggle()
                            }
                            doToggle(false)
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

    companion object {
        val CATEGORY = 1
        val SETTING = 2
    }
}