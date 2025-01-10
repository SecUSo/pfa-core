package org.secuso.pfacore.ui.preferences

import android.content.Context
import org.secuso.pfacore.model.preferences.Preferences
import org.secuso.pfacore.ui.preferences.settings.DisplayableCategory
import org.secuso.pfacore.ui.preferences.settings.DisplayableSettingInfo
import org.secuso.pfacore.ui.preferences.settings.Settings
import org.secuso.pfacore.model.preferences.settings.Settings as MSettings

fun appPreferences(context: Context, initializer: Preferences<DisplayableCategory, Settings>.() -> Unit): Preferences<DisplayableCategory, Settings> {
    return Preferences.build(context = context, initializer =  initializer, factory = { ctx, init -> Settings.build(ctx, init) } )
}