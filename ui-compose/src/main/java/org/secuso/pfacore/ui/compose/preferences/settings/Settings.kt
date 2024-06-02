package org.secuso.pfacore.ui.compose.preferences.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.preference.PreferenceManager
import org.secuso.pfacore.model.preferences.settings.SettingEntry
import org.secuso.pfacore.model.preferences.Info
import org.secuso.pfacore.ui.compose.Displayable
import org.secuso.pfacore.ui.compose.preferences.settings.composables.SettingsMenu
import org.secuso.pfacore.model.preferences.settings.Settings as MSettings
import org.secuso.pfacore.model.preferences.settings.Settings.Category as MCategory
import org.secuso.pfacore.model.preferences.settings.Settings.Menu as MMenu
import org.secuso.pfacore.model.preferences.settings.Settings.Setting as MSetting

class Settings(
    settings: List<SettingCategory>,
    private val display: @Composable () -> Unit = @Composable { SettingsMenu(settings) }
) : MSettings<DisplayableSettingInfo, SettingCategory, SettingMenu>(settings), Displayable {

    @Composable
    override fun Display(onClick: (() -> Unit)?) {
        display()
    }

    class Setting(
        preferences: SharedPreferences,
        val context: Context,
        builders: SettingsBuilders<DisplayableSettingInfo, SettingCategory, SettingMenu, Setting, Category, Menu>
    ) : MSetting<DisplayableSettingInfo, SettingCategory, SettingMenu, Setting, Category, Menu>(
        preferences = preferences,
        builders = builders
    ) {
        fun switch(initializer: SwitchSetting.SwitchBuildInfo.() -> Unit): SwitchSetting.SwitchData {
            return SwitchSetting.SwitchBuildInfo(context.resources).apply(initializer).build(SwitchSetting.factory()).create().register().data
        }

        fun <T> radio(initializer: RadioSetting.RadioBuildInfo<T>.() -> Unit): RadioSetting.RadioData<T> {
            return RadioSetting.RadioBuildInfo(context.resources, listOf<SettingEntry<T>>()).apply(initializer).build(RadioSetting.factory()).create().register().data
        }

        fun menu(initializer: MenuSetting.MenuBuildInfo.() -> Unit) {
            MenuSetting.MenuBuildInfo(context.resources).apply(initializer).build(MenuSetting.factory()).create().register()
        }
    }

    class Category(context: Context, builders: SettingsBuilders<DisplayableSettingInfo, SettingCategory, SettingMenu, Setting, Category, Menu>) :
        MCategory<DisplayableSettingInfo, SettingCategory, SettingMenu, Setting, Category, Menu>(context, builders)

    class Menu(builders: SettingsBuilders<DisplayableSettingInfo, SettingCategory, SettingMenu, Setting, Category, Menu>) :
        MMenu<DisplayableSettingInfo, SettingCategory, SettingMenu, Setting, Category, Menu>(builders)

    companion object {
        fun build(
            context: Context,
            initializer: Category.() -> Unit
        ): Settings {
            val builders = SettingsBuilders<DisplayableSettingInfo, SettingCategory, SettingMenu, Setting, Category, Menu>(
                { Setting(PreferenceManager.getDefaultSharedPreferences(context), context, it) },
                { Category(context, it) },
                { Menu(it) },
                { name, setting -> SettingCategory(name, setting) },
                { name, menu -> SettingMenu(name, menu) }
            )
            return org.secuso.pfacore.model.preferences.settings.Settings.build<DisplayableSettingInfo, SettingCategory, SettingMenu, Setting, Category, Menu, Settings>(
                { Settings(it) },
                builders,
                initializer
            )
        }
    }
}

interface DisplayableSettingInfo: Info, Displayable