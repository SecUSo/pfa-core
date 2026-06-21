package org.secuso.pfacore.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

fun interface Inflatable {
    fun inflate(inflater: LayoutInflater, root: ViewGroup?, owner: LifecycleOwner): View
}

internal fun View.replace(inflater: LayoutInflater, owner: LifecycleOwner, inflatable: org.secuso.pfacore.ui.Inflatable): View {
    val parent = this.parent as? ViewGroup ?: run {
        // Parent should never be null, but sometimes it is (assuming due to lifecycle issues
        // If this is the case, postpone the call until the parent is set again.
        val newView = inflatable.inflate(inflater, null, owner)
        post {
            this.replace(newView)
        }
        return newView
    }
    val index = parent.indexOfChild(this)
    parent.removeViewAt(index)
    val newView = inflatable.inflate(inflater, parent, owner)
    parent.addView(newView, index)
    return newView
}

internal fun View.replace(view: View) {
    val parent = this.parent as? ViewGroup ?: run {
        // Parent should never be null, but sometimes it is (assuming due to lifecycle issues
        // If this is the case, postpone the call until the parent is set again.
        post { this.replace(view) }
        return
    }
    val index = parent.indexOfChild(this)
    parent.removeViewAt(index)
    parent.addView(view)
}