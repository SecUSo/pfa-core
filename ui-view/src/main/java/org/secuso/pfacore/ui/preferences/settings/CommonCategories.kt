package org.secuso.pfacore.ui.preferences.settings

import org.secuso.pfacore.R

fun Settings.Category.general(initializer: Settings.Setting.() -> Unit) {
    this.category(R.string.settings_category_general, initializer)
}

fun Settings.Category.appearance(initializer: Settings.Setting.() -> Unit) {
    this.category(R.string.settings_category_appearance, initializer)
}