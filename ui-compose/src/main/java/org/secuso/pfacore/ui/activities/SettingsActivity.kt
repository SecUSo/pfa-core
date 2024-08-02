package org.secuso.pfacore.ui.activities

import androidx.compose.runtime.Composable
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.Displayable

class SettingsActivity : BaseActivity() {

    @Composable
    override fun Content(application: PFApplication) = (application.data.settings as Displayable).Display {}

}