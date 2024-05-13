package org.secuso.pfacore.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.secuso.pfacore.backup.Restorer

interface SettingInfo
interface SettingBuildInfo
fun interface SettingInfoFactory<BI: SettingBuildInfo, SI: SettingInfo> {
    fun build(info: BI): () -> SI
}

interface Setting<SI: SettingInfo> {
    val data: SI
}

data class SettingEntry<T>(
    var entry: String,
    var value: T
)

interface ISettingData<T>: SettingInfo, Preferable<T> {
    val state: MutableLiveData<T>
    var enabled: LiveData<Boolean>
    var onUpdate: (T) -> Unit
}

interface ISettingDataBuildInfo<T>: SettingBuildInfo, PreferableBuildInfo<T> {
    var dependency: String?
    var onUpdate: (T) -> Unit
}

open class SettingDataBuildInfo<T>: ISettingDataBuildInfo<T>, PreferenceBuildInfo<T>() {
    override var dependency: String? = null
    override var onUpdate: (T) -> Unit = {}
}

open class SettingData<T>(
    state: MutableLiveData<T>,
    override var default: T,
    override var key: String,
    override var backup: Boolean,
    restorer: Restorer<T>,
    override var onUpdate: (T) -> Unit,
    override var enabled: LiveData<Boolean>
): ISettingData<T>, Preference<T>(state, default, key, backup, restorer, onUpdate)

typealias EnabledByDependency = (String?) -> LiveData<Boolean>
typealias DeriveState<T> = (String, T) -> MutableLiveData<T>
typealias DataSaverUpdater<T> = (String, T, (T) -> Unit) -> (T) -> Unit

fun <T, BI: ISettingDataBuildInfo<T>, SI: ISettingData<T>> settingDataFactory(
    state: DeriveState<T>,
    enabled: EnabledByDependency,
    restorer: (T) -> Restorer<T>,
    onUpdate: DataSaverUpdater<T>,
    adapt: (BI, SettingData<T>) -> SI
): SettingInfoFactory<BI, SI> {
    return SettingInfoFactory<BI, SI> { info ->
        { adapt(info, SettingData(
            state(info.key!!, info.default!!),
            info.default!!,
            info.key!!,
            info.backup,
            restorer(info.default!!),
            onUpdate(info.key!!, info.default!!, info.onUpdate),
            enabled(info.dependency)
        )) }
    }
}
