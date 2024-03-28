package org.secuso.pfacore.ui.activities

import androidx.compose.runtime.Composable
import org.secuso.pfacore.model.PFApplication

class SettingsActivity : BaseActivity() {

    @Composable
    override fun Content(application: PFApplication) = application.Settings.composable()

}