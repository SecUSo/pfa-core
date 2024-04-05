package org.secuso.pfacore.ui.compose.activities

import androidx.compose.runtime.Composable
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.compose.about.About

class AboutActivity : BaseActivity() {

    @Composable
    override fun Content(application: PFApplication) {
        About(data = application.data.about)
    }
}