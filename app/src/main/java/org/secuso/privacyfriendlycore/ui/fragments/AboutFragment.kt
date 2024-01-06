package org.secuso.privacyfriendlycore.ui.fragments

import androidx.compose.runtime.Composable
import org.secuso.privacyfriendlycore.R
import org.secuso.privacyfriendlycore.ui.About
import org.secuso.privacyfriendlycore.ui.AboutData

class AboutFragment(
    private val name: String,
    private val version: String,
    private val authors: String,
    private val repo: String
) : BaseFragment() {
    override val layout: Int
        get() = R.layout.fragment_about

    override val component: Int
        get() = R.id.compose_view

    override fun content() = @Composable { About(data = AboutData(name = name, version = version, authors = authors, repo = repo)) }

}