package org.secuso.pfacore.ui.preferences

import android.content.Context
import org.secuso.pfacore.model.preferences.Preferences
import org.secuso.pfacore.ui.preferences.settings.InflatableCategory
import org.secuso.pfacore.ui.preferences.settings.Settings

fun appPreferences(context: Context, initializer: Preferences<InflatableCategory, Settings>.() -> Unit): Preferences<InflatableCategory, Settings> {
    return Preferences.build(context = context, initializer =  initializer, factory = { ctx, init -> Settings.build(ctx, init) } )
}