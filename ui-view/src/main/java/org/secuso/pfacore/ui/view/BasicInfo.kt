package org.secuso.pfacore.ui.view

import android.content.res.Resources

class BasicInfo(
    private val resources: Resources,
    private val default: (String) -> Inflatable
) {
    private var composable: (Inflatable)? = null

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
    fun custom(composable: Inflatable) {
        this.composable = composable
    }
}

class TransformableInfo<D, S>(
    private val resources: Resources,
    private val default: ((D, S) -> String) -> (D, S) -> Inflatable
) {
    private var composable: ((D, S) -> Inflatable)? = null

    fun build() = this.composable!!

    @Suppress("Unused")
    fun resource(id: Int) {
        this.composable = default { _, _ -> resources.getString(id) }
    }

    @Suppress("Unused")
    fun literal(text: String) {
        this.composable = default { _, _ -> text }
    }

    @Suppress("Unused")
    fun transform(transformer: (D, S) -> String) {
        this.composable = default(transformer)
    }

    @Suppress("Unused")
    fun custom(composable: (D, S) -> Inflatable) {
        this.composable = composable
    }
}