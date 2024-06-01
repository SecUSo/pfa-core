package org.secuso.pfacore.ui.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.view.preferences.settings.Settings
import org.secuso.ui.view.R

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val settings = PFApplication.instance.data.settings
        if (settings !is Settings) {
            throw IllegalStateException("The application setting is of type ${settings::class.java} but expected ${Settings::class.java}")
        }
        supportFragmentManager.beginTransaction().add(R.id.fragment, settings.build(R.id.fragment, supportFragmentManager), null).commit()
    }
}