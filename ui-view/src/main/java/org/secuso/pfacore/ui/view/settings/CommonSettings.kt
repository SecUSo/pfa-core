package org.secuso.pfacore.ui.view.settings

import androidx.appcompat.app.AppCompatDelegate
import org.secuso.pfacore.R
import org.secuso.pfacore.model.Theme

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