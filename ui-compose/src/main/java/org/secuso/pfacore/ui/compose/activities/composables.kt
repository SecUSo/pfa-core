package org.secuso.pfacore.ui.compose.activities

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.model.Theme
import org.secuso.pfacore.ui.compose.theme.PrivacyFriendlyCoreTheme
import org.secuso.pfacore.ui.compose.theme.navbar

@Composable
fun WithTheme(content: @Composable () -> Unit) {
    val application = PFApplication.instance
    val theme = application.data.theme.observeAsState()
    PrivacyFriendlyCoreTheme(
        darkTheme = isSystemInDarkTheme()
                && (LocalConfiguration.current.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
                && (theme.value == Theme.DARK || theme.value == Theme.SYSTEM)
    ) {
        content()
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopAppBar(title: State<String?>, onClick: () -> Unit, icon: (@Composable () -> Unit)? = null) {
    TopAppBar(
        title = { Text(text = title.value ?: PFApplication.instance.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = onClick) {
                if (icon != null) {
                    icon()
                } else {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back", tint = Color.White)
                }
            }
        },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.navbar,
            titleContentColor = Color.White
        ),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
    )
}