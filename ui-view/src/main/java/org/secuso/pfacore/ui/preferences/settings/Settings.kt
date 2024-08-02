package org.secuso.pfacore.ui.preferences.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import org.secuso.pfacore.model.preferences.settings.SettingEntry
import org.secuso.pfacore.model.preferences.Info
import org.secuso.pfacore.model.preferences.settings.Settings as MSettings
import org.secuso.pfacore.model.preferences.settings.Settings.Category as MCategory
import org.secuso.pfacore.model.preferences.settings.Settings.Menu as MMenu
import org.secuso.pfacore.model.preferences.settings.Settings.Setting as MSetting
import org.secuso.pfacore.ui.Inflatable
import org.secuso.pfacore.ui.preferences.settings.components.SettingsMenuFragment

class Settings(
    private val settings: List<SettingCategory>,
) : MSettings<InflatableSetting, SettingCategory, SettingMenu>(settings) {

    fun build(viewId: Int, fragmentManager: FragmentManager) = SettingsMenuFragment().apply {
        categories = settings;
        openMenu = { fragmentManager.beginTransaction().replace(viewId, it).addToBackStack(null).commit() }
    }
    class Setting(
        preferences: SharedPreferences,
        val context: Context,
        builders: SettingsBuilders<InflatableSetting, SettingCategory, SettingMenu, Setting, Category, Menu>
    ) : MSetting<InflatableSetting, SettingCategory, SettingMenu, Setting, Category, Menu>(
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

    class Category(context: Context, builders: SettingsBuilders<InflatableSetting, SettingCategory, SettingMenu, Setting, Category, Menu>) :
        MCategory<InflatableSetting, SettingCategory, SettingMenu, Setting, Category, Menu>(context, builders)

    class Menu(builders: SettingsBuilders<InflatableSetting, SettingCategory, SettingMenu, Setting, Category, Menu>) :
        MMenu<InflatableSetting, SettingCategory, SettingMenu, Setting, Category, Menu>(builders)

    companion object {
        fun build(
            context: Context,
            initializer: Category.() -> Unit
        ): Settings {
            val builders = SettingsBuilders<InflatableSetting, SettingCategory, SettingMenu, Setting, Category, Menu>(
                { Setting(PreferenceManager.getDefaultSharedPreferences(context), context, it) },
                { Category(context, it) },
                { Menu(it) },
                { name, setting -> SettingCategory(name, setting) },
                { name, menu -> SettingMenu(name, menu) }
            )
            return org.secuso.pfacore.model.preferences.settings.Settings.build<InflatableSetting, SettingCategory, SettingMenu, Setting, Category, Menu, Settings>(
                { Settings(it) },
                builders,
                initializer
            )
        }
    }
}

interface InflatableSetting: Info {

    val enabled: LiveData<Boolean>
        get() = MutableLiveData(true)
    val expandable: Boolean
    val action: Inflatable?
        get() = null
    val title: Inflatable
    val description: Inflatable?

    /**
     * Use another icon to display the expand-toggle state. Returning null uses the default icons.
     */
    fun expandableIcon(expanded: Boolean): Int? = null
}