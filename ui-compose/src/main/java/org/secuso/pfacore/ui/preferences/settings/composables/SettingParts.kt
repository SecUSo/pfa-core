package org.secuso.pfacore.ui.preferences.settings.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingTitle(text: String, modifier: Modifier) {
    Text(text = text, modifier = modifier, style = MaterialTheme.typography.titleMedium)
}

@Composable
fun SettingSummary(text: String, modifier: Modifier) {
    Text(text = text, modifier = modifier, style = MaterialTheme.typography.bodyMedium)
}