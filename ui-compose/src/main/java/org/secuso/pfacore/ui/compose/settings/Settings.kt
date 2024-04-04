package org.secuso.pfacore.ui.compose.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.preference.PreferenceManager
import org.secuso.pfacore.backup.Restorer
import org.secuso.pfacore.model.settings.SettingData
import org.secuso.pfacore.ui.compose.settings.composables.RadioPreference
import org.secuso.pfacore.ui.compose.settings.composables.SettingsMenu
import org.secuso.pfacore.ui.compose.settings.composables.SwitchPreference
import org.secuso.pfacore.model.settings.RadioSetting as MRadioSetting
import org.secuso.pfacore.model.settings.Settings as MSettings
import org.secuso.pfacore.model.settings.Settings.Category as MCategory
import org.secuso.pfacore.model.settings.Settings.Menu as MMenu
import org.secuso.pfacore.model.settings.Settings.Setting as MSetting
import org.secuso.pfacore.model.settings.SwitchSetting as MSwitchSetting

class Settings(
    settings: List<SettingCategory>,
    val Display: @Composable () -> Unit = @Composable { SettingsMenu(settings) }
) : MSettings<SettingDecorator<Any>, SettingCategory, SettingMenu>(settings) {

    class Setting(
        val preferences: SharedPreferences,
        resources: Resources,
        builders: SettingsBuilders<SettingDecorator<Any>, SingleSetting<Any>, SettingCategory, SettingMenu, Setting, Category, Menu>
    ) : MSetting<SettingDecorator<Any>, SingleSetting<Any>, SettingCategory, SettingMenu, Setting, Category, Menu>(
        resources = resources,
        builders = builders
    ) {
        fun switch(initializer: SingleSetting<Boolean>.() -> Unit): SettingDecorator<Any> {
            return SingleSetting(resources) { data, backup, restorer -> SwitchSetting(data, backup, restorer) }.apply(initializer).create(preferences)
        }

        inline fun <reified T> radio(initializer: SingleSetting<T>.() -> Unit): SettingDecorator<Any> {
            return SingleSetting<T>(resources) { data, backup, restorer -> RadioSetting(data, backup, restorer) }.apply(initializer).create(preferences)
        }
    }

    class Category(context: Context, builders: SettingsBuilders<SettingDecorator<Any>, SingleSetting<Any>, SettingCategory, SettingMenu, Setting, Category, Menu>) :
        MCategory<SettingDecorator<Any>, SingleSetting<Any>, SettingCategory, SettingMenu, Setting, Category, Menu>(context, builders)

    class Menu(builders: SettingsBuilders<SettingDecorator<Any>, SingleSetting<Any>, SettingCategory, SettingMenu, Setting, Category, Menu>) :
        MMenu<SettingDecorator<Any>, SingleSetting<Any>, SettingCategory, SettingMenu, Setting, Category, Menu>(builders)

    companion object {
        fun build(
            context: Context,
            initializer: Category.() -> Unit
        ): Settings {
            val builders = SettingsBuilders<SettingDecorator<Any>, SingleSetting<Any>, SettingCategory, SettingMenu, Setting, Category, Menu>(
                { Setting(PreferenceManager.getDefaultSharedPreferences(context), context.resources, it) },
                { Category(context, it) },
                { Menu(it) },
                { name, setting -> SettingCategory(name, setting) },
                { name, menu -> SettingMenu(name, menu) }
            )
            return MSettings.build<SettingDecorator<Any>, SingleSetting<Any>, SettingCategory, SettingMenu, Setting, Category, Menu, Settings>(
                { Settings(it) },
                builders,
                initializer
            )
        }
    }
}

class SwitchSetting(data: SettingData<Boolean>, backup: Boolean, restorer: Restorer<Boolean>) : MSwitchSetting<SettingDecorator<Boolean>>(data, backup, restorer),
    DisplayableInnerSetting<Boolean, SettingDecorator<Boolean>> {
    @Composable
    override fun Display(
        title: @Composable (SettingData<Boolean>, Boolean, Modifier) -> Unit,
        summary: @Composable (SettingData<Boolean>, Boolean, Modifier) -> Unit,
        onClick: (() -> Unit)?
    ) {
        SwitchPreference(this, super.data.enable.observeAsState(false), { super.data.value = it }, title, summary, onClick)
    }
}

class RadioSetting<T>(data: SettingData<T>, backup: Boolean, restorer: Restorer<T>) : MRadioSetting<T, SettingDecorator<T>>(data, backup, restorer),
    DisplayableInnerSetting<T, SettingDecorator<T>> {
    @Composable
    override fun Display(
        title: @Composable (SettingData<T>, T, Modifier) -> Unit,
        summary: @Composable (SettingData<T>, T, Modifier) -> Unit,
        onClick: (() -> Unit)?
    ) {
        RadioPreference(this, super.data.enable.observeAsState(false), { super.data.value = it }, title, summary, onClick)
    }
}