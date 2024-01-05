package org.secuso.privacyfriendlycore.ui.settings

import android.content.Context
import android.content.SharedPreferences
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
        this.settings[category] = SettingBuilder(preferences = this.preferences).apply(initializer).settings

    }
}

class SettingBuilder(
    val settings: MutableList<SettingData<*>> = mutableListOf(),
    private val preferences: SharedPreferences
) {

    fun switch(initializer: SettingDSL<Boolean>.() -> Unit) {
        val setting = SettingDSL<Boolean>()
            .apply(initializer)
            .compose(state = { data ->
                mutableStateOf(preferences.getBoolean(data.key!!, data.default!!))
            }) {
                data ->
                    SwitchPreference(
                        data = data,
                        checked = data.state,
                        update = { preferences.edit().putBoolean(data.key, it).apply(); data.state.value = it }
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
    var customTitle: (@Composable (T, Modifier) -> Unit)? = null,
    var customSummary: (@Composable (T, Modifier) -> Unit)? = null
) {
    private val defaultTitle: (String) -> (@Composable (T, Modifier) -> Unit) = { text ->
        { _, modifier -> Text(text = text, modifier = modifier) }
    }
    private val defaultSummary: (String) -> (@Composable (T, Modifier) -> Unit) = { text ->
        { _, modifier -> SummaryText(text = text, modifier = modifier) }
    }
    fun compose(state: (SettingDSL<T>) -> MutableState<T>, composable: @Composable (data: SettingData<T>) -> Unit): SettingData<T> {
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
                _composable = composable
            )
        }
    }
}

fun settings(context: Context, initializer: SettingsBuilder.() -> Unit): Settings {
    return SettingsBuilder(context).apply(initializer).settings
}
