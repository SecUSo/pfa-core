package org.secuso.pfacore.application

import androidx.lifecycle.LiveData
import org.secuso.pfacore.model.Theme
import org.secuso.pfacore.model.about.About
import org.secuso.pfacore.model.help.Help
import org.secuso.pfacore.model.preferences.Preferable
import org.secuso.pfacore.model.preferences.settings.ISettings
import org.secuso.pfacore.model.tutorial.Tutorial

data class PFData(
    val settings: ISettings<*>,
    val about: About,
    val help: Help<*>,
    val tutorial: Tutorial<*>,
    val theme: LiveData<Theme>,
    val firstLaunch: Preferable<Boolean>,
    val includeDeviceDataInReport: Preferable<Boolean>
)