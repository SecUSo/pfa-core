package org.secuso.pfacore.model.settings

import android.content.res.Resources
import org.secuso.pfacore.backup.Restorer
import org.secuso.pfacore.model.DeriveState
import org.secuso.pfacore.model.EnabledByDependency
import org.secuso.pfacore.model.ISettingData
import org.secuso.pfacore.model.ISettingDataBuildInfo
import org.secuso.pfacore.model.SettingBuildInfo
import org.secuso.pfacore.model.SettingData
import org.secuso.pfacore.model.SettingEntry
import org.secuso.pfacore.model.SettingInfo
import org.secuso.pfacore.model.SettingInfoFactory
import org.secuso.pfacore.model.settingDataFactory

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

typealias SettingFactory<T, BI, SI> = (state: DeriveState<T>, enabled: EnabledByDependency, (T) -> Restorer<T>) -> SettingInfoFactory<BI,SI>
abstract class SwitchSetting<SD: SwitchSetting.SwitchData, S : SettingComposite<SD>>(data: SD) : SettingComposite<SD>(data) {
    open class SwitchData(val data: SettingData<Boolean>): ISettingData<Boolean> by data
    interface SwitchBuildInfo: ISettingDataBuildInfo<Boolean>
    companion object {
        fun <SI: SwitchBuildInfo, SD: SwitchData> factory(adapt: (SI, SwitchData) -> SD): SettingFactory<Boolean, SI, SD> =
            { state, enabled, restorer -> settingDataFactory(state, enabled, restorer) { info, it -> adapt(info, SwitchData(it)) } }
    }
}
abstract class RadioSetting<T, SD: RadioSetting.RadioData<T>, S : SettingComposite<SD>>(data: SD) : SettingComposite<SD>(data) {
    open class RadioData<T>(val data: SettingData<T>, val entries: List<SettingEntry<T>>): ISettingData<T> by data
    interface RadioBuildInfo<T>: ISettingDataBuildInfo<T> {
        fun entries(initializer: Entries<T>.() -> Unit)
        var entries: List<SettingEntry<T>>
    }
    companion object {
        inline fun < T, SI: RadioBuildInfo<T>, SD: RadioData<T>> factory(crossinline adapt: (SI, RadioData<T>) -> SD): SettingFactory<T, SI, SD> =
            { state, enabled, restorer -> settingDataFactory(state, enabled, restorer) { info, it -> adapt(info, RadioData(it, info.entries)) } }
    }
}
abstract class MenuSetting<SD : MenuSetting.MenuData, S : SettingComposite<SD>>(data: SD) : SettingComposite<SD>(data) {
    open class MenuData: SettingInfo
    interface MenuBuildInfo: SettingBuildInfo
    companion object {
        fun <SI: MenuBuildInfo, SD: MenuData> factory(adapt: (SI, MenuData) -> SD): SettingFactory<Unit, SI, SD> =
            { _, _, _ -> SettingInfoFactory { info -> { adapt(info, MenuData()) } } }
    }
}
