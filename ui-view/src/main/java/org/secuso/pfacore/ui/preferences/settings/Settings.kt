package org.secuso.pfacore.ui.preferences.settings

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.secuso.pfacore.model.preferences.settings.SettingEntry
import org.secuso.pfacore.model.preferences.Info
import org.secuso.pfacore.model.preferences.settings.SettingCategory
import org.secuso.pfacore.model.preferences.settings.SettingMenu
import org.secuso.pfacore.model.preferences.settings.Settings as MSettings
import org.secuso.pfacore.ui.Inflatable
import org.secuso.pfacore.ui.preferences.settings.components.SettingsMenuFragment

class Settings(
    private val settings: List<SettingCategory<InflatableSettingInfo>>,
) : MSettings<InflatableSettingInfo>(settings) {

    fun build(viewId: Int, fragmentManager: FragmentManager) = SettingsMenuFragment().apply {
        categories = settings;
        openMenu = { fragmentManager.beginTransaction().replace(viewId, it).addToBackStack(null).commit() }
    }

    companion object {
        fun build(
            context: Context,
            initializer: Category<InflatableSettingInfo>.() -> Unit
        ): Settings {
            return MSettings.build<InflatableSettingInfo, Settings>(
                context,
                { Settings(it) },
                initializer
            )
        }
    }
}

fun InflatableSetting.switch(initializer: SwitchSetting.SwitchBuildInfo.() -> Unit): SwitchSetting.SwitchData {
    return SwitchSetting.SwitchBuildInfo(context.resources).apply(initializer).build(SwitchSetting.factory()).create().register().data
}

fun <T> InflatableSetting.radio(initializer: RadioSetting.RadioBuildInfo<T>.() -> Unit): RadioSetting.RadioData<T> {
    return RadioSetting.RadioBuildInfo(context.resources, listOf<SettingEntry<T>>()).apply(initializer).build(RadioSetting.factory()).create().register().data
}

fun InflatableSetting.menu(initializer: MenuSetting.MenuBuildInfo.() -> Unit) {
    MenuSetting.MenuBuildInfo(context.resources).apply(initializer).build(MenuSetting.factory()).create().register()
}

interface InflatableSettingInfo: Info {

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

typealias InflatableSetting = MSettings.Setting<InflatableSettingInfo>
typealias InflatableCategory = MSettings.Category<InflatableSettingInfo>
typealias InflatableMenu = MSettings.Menu<InflatableSettingInfo>
typealias InflatableSettingCategory = SettingCategory<InflatableSettingInfo>
typealias InflatableSettingMenu = SettingMenu<InflatableSettingInfo, InflatableSettingCategory>
