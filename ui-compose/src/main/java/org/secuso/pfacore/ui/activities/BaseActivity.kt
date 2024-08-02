package org.secuso.pfacore.ui.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.theme.navbar

abstract class BaseActivity : AppCompatActivity() {

    @Composable
    abstract fun Content(application: PFApplication)

    val title: State<String?> = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = PFApplication.instance
        setContent {
            WithTheme {
                window.statusBarColor = MaterialTheme.colorScheme.navbar.toArgb()
                Scaffold(
                    topBar = {
                        TopAppBar(title, { finish() })
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