package org.secuso.privacyfriendlycore.ui.settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.preference.PreferenceManager
import org.secuso.privacyfriendlycore.ui.stage.Stage

typealias SettingCategoryMapping = HashMap<String, List<SettingData<*>>>

open class Settings(private val settings: SettingCategoryMapping) : ISettings {
    override val composable = @Composable { SettingsMenu(this.settings) }
    override val all: List<SettingData<*>>
        get() = settings.values.flatten()

    class Category(private val context: Context) : Stage.Builder<Settings> {
        private val settings: SettingCategoryMapping = hashMapOf()
        private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        override fun build(): Settings {
            Log.d("Debug", this.settings.keys.joinToString(", "))
            return Settings(this.settings)
        }

        @Suppress("Unused")
        fun category(category: String, initializer: org.secuso.privacyfriendlycore.ui.settings.builder.Settings.() -> Unit) {
            this.settings[category] =
                org.secuso.privacyfriendlycore.ui.settings.builder.Settings(preferences = this.preferences, resources = context.resources).apply(initializer).settings
        }

        @Suppress("Unused")
        fun category(categoryId: Int, initializer: org.secuso.privacyfriendlycore.ui.settings.builder.Settings.() -> Unit) {
            this.settings[context.getString(categoryId)] = org.secuso.privacyfriendlycore.ui.settings.builder.Settings(
                preferences = this.preferences,
                resources = context.resources
            ).apply(initializer).settings
        }
    }

    companion object {
        @Suppress("Unused")
        fun build(context: Context, initializer: Category.() -> Unit): Settings {
            return Category(context).apply(initializer).build()
        }
    }

}