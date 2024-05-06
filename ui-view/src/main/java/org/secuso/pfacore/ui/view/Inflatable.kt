package org.secuso.pfacore.ui.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

fun interface Inflatable {
    fun inflate(inflater: LayoutInflater, root: ViewGroup, owner: LifecycleOwner): View
}

internal fun View.replace(inflater: LayoutInflater, owner: LifecycleOwner, inflatable: Inflatable) {
    val parent = this.parent as ViewGroup
    val index = parent.indexOfChild(this)
    parent.removeViewAt(index)
    parent.addView(inflatable.inflate(inflater, parent, owner), index)
}