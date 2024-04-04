package org.secuso.pfacore.ui.compose

import androidx.compose.runtime.Composable

interface Displayable {
    @Composable
    fun Display(onClick: (() -> Unit)? = null) {
    }
}