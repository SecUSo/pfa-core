package org.secuso.pfacore.model.preferences.settings

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.secuso.pfacore.activities.PFActivity
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

typealias UseActivityCallback = ((PFActivity) -> (() -> Unit)?)

/**
 * The behavior specific to any setting, even those entries not associated with any value.
 *
 * @property enabled An observable value stating if this setting is currently active or if it should be ignored.
 * @property useActivity A function being called with the current activity in the [android.app.Activity.onCreate] lifecycle.
 *      This function is expected to return a function which will be invoked in the [android.app.Activity.onDestroy] lifecycle to allow some cleanup code to be run.
 *      It is NOT intended to do any heavy work in this function as it will be called immediately, even if the preference is not visible, e.g. hidden inside another menu.
 *
 * @author Patrick Schneider
 */
interface ISettingBehaviour : Info {
    var enabled: LiveData<Boolean>
    var useActivity: UseActivityCallback?
}

open class SettingBehaviour(
    override var enabled: LiveData<Boolean> = MutableLiveData(true),
    override var useActivity: UseActivityCallback? = null
) : ISettingBehaviour

interface ISettingBehaviourBuildInfo : BuildInfo {
    var dependency: DependencyRelation.() -> Unit
    var useActivity: UseActivityCallback?
}

class SettingBehaviourBuildInfo : ISettingBehaviourBuildInfo {
    override var dependency: DependencyRelation.() -> Unit = {}
    override var useActivity: UseActivityCallback? = null
}

/**
 * The data specific to a setting.
 *
 * @author Patrick Schneider
 */
interface ISettingData<T> : ISettingBehaviour, Preferable<T>

/**
 * Declare dependencies between settings by requiring that a condition is met for a specific key.
 *
 * @author Patrick Schneider
 */
class DependencyRelation(internal val dependencies: MutableList<Pair<String, (Any) -> Boolean>> = mutableListOf()) {
    infix fun String.on(condition: (Any) -> Boolean) {
        dependencies.add(this to condition)
    }
    infix fun String.on(value: Any) {
        dependencies.add(this to { it == value })
    }
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
interface ISettingDataBuildInfo<T> : BuildInfo, PreferableBuildInfo<T>, ISettingBehaviourBuildInfo

open class SettingDataBuildInfo<T>: ISettingDataBuildInfo<T>, PreferenceBuildInfo<T>(), ISettingBehaviourBuildInfo {
    override var dependency: DependencyRelation.() -> Unit = {}
    override var useActivity: UseActivityCallback? = null
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
    override var enabled: LiveData<Boolean>,
    override var useActivity: UseActivityCallback? = null
): ISettingData<T>, Preference<T>(state, default, key, backup, restorer, onUpdate)

typealias EnabledByDependency = (DependencyRelation) -> LiveData<Boolean>
typealias SettingDataFactory<BI, SI> = (SharedPreferences, EnabledByDependency) -> InfoFactory<BI, SI>

fun <T, BI: ISettingDataBuildInfo<T>, SI: ISettingData<T>> settingDataFactory(
    adapt: (BI, SettingData<T>) -> SI
): SettingDataFactory<BI, SI> {
    return { preferences, enabled -> InfoFactory {
            info -> {
                val data = info.build<T, BI, SettingData<T>>(preferences) { state, restorer, onUpdate -> InfoFactory {
                    {
                        SettingData(
                            state(info.key!!, info.default!!),
                            info.default!!,
                            info.key!!,
                            info.backup,
                            restorer(info.default!!),
                            onUpdate(info.key!!, info.default!!, info.onUpdate),
                            enabled(DependencyRelation().apply(info.dependency)),
                            info.useActivity
                        )
                    }
                } }
                adapt(info, data)
            }
        }
    }
}

typealias SettingFactory<BI, SI> = (EnabledByDependency) -> InfoFactory<BI, SI>
fun <BI: ISettingBehaviourBuildInfo, SI: ISettingBehaviour> settingFactory(
    adapt: (BI, SettingBehaviour) -> SI
): SettingFactory<BI, SI> {
    return { enabled -> InfoFactory {
            info -> {
                val behaviour = SettingBehaviour(
                    enabled(DependencyRelation().apply(info.dependency)),
                    info.useActivity
                )
                adapt(info, behaviour)
            }
        }
    }
}
