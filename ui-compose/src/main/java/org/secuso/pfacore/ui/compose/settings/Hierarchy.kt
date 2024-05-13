package org.secuso.pfacore.ui.compose.settings

import org.secuso.pfacore.model.settings.SettingComposite
import org.secuso.pfacore.model.settings.SettingCategory
import org.secuso.pfacore.model.settings.SettingMenu

class SettingCategory(name: String, category: Settings.Category) : SettingCategory<DisplayableSettingInfo>(name, category.settings)
class SettingMenu(name: String, val menu: Settings.Menu) : SettingMenu<DisplayableSettingInfo>(name, menu.settings as List<SettingCategory<DisplayableSettingInfo>>)