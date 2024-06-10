package org.secuso.pfacore.ui.compose.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.compose.tutorial.Tutorial

class TutorialActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tutorial = PFApplication.instance.data.tutorial as Tutorial
        setContent { tutorial.Display {  } }
    }
}