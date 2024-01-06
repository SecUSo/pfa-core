package org.secuso.privacyfriendlycore.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
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
                title = @Composable { _, _, modifier -> Text(text = "Test", modifier = modifier) },
                summary = @Composable { _, _, modifier -> Text(text = "Summary", modifier = modifier) },
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

@Composable
fun SettingsMenu(settings: Settings) {
    LazyColumn(Modifier.fillMaxWidth()) {
        items(count = settings.keys.size, key = { settings.keys.elementAt(it)}) {
            val group = settings.keys.elementAt(it)
            PreferenceGroupHeader(text = group)
            for (setting in settings[group]!!) {
                setting.composable()
            }
        }
    }
}

@Preview
@Composable
fun SettingsMenuPreview() {
    PrivacyFriendlyCoreTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            SettingsMenu(settings = SettingsProvider().values.first())
        }
    }
}