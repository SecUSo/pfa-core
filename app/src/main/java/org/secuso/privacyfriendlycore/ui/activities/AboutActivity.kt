package org.secuso.privacyfriendlycore.ui.activities

import androidx.compose.runtime.Composable
import org.secuso.privacyfriendlycore.model.PFApplication
import org.secuso.privacyfriendlycore.ui.About

class AboutActivity : BaseActivity() {
    @Composable
    override fun Content(application: PFApplication) {
        About(data = application.About)
    }
}