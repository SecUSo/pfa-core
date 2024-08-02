package org.secuso.pfacore.ui

import androidx.compose.runtime.Composable

interface Displayable {
    @Composable
    fun Display(onClick: (() -> Unit)?)
}