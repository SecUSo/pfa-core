package org.secuso.pfacore.ui.activities

import android.os.Bundle
import androidx.compose.runtime.Composable
import org.secuso.pfacore.R
import org.secuso.pfacore.ui.Displayable
import org.secuso.pfacore.ui.PFApplication

class HelpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.title.value = getString(R.string.activity_help_title)
    }

    @Composable
    override fun Content(application: PFApplication) {
        (application.data.help as Displayable).Display {}
    }
}