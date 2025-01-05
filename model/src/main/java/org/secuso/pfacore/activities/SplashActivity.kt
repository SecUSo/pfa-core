/*
 This file is part of Privacy Friendly App Core Library.

 Privacy Friendly App Core Library is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly App Example is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly App Example. If not, see <http://www.gnu.org/licenses/>.
 */
package org.secuso.pfacore.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.application.PFModelApplication

/**
 * SplashScreen when the App is started.
 * It will check if the app was started before and start the Tutorial or the MainActivity.
 * The logo for the Splashscreen is set in the style for it.
 *
 * Originally Created on 22.10.16.
 * Ported to PFA-Core in 2024.
 * @author Karola Marky (yonjuni), Christopher Beckmann (Kamuno), Patrick Schneider
 *
 */
open class SplashActivity(val tutorial: Class<out Activity>) : AppCompatActivity() {

    companion object {
        const val EXTRA_LAUNCH_MAIN_ACTIVITY_AFTER_TUTORIAL = "launchMainActivityAfterTutorial"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firstLaunchPref = PFModelApplication.instance.data.firstLaunch
        val mainIntent = if (firstLaunchPref.value == null || firstLaunchPref.value == true) {
            firstLaunchPref.value = false
            Intent(this, tutorial).putExtra(EXTRA_LAUNCH_MAIN_ACTIVITY_AFTER_TUTORIAL, true)
        } else {
            Intent(this, PFModelApplication.instance.mainActivity)
        }

        startActivity(mainIntent)
        finish()
    }
}
