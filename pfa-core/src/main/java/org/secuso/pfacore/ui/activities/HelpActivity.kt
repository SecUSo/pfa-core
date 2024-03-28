package org.secuso.pfacore.ui.activities

import androidx.compose.runtime.Composable
import org.secuso.pfacore.model.PFApplication

class HelpActivity : BaseActivity() {

    @Composable
    override fun Content(application: PFApplication) {
        application.Help.composable()
    }
}