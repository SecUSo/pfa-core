package org.secuso.pfacore.ui.activities

import androidx.compose.runtime.Composable
import org.secuso.pfacore.R
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.Displayable

class HelpActivity : BaseActivity() {

    init {
        super.title.value = getString(R.string.activity_help_title)
    }

    @Composable
    override fun Content(application: PFApplication) {
        (application.data.help as Displayable).Display {}
    }
}