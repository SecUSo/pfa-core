package org.secuso.pfacore.ui.view.settings

import org.secuso.pfacore.model.settings.SettingCategory as MSettingCategory
import org.secuso.pfacore.model.settings.SettingMenu as MSettingMenu

class SettingCategory(name: String, categorySettings: Settings.Setting) : MSettingCategory<InflatableSetting>(name, categorySettings.settings)
class SettingMenu(name: String, val menu: Settings.Menu) : MSettingMenu<InflatableSetting, SettingCategory>(name, menu.setting!!, menu.settings)