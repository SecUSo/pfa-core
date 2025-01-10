package org.secuso.pfacore.ui.preferences.settings

import android.content.Context
import androidx.compose.runtime.Composable
import org.secuso.pfacore.model.preferences.settings.SettingEntry
import org.secuso.pfacore.model.preferences.Info
import org.secuso.pfacore.model.preferences.settings.SettingCategory
import org.secuso.pfacore.model.preferences.settings.SettingMenu
import org.secuso.pfacore.ui.Displayable
import org.secuso.pfacore.ui.preferences.settings.composables.SettingsMenu
import org.secuso.pfacore.model.preferences.settings.Settings as MSettings

class Settings(
    settings: List<DisplayableSettingCategory>,
    private val display: @Composable () -> Unit = @Composable { SettingsMenu(settings) }
) : MSettings<DisplayableSettingInfo>(settings), Displayable {

    @Composable
    override fun Display(onClick: (() -> Unit)?) {
        display()
    }

    companion object {
        fun build(
            context: Context,
            initializer: Category<DisplayableSettingInfo>.() -> Unit
        ): Settings {
            return MSettings.build<DisplayableSettingInfo, Settings>(
                context,
                { Settings(it) },
                initializer
            )
        }
    }
}

fun DisplayableSetting.switch(initializer: SwitchSetting.SwitchBuildInfo.() -> Unit): SwitchSetting.SwitchData {
    return SwitchSetting.SwitchBuildInfo(context.resources).apply(initializer).build(SwitchSetting.factory()).create().register().data
}

fun <T> DisplayableSetting.radio(initializer: RadioSetting.RadioBuildInfo<T>.() -> Unit): RadioSetting.RadioData<T> {
    return RadioSetting.RadioBuildInfo(context.resources, listOf<SettingEntry<T>>()).apply(initializer).build(RadioSetting.factory()).create().register().data
}

fun DisplayableSetting.menu(initializer: MenuSetting.MenuBuildInfo.() -> Unit) {
    MenuSetting.MenuBuildInfo(context.resources).apply(initializer).build(MenuSetting.factory()).create().register()
}

typealias DisplayableSetting = MSettings.Setting<DisplayableSettingInfo>
typealias DisplayableCategory = MSettings.Category<DisplayableSettingInfo>
typealias DisplayableMenu = MSettings.Menu<DisplayableSettingInfo>
typealias DisplayableSettingCategory = SettingCategory<DisplayableSettingInfo>
typealias DisplayableSettingMenu = SettingMenu<DisplayableSettingInfo, DisplayableSettingCategory>

interface DisplayableSettingInfo: Info, Displayable