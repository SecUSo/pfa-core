package org.secuso.pfacore.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.activities.SplashActivity
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.tutorial.Tutorial
import org.secuso.pfacore.ui.tutorial.TutorialComp

class TutorialActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tutorial = PFApplication.instance.data.tutorial as Tutorial
        tutorial.onFinish = {
            val activity: Class<out Activity>? = tutorial.launchActivity ?: run {
                if (intent.extras?.getBoolean(SplashActivity.EXTRA_LAUNCH_MAIN_ACTIVITY_AFTER_TUTORIAL) == true) {
                    PFApplication.instance.mainActivity
                } else {
                    null
                }
            }
            if (activity != null) {
                startActivity(tutorial.extras(Intent(this@TutorialActivity, activity)))
            }
            finish()
        }
        setContent { TutorialComp(tutorial) }
    }
}