package org.secuso.privacyfriendlycore.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.preference.PreferenceManager
import org.secuso.privacyfriendlycore.ui.composables.SummaryText

typealias Settings = HashMap<String, List<SettingData<*>>>

class SettingsBuilder(private val context: Context) {
    internal val settings: Settings = hashMapOf()
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun category(category: String, initializer: SettingBuilder.() -> Unit) {
        this.settings[category] = SettingBuilder(preferences = this.preferences, resources = context.resources).apply(initializer).settings
    }

    fun category(categoryId: Int, initializer: SettingBuilder.() -> Unit) {
        this.settings[context.getString(categoryId)] = SettingBuilder(preferences = this.preferences, resources = context.resources).apply(initializer).settings
    }
}

class SettingBuilder(
    val settings: MutableList<SettingData<*>> = mutableListOf(),
    private val preferences: SharedPreferences,
    private val resources: Resources
) {

    private fun track(dependency: String?): MutableState<Boolean> {
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
        val setting = SettingDSL<Boolean>(resources)
            .apply(initializer)
            .compose(
                state = { data ->
                    mutableStateOf(preferences.getBoolean(data.key!!, data.default!!))
                },
                enabled = { track(it) }
            ) { settingDSL ->
                { data ->
                    SwitchPreference(
                        data = data,
                        enabled = data.enable,
                        update = {
                            preferences.edit().putBoolean(data.key, it).apply()
                            data.state.value = it
                            settingDSL.onUpdate?.let { onUpdate -> onUpdate(it) }
                        }
                    )
                }
            }
        this.settings.add(setting)
    }

    fun radioString(initializer: SettingDSL<String>.() -> Unit) {
        val setting = SettingDSL<String>(resources)
            .apply(initializer)
            .compose(state = { data ->
                mutableStateOf(preferences.getString(data.key!!, data.default!!)!!)
            },
                enabled = { track(it) }
            ) { settingDSL ->
                { data ->
                    RadioPreference(
                        data = data,
                        enabled = data.enable,
                        update = {
                            preferences.edit().putString(data.key, it).apply()
                            data.state.value = it
                            settingDSL.onUpdate?.let { onUpdate -> onUpdate(it) }
                        }
                    )
                }
            }
        this.settings.add(setting)
    }

    fun radioInt(initializer: SettingDSL<Int>.() -> Unit) {
        val setting = SettingDSL<Int>(resources)
            .apply(initializer)
            .compose(state = { data ->
                mutableStateOf(preferences.getInt(data.key!!, data.default!!))
            },
                enabled = { track(it) }
            ) { settingDSL ->
                { data ->
                    RadioPreference(
                        data = data,
                        enabled = data.enable,
                        update = {
                            preferences.edit().putInt(data.key, it).apply()
                            data.state.value = it
                            settingDSL.onUpdate?.let { onUpdate -> onUpdate(it) }
                        }
                    )
                }
            }
        this.settings.add(setting)
    }
}

class SettingDSL<T>(
    private val resources: Resources,
    var key: String? = null,
    var default: T? = null,
    var depends: String? = null,
    var onUpdate: ((T) -> Unit)? = null
) {
    private var entries: List<SettingEntry<T>>? = null
    private var title: (@Composable (SettingData<T>, T, Modifier) -> Unit)? = null
    private var summary: (@Composable (SettingData<T>, T, Modifier) -> Unit)? = null

    fun entries(initializer: SettingEntriesDSL<T>.() -> Unit) {
        this.entries = SettingEntriesDSL<T>(resources).apply(initializer).collect()
    }

    fun title(initializer: SettingInfoDSL<T>.() -> Unit) {
        this.title = SettingInfoDSL<T>(resources) { transformer ->
            { data, value, modifier -> Text(text = transformer(data, value), modifier = modifier) }
        }.apply(initializer).build()
    }

    fun summary(initializer: SettingInfoDSL<T>.() -> Unit) {
        this.summary = SettingInfoDSL<T>(resources) { transformer ->
            { data, value, modifier -> SummaryText(text = transformer(data, value), modifier = modifier) }
        }.apply(initializer).build()
    }

    fun compose(
        state: (SettingDSL<T>) -> MutableState<T>,
        enabled: (String?) -> MutableState<Boolean>,
        composable: (SettingDSL<T>) -> @Composable (data: SettingData<T>) -> Unit): SettingData<T> {
        return when {
            key === null -> throw IllegalStateException("A setting needs to have a key")
            default === null -> throw IllegalStateException("A setting needs to have a default value")
            title === null -> throw IllegalStateException("A setting needs a title")
            else -> SettingData(
                key = key!!,
                state = state(this),
                defaultValue = default!!,
                title = this.title!!,
                summary = this.summary ?: { _, _, _ -> },
                _composable = composable(this),
                entries = entries,
                enable = enabled(depends)
            )
        }
    }
}

class SettingEntriesDSL<T>(
    private val resources: Resources,
    private var entries: List<String>? = null,
    private var values: List<T>? = null
) {
    fun collect() = entries!!.zip(values!!).map { (entry, value) -> SettingEntry(entry, value)}.toList()
    fun entries(entries: List<String>) {
        this.entries = entries
    }

    fun entries(id: Int) {
        this.entries = resources.getStringArray(id).toList()
    }

    fun values(values: List<T>) {
        this.values = values
    }
}

class SettingInfoDSL<T>(
    private val resources: Resources,
    private val default: ((SettingData<T>, T) -> String) -> (@Composable (SettingData<T>, T, Modifier) -> Unit)
) {
    private var composable: (@Composable (SettingData<T>, T, Modifier) -> Unit)? = null

    fun build() = this.composable!!

    fun resource(id: Int) {
        this.composable = default { _, _ -> resources.getString(id)}
    }

    fun literal(text: String) {
        this.composable = default { _,_ -> text }
    }

    fun transform(transformer: (SettingData<T>, T) -> String) {
        this.composable = default(transformer)
    }

    fun custom(composable: (@Composable (SettingData<T>, T, Modifier) -> Unit)) {
        this.composable = composable
    }
}

fun settings(context: Context, initializer: SettingsBuilder.() -> Unit): Settings {
    return SettingsBuilder(context).apply(initializer).settings
}
