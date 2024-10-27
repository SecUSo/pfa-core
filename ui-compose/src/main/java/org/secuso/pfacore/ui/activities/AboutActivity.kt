package org.secuso.pfacore.ui.activities

import android.os.Bundle
import androidx.compose.runtime.Composable
import org.secuso.pfacore.R
import org.secuso.pfacore.ui.PFApplication
import org.secuso.pfacore.ui.about.About

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.title.value = getString(R.string.activity_about_title)
    }

    @Composable
    override fun Content(application: PFApplication) {
        About(data = application.data.about)
    }
}