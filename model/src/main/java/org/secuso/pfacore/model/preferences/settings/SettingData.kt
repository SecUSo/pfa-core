package org.secuso.pfacore.model.preferences.settings

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.secuso.pfacore.backup.Restorer
import org.secuso.pfacore.model.preferences.BuildInfo
import org.secuso.pfacore.model.preferences.Info
import org.secuso.pfacore.model.preferences.InfoFactory
import org.secuso.pfacore.model.preferences.Preferable
import org.secuso.pfacore.model.preferences.PreferableBuildInfo
import org.secuso.pfacore.model.preferences.Preference
import org.secuso.pfacore.model.preferences.PreferenceBuildInfo
import org.secuso.pfacore.model.preferences.build

interface Setting<SI : Info> {
    val data: SI
}

data class SettingEntry<T>(
    var entry: String,
    var value: T
)

interface ISettingData<T> : Info, Preferable<T> {
    var enabled: LiveData<Boolean>
}

interface ISettingDataBuildInfo<T> : BuildInfo, PreferableBuildInfo<T> {
    var dependency: String?
}

open class SettingDataBuildInfo<T>: ISettingDataBuildInfo<T>, PreferenceBuildInfo<T>() {
    override var dependency: String? = null
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
typealias SettingFactory<BI, SI> = (SharedPreferences, EnabledByDependency) -> InfoFactory<BI, SI>

fun <T, BI: ISettingDataBuildInfo<T>, SI: ISettingData<T>> settingDataFactory(
    adapt: (BI, SettingData<T>) -> SI
): SettingFactory<BI, SI> {
    return { preferences, enabled ->
        InfoFactory { info ->
            {
                adapt(info, info.build<T, BI, SettingData<T>>(preferences) { state, restorer, onUpdate ->
                    InfoFactory {
                        {
                            SettingData(
                                state(info.key!!, info.default!!),
                                info.default!!,
                                info.key!!,
                                info.backup,
                                restorer(info.default!!),
                                onUpdate(info.key!!, info.default!!, info.onUpdate),
                                enabled(info.dependency)
                            )
                        }
                    }
                }
                )
            }
        }
    }
}
