package org.secuso.pfacore.ui.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import org.secuso.pfacore.application.PFApplication

open class BaseActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(when (PFApplication.instance.data.lightMode.value) {
            true -> AppCompatDelegate.MODE_NIGHT_YES
            false -> AppCompatDelegate.MODE_NIGHT_NO
            null -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        })
    }
}