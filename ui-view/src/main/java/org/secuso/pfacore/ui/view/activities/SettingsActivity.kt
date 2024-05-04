package org.secuso.pfacore.ui.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.view.settings.Settings
import org.secuso.ui.view.databinding.ActivitySettingsBinding

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = PFApplication.instance.data.settings
        if (settings !is Settings) {
            throw IllegalStateException("The application setting is of type ${settings::class.java} but expected ${Settings::class.java}")
        }
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        supportFragmentManager.beginTransaction().add(settings.build(), null).commit()
        setContentView(binding.root)
    }
}