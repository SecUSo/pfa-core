package org.secuso.pfacore.ui.activities

import androidx.compose.runtime.Composable
import org.secuso.pfacore.model.PFApplication
import org.secuso.pfacore.ui.About

class AboutActivity : BaseActivity() {

    @Composable
    override fun Content(application: PFApplication) {
        About(data = application.About)
    }
}