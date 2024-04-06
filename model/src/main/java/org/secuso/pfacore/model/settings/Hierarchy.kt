package org.secuso.pfacore.model.settings

import android.util.JsonReader
import org.secuso.pfacore.model.SettingInfo

interface SettingHierarchy<S : SettingComposite<*>> {
    fun all(): List<S>
}

open class SettingComposite<SI: SettingInfo>(
    val data: SI,
) : SettingHierarchy<SettingComposite<SI>> {

    override fun all(): List<SettingComposite<SI>> {
        return listOf(this)
    }
}

open class SettingCategory<SI: SettingInfo, S : SettingComposite<SI>>(
    val name: String,
    val settings: List<SettingHierarchy<S>>
) : SettingHierarchy<S> {
    override fun all() = settings.map { it.all() }.flatten()
}

open class SettingMenu<SI: SettingInfo, S : SettingComposite<SI>>(
    val name: String,
    val settings: List<SettingHierarchy<S>>
) : SettingHierarchy<S> {
    override fun all() = settings.map { it.all() }.flatten()
}