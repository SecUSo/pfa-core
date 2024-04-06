package org.secuso.pfacore.ui.compose.settings

import androidx.appcompat.app.AppCompatDelegate
import org.secuso.pfacore.R

class SettingThemeSelector() {

    enum class Mode {
        SYSTEM,
        LIGHT,
        DARK;
    }

    companion object {
        const val themeSelectorKey = "settings_day_night_theme"
    }

    fun build(): Settings.Setting.() -> Unit {
        return {
            radio<String> {
                key = themeSelectorKey
                default = Mode.SYSTEM.toString()
                onUpdate = {
                    when (Mode.valueOf(it)) {
                        Mode.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        Mode.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        Mode.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
                title { resource(R.string.select_day_night_theme) }
                summary { transform { state, value -> state.entries.find { it.value == value }!!.entry } }
                entries {
                    entries(R.array.array_day_night_theme)
                    values(SettingThemeSelector.Mode.entries.map { it.toString() })
                }
            }
        }
    }
}