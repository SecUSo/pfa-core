package org.secuso.pfacore.ui.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.application.PFApplication

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PFApplication.instance.data.theme.observe(this) { it.apply() }
    }
}