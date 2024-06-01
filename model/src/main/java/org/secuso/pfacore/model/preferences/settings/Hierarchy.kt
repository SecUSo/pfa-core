package org.secuso.pfacore.model.preferences.settings

import org.secuso.pfacore.model.preferences.Info

interface SettingHierarchy<SI: Info> {
    fun all(): List<SettingHierarchy<SI>>
    fun allSettings(): List<SettingComposite<SI, *>>
}

interface CategoricalSettingHierarchy<SI: Info> : SettingHierarchy<SI> {
    fun setting(): Setting<*>
}

open class SettingComposite<SI: Info, S: Setting<SI>>(
    val setting: S,
) : CategoricalSettingHierarchy<SI>, Setting<SI> by setting {

    override fun setting(): S = setting
    override fun all(): List<SettingComposite<SI, S>> = listOf(this)
    override fun allSettings(): List<SettingComposite<SI, S>> = listOf(this)

}

open class SettingCategory<SI: Info>(
    val name: String,
    val settings: List<CategoricalSettingHierarchy<SI>>
) : SettingHierarchy<SI> {
    override fun all() = settings
    override fun allSettings(): List<SettingComposite<SI, *>> = settings.map { it.allSettings() }.flatten()
}

open class SettingMenu<SI: Info, SC: SettingCategory<SI>>(
    val name: String,
    val setting: SettingComposite<SI, *>,
    val settings: List<SC>
) : CategoricalSettingHierarchy<SI> {
    override fun setting() = setting.setting
    override fun all() = settings
    override fun allSettings(): List<SettingComposite<SI, *>> = settings.map { it.allSettings() }.flatten()
}