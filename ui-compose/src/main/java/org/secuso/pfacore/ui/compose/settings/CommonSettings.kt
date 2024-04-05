package org.secuso.pfacore.ui.compose.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import org.secuso.pfacore.R
object CommonSettings {
    val themeSelectorKey = "settings_day_night_theme"

    fun themeSelector(context: Context): Settings.Setting.() -> Unit {
        return {
            radio<Int> {
                key = themeSelectorKey
                default = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                onUpdate = { AppCompatDelegate.setDefaultNightMode(it) }
                title { resource(R.string.select_day_night_theme) }
                summary {transform { state, value -> state.entries!!.find { it.value == value}!!.entry }}
                entries {
                    entries(R.array.array_day_night_theme)
                    values(context.resources.getStringArray(R.array.array_day_night_theme_values).map { it.toInt() })
                }
            }
        }
    }
}