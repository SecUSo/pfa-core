package org.secuso.pfacore.ui.fragments

import org.secuso.pfacore.R
import org.secuso.pfacore.ui.settings.Settings

class SettingsFragment(
    private val settings: Settings
) : BaseFragment() {
    override val layout: Int
        get() = R.layout.fragment_about

    override val component: Int
        get() = R.id.compose_view


    override fun content() = settings.composable

}