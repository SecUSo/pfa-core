package org.secuso.pfacore.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.model.permission.PFAPermissionOwner

abstract class PFActivity: AppCompatActivity(), PFAPermissionOwner {
    private val permissionCallbacks: MutableList<() -> Unit> = mutableListOf()

    override fun registerPFAPermissionInitialization(action: () -> Unit) {
        permissionCallbacks.add(action)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (callback in permissionCallbacks) {
            callback()
        }
    }
}