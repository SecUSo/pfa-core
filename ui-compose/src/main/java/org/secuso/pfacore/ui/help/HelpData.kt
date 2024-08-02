package org.secuso.pfacore.ui.help

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.secuso.pfacore.model.help.IHelpData
import org.secuso.pfacore.ui.Displayable

class HelpData(
    internal val title: @Composable (Modifier) -> Unit,
    internal val description: @Composable (Modifier) -> Unit,
) : IHelpData<HelpData>, Displayable {
    @Composable
    override fun Display(onClick: (() -> Unit)?) {
        HelpMenuItem(title, description, Modifier)
    }
}