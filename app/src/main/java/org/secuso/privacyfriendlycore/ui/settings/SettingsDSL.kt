package org.secuso.privacyfriendlycore.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.preference.PreferenceManager
import org.secuso.privacyfriendlycore.ui.composables.SummaryText

typealias Settings = HashMap<String, List<SettingData<*>>>

class SettingsBuilder(context: Context) {
    internal val settings: Settings = hashMapOf()
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun category(category: String, initializer: SettingBuilder.() -> Unit) {
        this.settings[category] = SettingBuilder(preferences = this.preferences).apply(initializer).settings

    }
}

class SettingBuilder(
    val settings: MutableList<SettingData<*>> = mutableListOf(),
    private val preferences: SharedPreferences
) {

    fun track(dependency: String?): MutableState<Boolean> {
        if (dependency == null) {
            return mutableStateOf(true)
        }
        val state = settings.find { it.key == dependency }?.state ?: throw IllegalStateException("Dependency $dependency not found. Dependencies must be in the same category and precede the setting")
        if (state.value !is Boolean) {
            throw IllegalStateException("A Setting can only depend on Boolean-Settings")
        }
        return state as MutableState<Boolean>
    }
    fun switch(initializer: SettingDSL<Boolean>.() -> Unit) {
        val setting = SettingDSL<Boolean>()
            .apply(initializer)
            .compose(
                state = { data ->
                    mutableStateOf(preferences.getBoolean(data.key!!, data.default!!))
                },
                enabled = { track(it) }
            ) { data -> SwitchPreference(
                    data = data,
                    enabled = data.enable,
                    checked = data.state,
                    update = { preferences.edit().putBoolean(data.key, it).apply(); data.state.value = it }
                )
            }
        this.settings.add(setting)
    }

    fun radioString(initializer: SettingDSL<String>.() -> Unit) {
        val setting = SettingDSL<String>()
            .apply(initializer)
            .compose(state = { data ->
                mutableStateOf(preferences.getString(data.key!!, data.default!!)!!)
            },
                enabled = { track(it) }
            ) { data -> RadioPreference(
                    data = data,
                    enabled = data.enable,
                    selected = data.state,
                    update = { preferences.edit().putString(data.key, it).apply(); data.state.value = it }
                )
            }
        this.settings.add(setting)
    }

    fun radioInt(initializer: SettingDSL<Int>.() -> Unit) {
        val setting = SettingDSL<Int>()
            .apply(initializer)
            .compose(state = { data ->
                mutableStateOf(preferences.getInt(data.key!!, data.default!!))
            },
                enabled = { track(it) }
            ) { data -> RadioPreference(
                data = data,
                enabled = data.enable,
                selected = data.state,
                update = { preferences.edit().putInt(data.key, it).apply(); data.state.value = it }
            )
            }
        this.settings.add(setting)
    }
}

class SettingDSL<T>(
    var key: String? = null,
    var default: T? = null,
    var title: String? = null,
    var summary: String? = null,
    var customTitle: (@Composable (SettingData<T>, Modifier) -> Unit)? = null,
    var customSummary: (@Composable (SettingData<T>, Modifier) -> Unit)? = null,
    private var entries: List<SettingEntry<T>>? = null,
    var depends: String? = null
) {
    private val defaultTitle: (String) -> (@Composable (SettingData<T>, Modifier) -> Unit) = { text ->
        { _, modifier -> Text(text = text, modifier = modifier) }
    }
    private val defaultSummary: (String) -> (@Composable (SettingData<T>, Modifier) -> Unit) = { text ->
        { _, modifier -> SummaryText(text = text, modifier = modifier) }
    }

    fun entries(initializer: SettingEntriesDSL<T>.() -> Unit) {
        this.entries = SettingEntriesDSL<T>().apply(initializer).collect()
    }
    fun compose(
        state: (SettingDSL<T>) -> MutableState<T>,
        enabled: (String?) -> MutableState<Boolean>,
        composable: @Composable (data: SettingData<T>) -> Unit): SettingData<T> {
        return when {
            key === null -> throw IllegalStateException("A setting needs to have a key")
            default === null -> throw IllegalStateException("A setting needs to have a default value")
            title === null -> throw IllegalStateException("A setting needs a title")
            else -> SettingData(
                key = key!!,
                state = state(this),
                defaultValue = default!!,
                title = customTitle ?: defaultTitle(title!!),
                summary = customSummary ?: if (summary != null) { defaultSummary(summary!!) } else { { _, _ -> } },
                _composable = composable,
                entries = entries,
                enable = enabled(depends)
            )
        }
    }
}

class SettingEntriesDSL<T>(
    var entries: List<String>? = null,
    var values: List<T>? = null
) {
    fun collect() = entries!!.zip(values!!).map { (entry, value) -> SettingEntry(entry, value)}.toList()
}

fun settings(context: Context, initializer: SettingsBuilder.() -> Unit): Settings {
    return SettingsBuilder(context).apply(initializer).settings
}
