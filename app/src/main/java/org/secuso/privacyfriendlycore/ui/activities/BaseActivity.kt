package org.secuso.privacyfriendlycore.ui.activities

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextOverflow
import org.secuso.privacyfriendlycore.model.PFApplication
import org.secuso.privacyfriendlycore.ui.theme.PrivacyFriendlyCoreTheme
import org.secuso.privacyfriendlycore.ui.theme.navbar

abstract class BaseActivity: ComponentActivity() {

    @Composable
    abstract fun content(application: PFApplication)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = PFApplication.instance(this)
        setContent {
            PrivacyFriendlyCoreTheme(
                useDarkTheme = isSystemInDarkTheme() && resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK  == Configuration.UI_MODE_NIGHT_YES && !application.LightMode
            ) {
                window.statusBarColor = MaterialTheme.colorScheme.navbar.toArgb()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = application.ApplicationName, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
                                }
                            },
                            colors = TopAppBarDefaults.smallTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.navbar,
                                titleContentColor = Color.White
                            ),
                            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
                        )
                    }
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        content(application)
                    }
                }
            }
        }
    }
}