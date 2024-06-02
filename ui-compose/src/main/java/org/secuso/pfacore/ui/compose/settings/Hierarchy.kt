package org.secuso.pfacore.ui.compose.settings

import org.secuso.pfacore.model.settings.SettingCategory as MSettingCategory
import org.secuso.pfacore.model.settings.SettingMenu as MSettingMenu

class SettingCategory(name: String, categorySettings: Settings.Setting) : MSettingCategory<DisplayableSettingInfo>(name, categorySettings.settings)
class SettingMenu(name: String, menu: Settings.Menu) : MSettingMenu<DisplayableSettingInfo, SettingCategory>(name, menu.setting!!, menu.settings)