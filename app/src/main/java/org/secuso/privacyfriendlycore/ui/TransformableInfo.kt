package org.secuso.privacyfriendlycore.ui

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class BasicInfo(
    private val resources: Resources,
    private val default: (String) -> @Composable (Modifier) -> Unit
) {
    private var composable: (@Composable (Modifier) -> Unit)? = null

    fun build() = this.composable!!

    @Suppress("Unused")
    fun resource(id: Int) {
        this.composable = default(resources.getString(id))
    }

    @Suppress("Unused")
    fun literal(text: String) {
        this.composable = default(text)
    }

    @Suppress("Unused")
    fun custom(composable: (@Composable (Modifier) -> Unit)) {
        this.composable = composable
    }
}

class TransformableInfo<D, S>(
    private val resources: Resources,
    private val default: ((D, S) -> String) -> (@Composable (D, S, Modifier) -> Unit)
) {
    private var composable: (@Composable (D, S, Modifier) -> Unit)? = null

    fun build() = this.composable!!

    @Suppress("Unused")
    fun resource(id: Int) {
        this.composable = default { _, _ -> resources.getString(id)}
    }

    @Suppress("Unused")
    fun literal(text: String) {
        this.composable = default { _, _ -> text}
    }

    @Suppress("Unused")
    fun transform(transformer: (D, S) -> String) {
        this.composable = default(transformer)
    }

    @Suppress("Unused")
    fun custom(composable: (@Composable (D, S, Modifier) -> Unit)) {
        this.composable = composable
    }
}
