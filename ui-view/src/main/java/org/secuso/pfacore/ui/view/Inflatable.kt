package org.secuso.pfacore.ui.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun interface Inflatable {
    fun inflate(inflater: LayoutInflater, root: ViewGroup): View
}

internal fun View.replace(inflater: LayoutInflater, inflatable: Inflatable) {
    val parent = this.parent as ViewGroup
    val index = parent.indexOfChild(this)
    parent.removeViewAt(index)
    parent.addView(inflatable.inflate(inflater, parent), index)
}