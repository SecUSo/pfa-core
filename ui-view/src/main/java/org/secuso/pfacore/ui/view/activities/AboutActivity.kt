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
package org.secuso.pfacore.ui.view.activities

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.R
import org.secuso.pfacore.application.PFApplication
import org.secuso.ui.view.databinding.ActivityAboutBinding
import java.util.regex.Pattern

/**
 * Activity that gives information about the developers.
 */
class AboutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAboutBinding.inflate(layoutInflater)
        binding.data = PFApplication.instance.data.about
        binding.aboutGithubUrl.text = SpannableString(getString(R.string.about_github)).apply {
            setSpan(URLSpan(PFApplication.instance.data.about.repo), 0, this.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.aboutGithubUrl.movementMethod = LinkMovementMethod.getInstance()
        binding.aboutSecusoWebsite.movementMethod = LinkMovementMethod.getInstance()
        setContentView(binding.root)
    }
}