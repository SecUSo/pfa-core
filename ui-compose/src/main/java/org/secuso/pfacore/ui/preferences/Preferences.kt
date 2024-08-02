package org.secuso.pfacore.ui.preferences

import android.content.Context
import org.secuso.pfacore.model.preferences.Preferences
import org.secuso.pfacore.ui.preferences.settings.Settings

fun appPreferences(context: Context, initializer: Preferences<Settings.Category, Settings>.() -> Unit): Preferences<Settings.Category, Settings> {
    return Preferences.build<Settings.Category, Settings>(context = context, initializer =  initializer, factory = { ctx, init -> Settings.build(ctx, init) } )
}