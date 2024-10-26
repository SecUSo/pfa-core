package org.secuso.pfacore.ui

import org.secuso.pfacore.application.PFData as MPFData
import org.secuso.pfacore.application.PFModelApplication
import org.secuso.pfacore.ui.help.Help
import org.secuso.pfacore.ui.preferences.settings.Settings
import org.secuso.pfacore.ui.tutorial.Tutorial

typealias PFData = MPFData<Settings, Help, Tutorial>
abstract class PFApplication: PFModelApplication<PFData>() {
    companion object {
        val instance
            get() = PFModelApplication.instance as PFApplication
    }
}