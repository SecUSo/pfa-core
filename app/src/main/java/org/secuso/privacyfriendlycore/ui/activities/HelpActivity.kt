package org.secuso.privacyfriendlycore.ui.activities

import androidx.compose.runtime.Composable
import org.secuso.privacyfriendlycore.model.PFApplication
import org.secuso.privacyfriendlycore.ui.About

class HelpActivity : BaseActivity() {

    @Composable
    override fun Content(application: PFApplication) {
        application.Help.composable()
    }
}