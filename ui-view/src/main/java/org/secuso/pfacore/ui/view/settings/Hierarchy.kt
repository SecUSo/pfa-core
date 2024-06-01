package org.secuso.pfacore.ui.view.settings

import org.secuso.pfacore.model.settings.SettingCategory
import org.secuso.pfacore.model.settings.SettingComposite
import org.secuso.pfacore.model.settings.SettingMenu

class SettingCategory(name: String, category: Settings.Category) : SettingCategory<InflatableSetting, SettingComposite<InflatableSetting>>(name, category.settings)
class SettingMenu(name: String, val menu: Settings.Menu) : SettingMenu<InflatableSetting, SettingComposite<InflatableSetting>>(name, menu.settings)