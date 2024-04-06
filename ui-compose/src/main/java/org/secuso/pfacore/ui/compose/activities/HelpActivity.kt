package org.secuso.pfacore.ui.compose.activities

import androidx.compose.runtime.Composable
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.compose.Displayable

class HelpActivity : BaseActivity() {

    @Composable
    override fun Content(application: PFApplication) {
        (application.data.help as Displayable).Display {}
    }
}