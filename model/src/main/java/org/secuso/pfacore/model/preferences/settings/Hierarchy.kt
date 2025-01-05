package org.secuso.pfacore.model.preferences.settings

import org.secuso.pfacore.model.preferences.Info

/**
 * The setting menu is intended to be a Composite, meaning that a [SettingMenu] contains one or many [SettingCategory],
 * a [SettingCategory] contains one or many [SettingComposite] whilst a [SettingComposite] may either be a normal setting or a setting containing another [SettingMenu].
 *
 * @param SI The data a single setting may hold.
 *
 * @author Patrick Schneider
 */
interface SettingHierarchy<SI: Info> {
    /**
     * Retrieve all elements in the hierarchy directly below this element.
     */
    fun all(): List<SettingHierarchy<SI>>

    /**
     * Retrieve all settings below this element.
     */
    fun allSettings(): List<SettingComposite<SI, *>>
}

/**
 * A [SettingComposite] as a direct child of a [SettingCategory] has to have a displayable [Setting].
 *
 * @author Patrick Schneider
 */
interface CategoricalSettingHierarchy<SI: Info> : SettingHierarchy<SI> {
    fun setting(): Setting<*>
}

/**
 * This class is the direct child in the setting hierarchy of a [SettingCategory] and represents a single setting.
 *
 * @author Patrick Schneider
 */
open class SettingComposite<SI: Info, S: Setting<SI>>(
    val setting: S,
) : CategoricalSettingHierarchy<SI>, Setting<SI> by setting {

    override fun setting(): S = setting
    override fun all(): List<SettingComposite<SI, S>> = listOf(this)
    override fun allSettings(): List<SettingComposite<SI, S>> = listOf(this)

}

/**
 * This class is the direct child in the setting hierarchy of a [SettingMenu] and represents a category.
 * A category has a name and a set of settings.
 *
 * @author Patrick Schneider
 */
open class SettingCategory<SI: Info>(
    val name: String,
    val settings: List<CategoricalSettingHierarchy<SI>>
) : SettingHierarchy<SI> {
    override fun all() = settings
    override fun allSettings(): List<SettingComposite<SI, *>> = settings.map { it.allSettings() }.flatten()
}

/**
 * This class represents a [SettingComposite] which shall open a new setting screen if clicked..
 *
 * @author Patrick Schneider
 */
open class SettingMenu<SI: Info, SC: SettingCategory<SI>>(
    val name: String,
    val setting: SettingComposite<SI, *>,
    val settings: List<SC>
) : CategoricalSettingHierarchy<SI> {
    override fun setting() = setting.setting()
    override fun all() = settings
    override fun allSettings(): List<SettingComposite<SI, *>> = settings.map { it.allSettings() }.flatten()
}