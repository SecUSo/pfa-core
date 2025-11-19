package org.secuso.pfacore.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import org.secuso.pfacore.model.permission.PFAPermissionLifecycleCallbacks
import org.secuso.pfacore.model.permission.PFAPermissionOwner
import org.secuso.pfacore.ui.PFApplication
import org.secuso.ui.view.databinding.ActivityBaseBinding

open class BaseActivity(val base: Boolean = true): AppCompatActivity(), PFAPermissionOwner {

    open val parentActivity: Class<out Activity> = PFApplication.instance.mainActivity
    private lateinit var binding: ActivityBaseBinding
    private val permissionCallbacks: MutableList<() -> Unit> = mutableListOf()

    override fun registerPFAPermissionInitialization(action: () -> Unit) {
        permissionCallbacks.add(action)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (callback in permissionCallbacks) {
            callback()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (!PFApplication.instance.data.theme.hasActiveObservers()) {
            PFApplication.instance.data.theme.observe(this) { it.apply() }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                TaskStackBuilder.create(this)
                    .addParentStack(parentActivity)
                    .addNextIntent(Intent(this, parentActivity))
                    // TaskStackBuilder::startActivities adds the Intent.FLAG_ACTIVITY_CLEAR_TASK which will add a transition animation.
                    // We don't want that.
                    .intents.apply {
                        if (isNotEmpty()) {
                            this[0].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
                        }
                        startActivities(this)
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initContent() {
        binding = ActivityBaseBinding.inflate(layoutInflater)
        super.setContentView(binding.root)

        if (supportActionBar == null ) {
            setSupportActionBar(findViewById(org.secuso.ui.view.R.id.toolbar))
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun setContentView(view: View) {
        if (base) {
            initContent()
            binding.content.addView(view)
        } else {
            super.setContentView(view)
        }
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        if (base) {
            initContent()
            layoutInflater.inflate(layoutResID, binding.content, true)
        } else {
            super.setContentView(layoutResID)
        }

    }
}