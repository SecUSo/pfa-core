package org.secuso.pfacore.ui

import android.content.res.Resources

class BasicInfo(
    private val resources: Resources,
    private val default: (CharSequence) -> org.secuso.pfacore.ui.Inflatable
) {
    private var composable: (org.secuso.pfacore.ui.Inflatable)? = null

    fun build() = this.composable!!

    @Suppress("Unused")
    fun resource(id: Int) {
        this.composable = default(resources.getString(id))
    }

    @Suppress("Unused")
    fun literal(text: CharSequence) {
        this.composable = default(text)
    }

    @Suppress("Unused")
    fun custom(composable: org.secuso.pfacore.ui.Inflatable) {
        this.composable = composable
    }
}

class TransformableInfo<D, S>(
    private val resources: Resources,
    private val default: ((D, S) -> CharSequence) -> (D, S) -> org.secuso.pfacore.ui.Inflatable
) {
    private var composable: ((D, S) -> org.secuso.pfacore.ui.Inflatable)? = null

    fun build() = this.composable!!

    @Suppress("Unused")
    fun resource(id: Int) {
        this.composable = default { _, _ -> resources.getString(id) }
    }

    @Suppress("Unused")
    fun literal(text: CharSequence) {
        this.composable = default { _, _ -> text }
    }

    @Suppress("Unused")
    fun transform(transformer: (D, S) -> CharSequence) {
        this.composable = default(transformer)
    }

    @Suppress("Unused")
    fun custom(composable: (D, S) -> org.secuso.pfacore.ui.Inflatable) {
        this.composable = composable
    }
}