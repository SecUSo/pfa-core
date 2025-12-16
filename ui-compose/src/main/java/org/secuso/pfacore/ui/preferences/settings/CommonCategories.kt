package org.secuso.pfacore.ui.preferences.settings
import org.secuso.pfacore.ui.preferences.settings.Settings

import org.secuso.pfacore.R

fun DisplayableCategory.general(initializer: DisplayableSetting.() -> Unit) {
    this.category(R.string.settings_category_general, initializer)
}

fun DisplayableCategory.appearance(initializer: DisplayableSetting.() -> Unit) {
    this.category(R.string.settings_category_appearance, initializer)
}