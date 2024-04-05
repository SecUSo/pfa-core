package org.secuso.pfacore.application

import androidx.lifecycle.LiveData
import org.secuso.pfacore.model.about.About
import org.secuso.pfacore.model.help.Help
import org.secuso.pfacore.model.settings.ISetting
import org.secuso.pfacore.model.settings.ISettings

data class PFData(
    val settings: ISettings<*>,
    val about: About,
    val help: Help<*>,
    val lightMode: LiveData<Boolean>
)