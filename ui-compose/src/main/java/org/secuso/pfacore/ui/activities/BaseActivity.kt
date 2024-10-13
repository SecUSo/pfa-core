package org.secuso.pfacore.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.TaskStackBuilder
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.theme.navbar

abstract class BaseActivity : AppCompatActivity() {

    open val parentActivity: Class<out Activity> = PFApplication.instance.mainActivity

    @Composable
    abstract fun Content(application: PFApplication)

    @Composable
    open fun Actions() {

    }

    protected val title: MutableState<String> = mutableStateOf(PFApplication.instance.name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = PFApplication.instance
        setContent {
            WithTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = title,
                            onNavigationClick = {
                                TaskStackBuilder.create(this)
                                    .addParentStack(parentActivity)
                                    .addNextIntent(Intent(this, parentActivity))
                                    .intents.apply {
                                        if (isNotEmpty()) {
                                            this[0].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
                                        }
                                        startActivities(this)
                                    }
                            },
                            actions = { Actions() }
                        )
                    }
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        Content(application)
                    }
                }
            }
        }
    }
}