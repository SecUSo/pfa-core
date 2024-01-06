package org.secuso.privacyfriendlycore.ui.activities

import androidx.compose.runtime.Composable
import org.secuso.privacyfriendlycore.model.PFApplication
import org.secuso.privacyfriendlycore.ui.SettingsMenu

class SettingsActivity: BaseActivity() {
    
    @Composable
    override fun Content(application: PFApplication) {
        SettingsMenu(settings = application.Settings)
    }
}