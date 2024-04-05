package org.secuso.pfacore.ui.compose.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextOverflow
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.compose.theme.PrivacyFriendlyCoreTheme
import org.secuso.pfacore.ui.compose.theme.navbar

abstract class BaseActivity : ComponentActivity() {

    @Composable
    abstract fun Content(application: PFApplication)

    val title: State<String?> = mutableStateOf(null)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = PFApplication.instance
        setContent {
            PrivacyFriendlyCoreTheme(
                useDarkTheme = isSystemInDarkTheme() && resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES && !application.lightMode
            ) {
                window.statusBarColor = MaterialTheme.colorScheme.navbar.toArgb()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = title.value ?: application.applicationName, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back", tint = Color.White)
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
                        Content(application)
                    }
                }
            }
        }
    }
}