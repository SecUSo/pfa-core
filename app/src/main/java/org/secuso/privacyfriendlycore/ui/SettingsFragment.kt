package org.secuso.privacyfriendlycore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.fragment.app.Fragment
import org.secuso.privacyfriendlycore.R
import org.secuso.privacyfriendlycore.ui.composables.PreferenceGroupHeader
import org.secuso.privacyfriendlycore.ui.settings.SettingData
import org.secuso.privacyfriendlycore.ui.settings.Settings
import org.secuso.privacyfriendlycore.ui.settings.SwitchPreference
import org.secuso.privacyfriendlycore.ui.theme.PrivacyFriendlyCoreTheme

class SettingsProvider: PreviewParameterProvider<Settings> {
    private val state = mutableStateOf(false)
    override val values: Sequence<Settings> = listOf(
        hashMapOf<String, List<SettingData<*>>>("General" to listOf(
            SettingData(
                key = "Test",
                state = state,
                defaultValue = false,
                title = @Composable { _, modifier -> Text(text = "Test", modifier = modifier) },
                summary = @Composable { _, modifier -> Text(text = "Summary", modifier = modifier) },
                enable = state,
                _composable = @Composable { data -> SwitchPreference(
                    data = data,
                    enabled = state,
                    checked = state,
                    update = {})}
            )
        ))
    ).asSequence()
}

class SettingsFragment(private val settings: Settings): Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SettingsMenu(settings = settings)
            }
        }

        return view
    }
}

@Composable
fun SettingGroup(group: String, settings: List<SettingData<*>>) {
    Column(Modifier.fillMaxWidth()) {
        PreferenceGroupHeader(text = group)
        LazyColumn(Modifier.fillMaxWidth()) {
            items(count = settings.size) {
                val setting = settings.elementAt(it)
                setting.composable()
            }
        }
    }
}

@Composable
fun SettingsMenu(settings: Settings) {
    PrivacyFriendlyCoreTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxWidth()) {
                for (key in settings.keys) {
                    SettingGroup(group = key, settings = settings[key]!!)
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingsMenuPreview() {
    SettingsMenu(settings = SettingsProvider().values.first())
}