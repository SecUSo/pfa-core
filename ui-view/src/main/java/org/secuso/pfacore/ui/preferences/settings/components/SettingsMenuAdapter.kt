package org.secuso.pfacore.ui.preferences.settings.components

import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import org.secuso.pfacore.R
import org.secuso.pfacore.model.preferences.settings.SettingCategory
import org.secuso.pfacore.model.preferences.settings.SettingComposite
import org.secuso.pfacore.model.preferences.settings.SettingHierarchy
import org.secuso.pfacore.model.preferences.settings.SettingMenu
import org.secuso.pfacore.ui.replace
import org.secuso.pfacore.ui.preferences.settings.InflatableSettingInfo
import org.secuso.pfacore.ui.preferences.settings.InflatableSettingMenu
import org.secuso.ui.view.databinding.PreferenceBasicBinding
import org.secuso.ui.view.databinding.PreferenceCategoryBinding

fun View.addClickStyle() {
    // resolve the selectableItemBackground drawable such that there is a nice visual feedback
    val typedValue = TypedValue()
    context.theme.resolveAttribute(
        com.google.android.material.R.attr.selectableItemBackground,
        typedValue,
        true
    )

    val drawable = AppCompatResources.getDrawable(context, typedValue.resourceId)
    background = drawable
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        foreground = drawable?.constantState?.newDrawable()?.mutate()
    }
}

class SettingsMenuAdapter(
    private val activity: AppCompatActivity,
    private val inflater: LayoutInflater,
    private val owner: LifecycleOwner,
    private val openMenu: (InflatableSettingMenu) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<SettingHierarchy<InflatableSettingInfo>> = listOf()
        // We want to display the whole menu by using a single recyclerview
        // therefore we want to flatten the hierarchy by treating the category as title only
        // and appending the settings of the category.
        set(value) {
            field = value.map { when(it) {
                is SettingCategory -> mutableListOf<SettingHierarchy<InflatableSettingInfo>>(it).apply { this.addAll(it.settings) }
                else -> listOf(it)
            } }.flatten()
        }

    override fun getItemCount() = items.count()
    override fun getItemViewType(position: Int) = when (items[position]) {
        is SettingCategory -> CATEGORY
        is SettingComposite<InflatableSettingInfo, *>, is SettingMenu<*,*> -> SETTING
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
        /* temporary fix
           recyclerview normally recycles the view holder and keeps the binding valid
           but as we replace the views of the binding, and we cannot alter the binding itself,
           a reuse of a view holder will lead to issues like the usage of false titles, descriptions, toggles, ...

           Telling recyclerview to not recycle the view holder is an easy, but rather expensive fix.
           But as we don't expect there to be any circumstance where hundreds of settings need to be rendered in a second
           this should be acceptable until the logic is more refined.*/
//        holder.setIsRecyclable(false)
        when (holder) {
            is CategoryViewHolder -> holder.binding.text = (items[position] as SettingCategory).name
            is SettingViewHolder -> {
                val setting = when (val item = items[position]) {
                    is SettingComposite<InflatableSettingInfo, *> -> item.setting as InflatableSettingInfo
                    is SettingMenu<*,*> -> {
                        item.setting() as InflatableSettingInfo
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
                            toggle.context.theme.resolveAttribute(R.attr.colorOnSurface, this, true)
                        }
                        toggle.setColorFilter(color.data)
                    }


                    val titleView = title.replace(inflater, owner, setting.title)
                    val descriptionView = if (setting.description != null) {
                        description.replace(inflater, owner, setting.description!!)
                    } else {
                        null
                    }

                    val rootClick = if (setting.onlyRootExpandable()) { holder.binding.top } else { holder.binding.root }
                    enabled = setting.enabled.value ?: true
                    rootClick.isClickable = true
                    setting.enabled.observe(owner) {
                        enabled = it
                        if (enabled) {
                            rootClick.apply {
                                isFocusable = true
                                isClickable = true
                                addClickStyle()
                            }
                            root.isEnabled = true
                            action.isEnabled = true
                            titleView.isEnabled = true
                            descriptionView?.isEnabled = true
                        } else {
                            root.isEnabled = false
                            action.isEnabled = false
                            titleView.isEnabled = false
                            descriptionView?.isEnabled = false
                        }

                    }

                    if (!setting.expandable) {
                        if (setting.action != null) {
                            val view = toggle.replace(inflater, owner, setting.action!!)
                            rootClick.setOnClickListener { view.callOnClick() }
                        } else if (setting.onClick != null) {
                            rootClick.setOnClickListener {
                                Log.d("click", "click")
                                setting.onClick?.invoke(activity)
                            }
                        } else {
                            // Item is toggleable, therefore
                            toggle.setOnClickListener {
                                doToggle()
                                when (val item = items[holder.adapterPosition]) {
                                    is SettingMenu<*,*> -> {
                                        openMenu(item as InflatableSettingMenu)
                                    }
                                    else -> {}
                                }
                            }
                            doToggle(false)
                            rootClick.setOnClickListener {
                                toggle.callOnClick()
                            }
                        }
                    }  else {
                        if (setting.action != null) {
                            action.replace(inflater, owner, setting.action!!)
                            toggle.setOnClickListener {
                                doToggle()
                            }
                            doToggle(false)
                            rootClick.setOnClickListener {
                                toggle.callOnClick()
                            }
                        }
                    }
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