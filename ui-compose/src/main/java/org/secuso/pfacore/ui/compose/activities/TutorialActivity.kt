package org.secuso.pfacore.ui.compose.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.compose.tutorial.Tutorial

class TutorialActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tutorial = PFApplication.instance.data.tutorial as Tutorial
        tutorial.onFinish = {
            if (tutorial.launchActivity != null) {
                startActivity(tutorial.extras(Intent(this@TutorialActivity, tutorial.launchActivity!!)))
            }
            finish()
        }
        setContent { tutorial.Display {  } }
    }
}