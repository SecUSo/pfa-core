/*
 This file is part of the library PFA-Core.
 PFA-Core is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.
 Privacy Friendly Notes is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Notes. If not, see <http://www.gnu.org/licenses/>.
 */
package org.secuso.ui.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.application.PFApplication
import org.secuso.ui.view.databinding.ActivityAboutBinding

/**
 * Activity that gives information about the developers.
 */
class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAboutBinding.inflate(layoutInflater)
        binding.data = PFApplication.instance.data.about
        setContentView(binding.root)
    }
}