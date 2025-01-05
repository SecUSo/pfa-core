package org.secuso.pfacore.model.preferences.settings

import android.content.res.Resources
import org.secuso.pfacore.model.preferences.InfoFactory
import org.secuso.pfacore.model.preferences.BuildInfo
import org.secuso.pfacore.model.preferences.Info

/**
 * A DSL-Builder to build a list of [SettingEntry].
 *
 * @author Patrick Schneider
 */
class Entries<T>(
    private val resources: Resources,
    private var entries: List<String>? = null,
    private var values: List<T>? = null
) {
    fun collect() = entries!!.zip(values!!).map { (entry, value) -> SettingEntry(entry, value) }.toList()

    @Suppress("Unused")
    fun entries(entries: List<String>) {
        this.entries = entries
    }

    @Suppress("Unused")
    fun entries(id: Int) {
        this.entries = resources.getStringArray(id).toList()
    }

    @Suppress("Unused")
    fun values(values: List<T>) {
        this.values = values
    }
}

/**
 * A bare switch setting without any information to be displayed.
 * Those will be added in the `ui-*` libraries.
 *
 * @author Patrick Schneider
 */
abstract class SwitchSetting<SD: SwitchSetting.SwitchData>(override val data: SD): Setting<SD> {
    open class SwitchData(val data: SettingData<Boolean>): ISettingData<Boolean> by data
    interface SwitchBuildInfo: ISettingDataBuildInfo<Boolean>
    companion object {
        fun <SI: SwitchBuildInfo, SD: SwitchData> factory(adapt: (SI, SwitchData) -> SD): SettingFactory<SI, SD>
            = settingDataFactory { info, it -> adapt(info, SwitchData(it)) }
    }
}

/**
 * A bare radio setting without any information to be displayed.
 * Those will be added in the `ui-*` libraries.
 *
 * @author Patrick Schneider
 */
abstract class RadioSetting<T, SD: RadioSetting.RadioData<T>>(override val data: SD): Setting<SD> {
    open class RadioData<T>(val data: SettingData<T>, val entries: List<SettingEntry<T>>): ISettingData<T> by data
    interface RadioBuildInfo<T>: ISettingDataBuildInfo<T> {
        fun entries(initializer: Entries<T>.() -> Unit)
        var entries: List<SettingEntry<T>>
    }
    companion object {
        inline fun < T, SI: RadioBuildInfo<T>, SD: RadioData<T>> factory(crossinline adapt: (SI, RadioData<T>) -> SD): SettingFactory<SI, SD>
            = settingDataFactory { info, it -> adapt(info, RadioData(it, info.entries)) }
    }
}

/**
 * A bare menu setting without any information to be displayed.
 * Those will be added in the `ui-*` libraries.
 *
 * @author Patrick Schneider
 */
abstract class MenuSetting<SD : MenuSetting.MenuData>(override val data: SD): Setting<SD> {
    open class MenuData: Info
    interface MenuBuildInfo: BuildInfo
    companion object {
        fun <SI: MenuBuildInfo, SD: MenuData> factory(adapt: (SI, MenuData) -> SD): SettingFactory<SI, SD> = { _, _ -> InfoFactory { info -> { adapt(info, MenuData()) } } }
    }
}
