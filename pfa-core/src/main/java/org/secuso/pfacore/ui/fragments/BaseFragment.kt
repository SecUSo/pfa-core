package org.secuso.pfacore.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import org.secuso.pfacore.ui.theme.PrivacyFriendlyCoreTheme

abstract class BaseFragment : Fragment() {

    abstract val layout: Int
    abstract val component: Int

    abstract fun content(): @Composable () -> Unit
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(this.layout, container, false)
        val composeView = view.findViewById<ComposeView>(this.component)

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PrivacyFriendlyCoreTheme(
                    useDarkTheme = isSystemInDarkTheme() && resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()), color = MaterialTheme.colorScheme.background
                    ) {
                        content()
                    }
                }
            }
        }

        return view
    }
}