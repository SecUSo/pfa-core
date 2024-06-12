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
package org.secuso.pfacore.ui.compose.activities

import org.secuso.pfacore.activities.SplashActivity

/**
 * SplashScreen when the App is started.
 * It will check if the app was started before and start the Tutorial or the MainActivity.
 * The logo for the Splashscreen is set in the style for it.
 *
 * Patrick Schneider
 */
class SplashActivity : SplashActivity(TutorialActivity::class.java)
