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