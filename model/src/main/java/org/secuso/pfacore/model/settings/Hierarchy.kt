package org.secuso.pfacore.model.settings

import android.util.JsonReader
import org.secuso.pfacore.backup.Restorer

interface SettingHierarchy<S : ISetting<*, S>> {
    fun all(): List<S>
}

interface ISetting<T, S : ISetting<T, S>> : SettingHierarchy<S> {
    val data: SettingData<T>
    val backup: Boolean

    fun restore(reader: JsonReader)
}

open class Setting<T, S : ISetting<T, S>>(
    override val data: SettingData<T>,
    override val backup: Boolean,
    private val restorer: Restorer<T>
) : ISetting<T, S> {
    override fun restore(reader: JsonReader) {
        data.state.value = restorer(reader)
    }

    override fun all(): List<S> {
        return listOf(this as S)
    }
}

open class SettingCategory<S : ISetting<*, S>>(
    val name: String,
    val settings: List<SettingHierarchy<S>>
) : SettingHierarchy<S> {
    override fun all() = settings.map { it.all() }.flatten()
}

open class SettingMenu<S : ISetting<*, S>>(
    val name: String,
    val settings: List<SettingHierarchy<S>>
) : SettingHierarchy<S> {
    override fun all() = settings.map { it.all() }.flatten()
}

abstract class SwitchSetting<S : ISetting<Boolean, S>>(data: SettingData<Boolean>, backup: Boolean, restorer: Restorer<Boolean>) : Setting<Boolean, S>(data, backup, restorer)
abstract class RadioSetting<T, S : ISetting<T, S>>(data: SettingData<T>, backup: Boolean, restorer: Restorer<T>) : Setting<T, S>(data, backup, restorer)
