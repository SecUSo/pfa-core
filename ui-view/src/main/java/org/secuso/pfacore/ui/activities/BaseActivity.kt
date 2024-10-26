package org.secuso.pfacore.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import org.secuso.pfacore.application.PFApplication

open class BaseActivity: AppCompatActivity() {

    open val parentActivity: Class<out Activity> = PFApplication.instance.mainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}