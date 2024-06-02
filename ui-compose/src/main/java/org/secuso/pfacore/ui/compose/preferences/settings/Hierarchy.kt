package org.secuso.pfacore.ui.compose.preferences.settings

import org.secuso.pfacore.model.preferences.settings.SettingCategory as MSettingCategory
import org.secuso.pfacore.model.preferences.settings.SettingMenu as MSettingMenu

class SettingCategory(name: String, categorySettings: Settings.Setting) : MSettingCategory<DisplayableSettingInfo>(name, categorySettings.settings)
class SettingMenu(name: String, menu: Settings.Menu) : MSettingMenu<DisplayableSettingInfo, SettingCategory>(name, menu.setting!!, menu.settings)