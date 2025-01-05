package org.secuso.pfacore.model

import androidx.appcompat.app.AppCompatDelegate

/**
 * Supported Theme-Options.
 *
 * @author Patrick Schneider.
 */
enum class Theme {
    SYSTEM,
    LIGHT,
    DARK;
//    YOU;

    fun apply() {
        when (this) {
            SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            YOU -> DynamicColors.applyToActivitiesIfAvailable(PFApplication.instance)
        }
    }
}