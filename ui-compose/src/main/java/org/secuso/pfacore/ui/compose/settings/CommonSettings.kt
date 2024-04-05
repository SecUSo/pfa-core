package org.secuso.pfacore.ui.compose.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import org.secuso.pfacore.R
object CommonSettings {
    val themeSelectorKey = "settings_day_night_theme"

    fun themeSelector(context: Context): Settings.Setting.() -> Unit {
        return {
            radio<String> {
                key = themeSelectorKey
                default = "System"
                onUpdate = { when(it) {
                    "System" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } }
                title { resource(R.string.select_day_night_theme) }
                summary {transform { state, value -> state.entries!!.find { it.value == value}!!.entry }}
                entries {
                    entries(R.array.array_day_night_theme)
                    values(listOf("System", "Light", "Dark"))
                }
            }
        }
    }
}