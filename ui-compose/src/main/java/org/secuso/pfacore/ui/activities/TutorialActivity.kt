package org.secuso.pfacore.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.search.SearchView.Behavior
import org.secuso.pfacore.activities.SplashActivity
import org.secuso.pfacore.ui.PFApplication
import org.secuso.pfacore.ui.theme.secusoAccent
import org.secuso.pfacore.ui.tutorial.TutorialComp

class TutorialActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).hide(WindowInsetsCompat.Type.navigationBars())

        val tutorial = PFApplication.instance.data.tutorial
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
        setContent {
            val accentColor = MaterialTheme.colorScheme.secusoAccent.toArgb()
            SideEffect {
                window.statusBarColor = accentColor
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    hide(WindowInsetsCompat.Type.navigationBars())
                }
            }
            TutorialComp(tutorial)
        }
    }
}