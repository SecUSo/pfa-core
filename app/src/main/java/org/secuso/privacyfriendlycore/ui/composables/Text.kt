package org.secuso.privacyfriendlycore.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CenterLines(modifier: Modifier = Modifier, sentences: Iterable<String>) {
    Text(text = sentences.joinToString(System.lineSeparator()), modifier = modifier.fillMaxWidth(), textAlign = TextAlign.Center)
}

@Composable
fun CenterText(modifier: Modifier = Modifier, text: String) {
    Text(text = text, modifier = modifier.fillMaxWidth(), textAlign = TextAlign.Center)
}

@Composable
fun SummaryText(modifier: Modifier = Modifier, text: String) {
    Text(text = text, modifier = modifier.fillMaxWidth().padding(top = 4.dp), style = MaterialTheme.typography.bodySmall)
}

@Composable
fun PreferenceGroupHeader(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
        style = MaterialTheme.typography.bodyMedium)
}