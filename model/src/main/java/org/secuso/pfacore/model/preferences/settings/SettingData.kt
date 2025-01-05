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

/**
 * A pair of a string and a value intended to be used in settings enabling the user to select a value from a list.
 *
 * @author Patrick Schneider
 */
data class SettingEntry<T>(
    var entry: String,
    var value: T
)

/**
 * The data specific to a setting.
 *
 * @property enabled An observable value stating if this setting is currently active or if it should be ignored.
 *
 * @author Patrick Schneider
 */
interface ISettingData<T> : Info, Preferable<T> {
    var enabled: LiveData<Boolean>
}

/**
 * The data needed to build a general setting.
 *
 * @property dependency A key to another setting in the same category of the setting to be build, determining if the setting is active or not.
 *
 * @see PreferableBuildInfo
 *
 * @author Patrick Schneider
 */
interface ISettingDataBuildInfo<T> : BuildInfo, PreferableBuildInfo<T> {
    var dependency: String?
}

open class SettingDataBuildInfo<T>: ISettingDataBuildInfo<T>, PreferenceBuildInfo<T>() {
    override var dependency: String? = null
}

/**
 * The minimum required data needed to represent a setting.
 *
 * @param state A mutable, observable state of the setting.
 * @param default The default value of this setting.
 * @param key The string which identifies this setting to store/load it's value.
 * @param backup Whether or not this setting shall be included in a backup.
 * @param value The current value the setting.
 * @param restorer Restore the setting value from JSON.
 * @param onUpdate a listener to be notified if the setting changes it's value.
 *
 * @author Patrick Schneider
 */
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
