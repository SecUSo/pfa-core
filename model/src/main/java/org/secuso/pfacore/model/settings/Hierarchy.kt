package org.secuso.pfacore.model.settings

import org.secuso.pfacore.model.Setting
import org.secuso.pfacore.model.SettingInfo

interface SettingHierarchy<SI: SettingInfo> {
    fun all(): List<SettingHierarchy<SI>>
    fun allSettings(): List<SettingComposite<SI, *>>
}

open class SettingComposite<SI: SettingInfo, S: Setting<SI>>(
    val setting: S,
) : SettingHierarchy<SI> {

    override fun all(): List<SettingComposite<SI, S>> = listOf(this)
    override fun allSettings(): List<SettingComposite<SI, S>> = listOf(this)

}

open class SettingCategory<SI: SettingInfo>(
    val name: String,
    val settings: List<SettingHierarchy<SI>>
) : SettingHierarchy<SI> {
    override fun all() = settings
    override fun allSettings(): List<SettingComposite<SI, *>> = settings.map { it.allSettings() }.flatten()
}

open class SettingMenu<SI: SettingInfo>(
    val name: String,
    val settings: List<SettingCategory<SI>>
) : SettingHierarchy<SI> {
    override fun all() = settings
    override fun allSettings(): List<SettingComposite<SI, *>> = settings.map { it.allSettings() }.flatten()
}