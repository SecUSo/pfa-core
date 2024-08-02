package org.secuso.pfacore.ui.activities

import androidx.compose.runtime.Composable
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.about.About

class AboutActivity : BaseActivity() {

    @Composable
    override fun Content(application: PFApplication) {
        About(data = application.data.about)
    }
}