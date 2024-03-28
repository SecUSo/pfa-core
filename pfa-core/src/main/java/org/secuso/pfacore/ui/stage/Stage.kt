package org.secuso.pfacore.ui.stage

import androidx.compose.runtime.Composable

interface Stage {
    val composable: @Composable () -> Unit

    interface Builder<S : Stage> {
        fun build(): S
    }

    companion object {
        @Suppress("Unused")
        fun <S : Stage, B : Builder<S>> build(builder: B, initializer: B.() -> Unit): S {
            return builder.apply(initializer).build()
        }
    }
}