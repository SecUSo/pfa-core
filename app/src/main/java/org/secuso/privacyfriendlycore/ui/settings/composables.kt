package org.secuso.privacyfriendlycore.ui.settings

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
import org.secuso.privacyfriendlycore.backup.booleanRestorer
import org.secuso.privacyfriendlycore.ui.composables.PreferenceGroupHeader
import org.secuso.privacyfriendlycore.ui.theme.PrivacyFriendlyCoreTheme

class SettingsProvider : PreviewParameterProvider<SettingCategoryMapping> {
    private val state = mutableStateOf(false)
    override val values: Sequence<SettingCategoryMapping> = listOf(
        hashMapOf<String, List<Setting<*>>>("General" to listOf(
            Setting(
                data = SettingData(
                    key = "Test",
                    state = state,
                    defaultValue = false,
                    title = @Composable { _, _, modifier -> Text(text = "Test", modifier = modifier) },
                    summary = @Composable { _, _, modifier -> Text(text = "Summary", modifier = modifier) },
                    enable = state,
                    _composable = @Composable { data ->
                        SwitchPreference(
                            data = data,
                            enabled = state,
                            update = {})
                    }
                ),
                backupable = true,
                restorer = booleanRestorer
            )
        ))
    ).asSequence()
}

@Composable
fun SettingsMenu(settings: SettingCategoryMapping) {
    LazyColumn(Modifier.fillMaxWidth()) {
        items(count = settings.keys.size, key = { settings.keys.elementAt(it) }) {
            val group = settings.keys.elementAt(it)
            PreferenceGroupHeader(text = group)
            for (setting in settings[group]!!) {
                setting.data.composable()
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