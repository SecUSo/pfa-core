package org.secuso.pfacore.ui.preferences.settings

import org.secuso.pfacore.R

fun InflatableCategory.general(initializer: InflatableSetting.() -> Unit) {
    this.category(R.string.settings_category_general, initializer)
}

fun InflatableCategory.appearance(initializer: InflatableSetting.() -> Unit) {
    this.category(R.string.settings_category_appearance, initializer)
}