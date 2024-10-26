package org.secuso.pfacore.ui.activities

import android.os.Bundle
import org.secuso.pfacore.ui.PFApplication
import org.secuso.ui.view.R

class SettingsActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val settings = PFApplication.instance.data.settings
        supportFragmentManager.beginTransaction().add(R.id.fragment, settings.build(R.id.fragment, supportFragmentManager), null).commit()
    }
}