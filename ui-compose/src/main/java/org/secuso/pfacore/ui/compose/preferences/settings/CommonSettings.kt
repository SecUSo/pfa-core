package org.secuso.pfacore.ui.compose.preferences.settings

import androidx.appcompat.app.AppCompatDelegate
import org.secuso.pfacore.R
import org.secuso.pfacore.model.Theme
import org.secuso.pfacore.model.preferences.Preferable
import org.secuso.pfacore.model.preferences.Preferences

class SettingThemeSelector {

    companion object {
        const val themeSelectorKey = "settings_day_night_theme"
    }

    fun build(): Settings.Setting.() -> RadioSetting.RadioData<String> {
        return {
            radio<String> {
                key = themeSelectorKey
                default = Theme.SYSTEM.toString()
                onUpdate = { Theme.valueOf(it).apply() }
                title { resource(R.string.select_day_night_theme) }
                summary { transform { state, value -> state.entries.find { it.value == value }!!.entry } }
                entries {
                    entries(R.array.array_day_night_theme)
                    values(Theme.entries.map { it.toString() })
                }
            }
        }
    }
}

class PreferenceFirstTimeLaunch {
    companion object {
        const val firstTimeLaunchKey = "IsFirstTimeLaunch"
    }
    fun build(): Preferences.Preference.() -> Preferable<Boolean> {
        return {
            preference {
                key = firstTimeLaunchKey
                default = true
                backup = true
            }
        }
    }
}