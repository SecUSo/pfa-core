package org.secuso.privacyfriendlycore.ui.fragments

import androidx.compose.runtime.Composable
import org.secuso.privacyfriendlycore.R
import org.secuso.privacyfriendlycore.ui.About
import org.secuso.privacyfriendlycore.ui.AboutData
import org.secuso.privacyfriendlycore.ui.SettingsMenu
import org.secuso.privacyfriendlycore.ui.settings.Settings

class SettingsFragment(
    private val settings: Settings
): BaseFragment() {
    override val layout: Int
        get() = R.layout.fragment_about

    override val component: Int
        get() = R.id.compose_view
    override fun content() = @Composable { SettingsMenu(settings = settings) }

}