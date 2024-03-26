package org.secuso.privacyfriendlycore.ui.settings

import androidx.compose.runtime.Composable
import org.secuso.privacyfriendlycore.ui.stage.Stage

interface ISettings : Stage {
    @Suppress("unused")
    val all: List<Setting<*>>

    // For some reason this override is necessary in the current configuration
    // Otherwise using ISettings results in a call to setComposable which crashes the app
    override val composable: @Composable () -> Unit
}