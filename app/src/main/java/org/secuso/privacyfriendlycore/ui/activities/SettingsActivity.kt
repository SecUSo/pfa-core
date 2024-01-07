package org.secuso.privacyfriendlycore.ui.activities

import androidx.compose.runtime.Composable
import org.secuso.privacyfriendlycore.model.PFApplication

class SettingsActivity : BaseActivity() {

    @Composable
    override fun Content(application: PFApplication) = application.Settings.composable()

}