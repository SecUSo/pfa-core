package org.secuso.pfacore.ui.activities

import android.os.Bundle
import androidx.compose.runtime.Composable
import org.secuso.pfacore.R
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.Displayable

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.title.value = getString(R.string.activity_settings_title)
    }

    @Composable
    override fun Content(application: PFApplication) = (application.data.settings as Displayable).Display {}

}