package org.secuso.pfacore.ui.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import org.secuso.pfacore.R
import org.secuso.pfacore.ui.settings.builder.SettingsBuilder

object CommonSettings {
    val themeSelectorKey = "settings_day_night_theme"

    fun themeSelector(context: Context): SettingsBuilder.() -> Unit {
        return {
            radioString {
                key = themeSelectorKey
                default = "-1"
                onUpdate = { AppCompatDelegate.setDefaultNightMode(it.toInt()) }
                title { resource(R.string.select_day_night_theme) }
                summary {transform { state, value -> state.entries!!.find { it.value == value}!!.entry }}
                entries {
                    entries(R.array.array_day_night_theme)
                    values(context.resources.getStringArray(R.array.array_day_night_theme_values).toList())
                }
            }
        }
    }
}