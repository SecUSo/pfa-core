package org.secuso.pfacore.model.preferences.settings

interface SettingHierarchy<SI: SettingInfo> {
    fun all(): List<SettingHierarchy<SI>>
    fun allSettings(): List<SettingComposite<SI, *>>
}

interface CategoricalSettingHierarchy<SI: SettingInfo> : SettingHierarchy<SI> {
    fun setting(): Setting<*>
}

open class SettingComposite<SI: SettingInfo, S: Setting<SI>>(
    val setting: S,
) : CategoricalSettingHierarchy<SI>, Setting<SI> by setting {

    override fun setting(): S = setting
    override fun all(): List<SettingComposite<SI, S>> = listOf(this)
    override fun allSettings(): List<SettingComposite<SI, S>> = listOf(this)

}

open class SettingCategory<SI: SettingInfo>(
    val name: String,
    val settings: List<CategoricalSettingHierarchy<SI>>
) : SettingHierarchy<SI> {
    override fun all() = settings
    override fun allSettings(): List<SettingComposite<SI, *>> = settings.map { it.allSettings() }.flatten()
}

open class SettingMenu<SI: SettingInfo, SC: SettingCategory<SI>>(
    val name: String,
    val setting: SettingComposite<SI, *>,
    val settings: List<SC>
) : CategoricalSettingHierarchy<SI> {
    override fun setting() = setting.setting
    override fun all() = settings
    override fun allSettings(): List<SettingComposite<SI, *>> = settings.map { it.allSettings() }.flatten()
}